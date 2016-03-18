/**
 * Copyright 2016 Flipkart Internet Pvt. Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.flipkart.storm.mysql;

import backtype.storm.Config;
import backtype.storm.metric.api.AssignableMetric;
import backtype.storm.metric.api.MeanReducer;
import backtype.storm.metric.api.ReducedMetric;
import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * The MySql Bin Log Spout that emits events from mysql bin logs as a stream.
 */
public class MySqlBinLogSpout extends BaseRichSpout {

    /** The Logger. */
    public static final Logger  LOGGER                     = LoggerFactory.getLogger(MySqlBinLogSpout.class);

    private static final ObjectMapper MAPPER               = new ObjectMapper();
    private long                msgAckCount                = 0;
    private long                msgSidelineCount           = 0;
    private long                msgFailedCount             = 0;
    private long                currentCommittedOffsetInZk = -1;
    private BinLogPosition      lastEmittedBeginTxPosition = null;
    private String              databaseName               = null;

    private final MySqlSpoutConfig  spoutConfig;
    private String                  topologyInstanceId;
    private String                  topologyName;
    private ZkClient                zkClient;
    private MySqlClient             mySqlClient;
    private OpenReplicatorManager   replicatorManager;
    private SpoutOutputCollector    collector;
    private long                    zkLastUpdateMs;

    private SortedMap<Long, Long>                   failureMessages           = new TreeMap<Long, Long>();
    private LinkedBlockingQueue<TransactionEvent>   txQueue                   = new LinkedBlockingQueue<TransactionEvent>();
    private SortedMap<Long, RetryTransactionEvent>  pendingMessagesToBeAcked  = new TreeMap<Long, RetryTransactionEvent>();

    private AssignableMetric failureCountMetric;
    private AssignableMetric sidelineCountMetric;
    private AssignableMetric successCountMetric;
    private ReducedMetric    txEventProcessTime;

    /**
     * Initialize the MySql Spout.
     *
     * @param spoutConfig MySql + Zookeeper Configuration
     */
    public MySqlBinLogSpout(MySqlSpoutConfig spoutConfig) {
        this.spoutConfig = spoutConfig;
    }

    @Override
    public void open(Map conf, final TopologyContext context, final SpoutOutputCollector spoutOutputCollector) {

        Preconditions.checkNotNull(this.spoutConfig.getZkBinLogStateConfig(),
                "Zookeeper Config cannot be null");

        Preconditions.checkNotNull(this.spoutConfig.getMysqlConfig(),
                "Mysql Config cannot be null");

        this.collector          = spoutOutputCollector;
        this.topologyInstanceId = context.getStormId();
        this.topologyName       = conf.get(Config.TOPOLOGY_NAME).toString();

        List<String> zkServers = this.spoutConfig.getZkBinLogStateConfig().getZkServers();
        if (zkServers == null) {
            zkServers = (List<String>) conf.get(Config.STORM_ZOOKEEPER_SERVERS);
        }

        Integer zkPort = this.spoutConfig.getZkBinLogStateConfig().getZkPort();
        if (zkPort == null) {
            zkPort = ((Number) conf.get(Config.STORM_ZOOKEEPER_PORT)).intValue();
        }

        Integer zkSessionTimeout = this.spoutConfig.getZkBinLogStateConfig().getZkSessionTimeoutInMs();
        if (zkSessionTimeout == null) {
            zkSessionTimeout = ((Number) conf.get(Config.STORM_ZOOKEEPER_SESSION_TIMEOUT)).intValue();
        }

        Integer zkConnectionTimeout = this.spoutConfig.getZkBinLogStateConfig().getZkConnectionTimeoutInMs();
        if (zkConnectionTimeout == null) {
            zkConnectionTimeout = ((Number) conf.get(Config.STORM_ZOOKEEPER_CONNECTION_TIMEOUT)).intValue();
        }

        Integer retryTimes = this.spoutConfig.getZkBinLogStateConfig().getZkRetryTimes();
        if (retryTimes == null) {
            retryTimes = ((Number) conf.get(Config.STORM_ZOOKEEPER_RETRY_TIMES)).intValue();
        }

        Integer sleepMsBetweenRetries = this.spoutConfig.getZkBinLogStateConfig().getZkSleepMsBetweenRetries();
        if (sleepMsBetweenRetries == null) {
            sleepMsBetweenRetries = ((Number) conf.get(Config.STORM_ZOOKEEPER_RETRY_INTERVAL)).intValue();
        }

        this.databaseName = this.spoutConfig.getMysqlConfig().getDatabase();

        initializeAndRegisterAllMetrics(context);
        zkClient = new ZkClient(zkServers, zkPort, zkSessionTimeout,
                    zkConnectionTimeout, retryTimes, sleepMsBetweenRetries);

        mySqlClient = new MySqlClient(new MySqlConnectionFactory(this.spoutConfig.getMysqlConfig()));

        replicatorManager = new OpenReplicatorManager(mySqlClient, zkClient);
        this.lastEmittedBeginTxPosition = replicatorManager.initialize(this.spoutConfig.getMysqlConfig(),
                                     this.spoutConfig.getZkBinLogStateConfig(),
                                     this.txQueue);
        replicatorManager.startReplication();
    }

    @Override
    public void close() {
        zkClient.close();
        mySqlClient.close();
        replicatorManager.close();
    }

    @Override
    public void nextTuple() {
        RetryTransactionEvent txRetrEvent = null;
        if (this.failureMessages.isEmpty()) {

            TransactionEvent txEvent = this.txQueue.poll();
            if (txEvent != null) {
                txRetrEvent = new RetryTransactionEvent(txEvent, 1);
            }
        } else {

            long failedScn = this.failureMessages.firstKey();
            txRetrEvent = this.pendingMessagesToBeAcked.get(failedScn);
            if (txRetrEvent != null) {
                if (txRetrEvent.getNumRetries() >= this.spoutConfig.getFailureConfig().getNumMaxRetries()) {
                        this.spoutConfig.getFailureConfig().getSidelineStrategy().sideline(txRetrEvent.getTxEvent());
                        this.failureMessages.remove(failedScn);
                        this.pendingMessagesToBeAcked.remove(failedScn);
                        this.msgSidelineCount++;
                        this.sidelineCountMetric.setValue(this.msgSidelineCount);
                        LOGGER.info("Sidelining message id .... {}", failedScn);
                        txRetrEvent = null;
                } else {
                    txRetrEvent = new RetryTransactionEvent(txRetrEvent.getTxEvent(), txRetrEvent.getNumRetries() + 1);
                }
            } else {
                //Nothing was pending it seems... Remove from failure
                this.failureMessages.remove(failedScn);
            }
        }

        try {
        if (txRetrEvent != null) {
            TransactionEvent txEvent = txRetrEvent.getTxEvent();
            this.txEventProcessTime.update(txEvent.getEndTimeInNanos() - txEvent.getStartTimeInNanos());
                String txJson = MAPPER.writeValueAsString(txEvent);
                BinLogPosition binLogPosition = new BinLogPosition(txEvent.getStartBinLogPosition(),
                                                                   txEvent.getStartBinLogFileName());
            long scn = binLogPosition.getSCN();
            this.pendingMessagesToBeAcked.put(scn, txRetrEvent);
            this.lastEmittedBeginTxPosition = binLogPosition;
            collector.emit(new Values(txEvent.getDatabaseName(), txJson), scn);
        }

        long diffWithNow = System.currentTimeMillis() - zkLastUpdateMs;
        if (diffWithNow > this.spoutConfig.getZkBinLogStateConfig().getZkScnUpdateRateInMs() || diffWithNow < 0) {
            commit();
        }
        } catch (Exception ex) {
            LOGGER.error("Error occurred in processing event {}:", txRetrEvent);
        }

    }

    @Override
    public void ack(Object msgId) {
        LOGGER.info("Acking For... {}", msgId);
        long scn = (Long) msgId;
        this.pendingMessagesToBeAcked.remove(scn);
        this.failureMessages.remove(scn);
        this.msgAckCount++;
        this.successCountMetric.setValue(this.msgAckCount);
    }

    @Override
    public void fail(Object msgId) {
        LOGGER.info("Failing For... {}", msgId);
        int numFailures = this.failureMessages.size();
        if (numFailures >= this.spoutConfig.getFailureConfig().getNumMaxTotalFailAllowed()) {
            throw new RuntimeException("Failure count greater than configured allowed failures...Stopping");
        }
        long scn = (Long) msgId;
        this.failureMessages.put(scn, System.currentTimeMillis());
        this.msgFailedCount++;
        this.failureCountMetric.setValue(this.msgFailedCount);
    }

    @Override
    public void deactivate() {
        commit();
    }

    /**
     * Fields emitted by the spout.
     *
     * @param declarer the fields declarer
     */
    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("databaseName", "txEvent"));
    }

    /**
     * Updates zookeeper with the bin log position/offset.
     */
    public void commit() {
        long offset = (pendingMessagesToBeAcked.isEmpty()) ? this.lastEmittedBeginTxPosition.getSCN() :
                                                             pendingMessagesToBeAcked.firstKey();
        if (currentCommittedOffsetInZk != offset) {
            LOGGER.debug("Updating ZK with offset {} for topology: {} with Id: {}",
                                                    offset, this.topologyName, this.topologyInstanceId);
            OffsetInfo offsetInfo = null;
            if (pendingMessagesToBeAcked.isEmpty()) {
                offsetInfo = new OffsetInfo(offset,
                                            this.topologyName,
                                            this.topologyInstanceId,
                                            this.databaseName,
                                            this.lastEmittedBeginTxPosition.getBinLogPosition(),
                                            this.lastEmittedBeginTxPosition.getBinLogFileName());
            } else {
                TransactionEvent txEvent = pendingMessagesToBeAcked.get(offset).getTxEvent();
                offsetInfo = new OffsetInfo(offset,
                                            this.topologyName,
                                            this.topologyInstanceId,
                                            txEvent.getDatabaseName(),
                                            txEvent.getStartBinLogPosition(),
                                            txEvent.getStartBinLogFileName());
            }

            zkClient.write(this.spoutConfig.getZkBinLogStateConfig().getZkScnCommitPath(), offsetInfo);
            zkLastUpdateMs = System.currentTimeMillis();
            currentCommittedOffsetInZk = offset;
            LOGGER.debug("Update Complete in ZK with offset {} for topology: {} with Id: {}",
                                                            offset, topologyName, topologyInstanceId);
        } else {
            LOGGER.debug("No update in ZK for offset {}", offset);
        }
    }

    private void initializeAndRegisterAllMetrics(TopologyContext context) {
        this.failureCountMetric     = new AssignableMetric(this.msgFailedCount);
        this.successCountMetric     = new AssignableMetric(this.msgAckCount);
        this.sidelineCountMetric    = new AssignableMetric(this.msgSidelineCount);
        this.txEventProcessTime     = new ReducedMetric(new MeanReducer());

        context.registerMetric(SpoutConstants.METRIC_FAILURECOUNT, this.failureCountMetric,
                               SpoutConstants.DEFAULT_TIMEBUCKETSIZEINSECS);
        context.registerMetric(SpoutConstants.METRIC_SUCCESSCOUNT, this.successCountMetric,
                               SpoutConstants.DEFAULT_TIMEBUCKETSIZEINSECS);
        context.registerMetric(SpoutConstants.METRIC_SIDELINECOUNT, this.sidelineCountMetric,
                               SpoutConstants.DEFAULT_TIMEBUCKETSIZEINSECS);
        context.registerMetric(SpoutConstants.METRIC_TXPROCESSTIME, this.txEventProcessTime,
                               SpoutConstants.DEFAULT_TIMEBUCKETSIZEINSECS);
    }
}









