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
import com.google.common.base.Preconditions;

import java.util.List;
import java.util.Map;

/**
 * This factory is responsible for creating the all external clients. (Zk/Mysql)
 * It has been structured in such a way, so as to make it easy for testing.
 * This class/methods are not static so as to make it testable via mockito. Will think
 * about using PowerMock or something similar later.
 */
public class ClientFactory {

    /**
     * Get the zookeeper client.
     *
     * @param stormConf the storm configuration
     * @param zkBinLogStateConfig the user zk configuration that overrides the storm zk configuration
     * @return the zk client
     */
    public ZkClient getZkClient(Map stormConf, ZkBinLogStateConfig zkBinLogStateConfig) {

        List<String> zkServers = zkBinLogStateConfig.getZkServers();
        if (zkServers == null) {
            zkServers = (List<String>) stormConf.get(Config.STORM_ZOOKEEPER_SERVERS);
        }

        Integer zkPort = zkBinLogStateConfig.getZkPort();
        if (zkPort == null) {
            zkPort = ((Number) stormConf.get(Config.STORM_ZOOKEEPER_PORT)).intValue();
        }

        Integer zkSessionTimeout = zkBinLogStateConfig.getZkSessionTimeoutInMs();
        if (zkSessionTimeout == null) {
            zkSessionTimeout = ((Number) stormConf.get(Config.STORM_ZOOKEEPER_SESSION_TIMEOUT)).intValue();
        }

        Integer zkConnectionTimeout = zkBinLogStateConfig.getZkConnectionTimeoutInMs();
        if (zkConnectionTimeout == null) {
            zkConnectionTimeout = ((Number) stormConf.get(Config.STORM_ZOOKEEPER_CONNECTION_TIMEOUT)).intValue();
        }

        Integer retryTimes = zkBinLogStateConfig.getZkRetryTimes();
        if (retryTimes == null) {
            retryTimes = ((Number) stormConf.get(Config.STORM_ZOOKEEPER_RETRY_TIMES)).intValue();
        }

        Integer sleepMsBetweenRetries = zkBinLogStateConfig.getZkSleepMsBetweenRetries();
        if (sleepMsBetweenRetries == null) {
            sleepMsBetweenRetries = ((Number) stormConf.get(Config.STORM_ZOOKEEPER_RETRY_INTERVAL)).intValue();
        }

        return new ZkClient(zkServers, zkPort,
                            zkSessionTimeout, zkConnectionTimeout,
                            retryTimes, sleepMsBetweenRetries);

    }

    /**
     * Get the mysql client.
     * @param mySqlConfig the mysql configuration
     * @return the mysql client
     */
    public MySqlClient getMySqlClient(MySqlConfig mySqlConfig) {
        return new MySqlClient(new MySqlConnectionFactory(mySqlConfig));
    }

    /**
     * Get the Open Replicator Client.
     *
     * @param mySqlClient the mysql client
     * @param zkClient the zookeeper client
     * @return the open replicator client
     */
    public OpenReplicatorClient getReplicatorClient(MySqlClient mySqlClient, ZkClient zkClient) {
        Preconditions.checkNotNull(mySqlClient, "Mysql Client cannot be null");
        Preconditions.checkNotNull(zkClient, "Zookeeper Client cannot be null");
        return new OpenReplicatorClient(mySqlClient, zkClient);
    }
}
