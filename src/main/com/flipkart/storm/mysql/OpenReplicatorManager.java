package com.flipkart.storm.mysql;

import com.flipkart.storm.mysql.schema.DatabaseInfo;
import com.google.code.or.OpenReplicator;
import org.apache.storm.guava.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.SQLException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class OpenReplicatorManager {

    public static final Logger LOGGER = LoggerFactory.getLogger(OpenReplicatorManager.class);

    private OpenReplicator      openReplicator;
    private final ZkClient      zkClient;
    private final MySqlClient   mySqlClient;

    public OpenReplicatorManager(MySqlClient mysqlClient, ZkClient client) {
        this.zkClient = client;
        this.mySqlClient = mysqlClient;
    }

    public BinLogPosition initialize(MySqlConfig mySqlConfig,
                                    ZkBinLogStateConfig zkConfig,
                                    LinkedBlockingQueue<TransactionEvent> txEventQueue) {
        this.openReplicator = new OpenReplicator();
        this.openReplicator.setUser(mySqlConfig.getUser());
        this.openReplicator.setPassword(mySqlConfig.getPassword());
        this.openReplicator.setServerId(mySqlConfig.getServerId());
        this.openReplicator.setPort(mySqlConfig.getPort());

        BinLogPosition binLogPosition = getBinLogPositionToStartFrom(mySqlConfig, zkConfig);
        this.openReplicator.setBinlogPosition(binLogPosition.getBinLogPosition());
        this.openReplicator.setBinlogFileName(binLogPosition.getBinLogFileName());
        this.openReplicator.setBinlogEventListener(new SpoutBinLogEventListener(txEventQueue, getSchema(mySqlConfig)));
        return binLogPosition;
    }

    public void startReplication() {
        try {
            this.openReplicator.start();
        } catch (Exception ex) {
            throw new RuntimeException("Error initializing the MySQL replicator...", ex);
        }
    }

    private BinLogPosition getBinLogPositionToStartFrom(MySqlConfig mysqlConfig, ZkBinLogStateConfig zkConfig) {
        try {
            if (zkConfig.isZkIgnoreBinLogPosition()) {
                LOGGER.info("Ignoring Zookeeper state because ignoreZkBingLogPosition set to true...");
                BinLogPosition binLogPosition = getBinLogPosition(mysqlConfig);
                LOGGER.info("Starting from BinLogFile {} and BinLogPosition {}", binLogPosition.getBinLogFileName(), binLogPosition.getBinLogPosition());
                return binLogPosition;
            } else {
                OffsetInfo offsetInfo = getDetailsFromZK(zkConfig.getZkScnCommitPath());
                if (offsetInfo == null) {
                    LOGGER.info("No Information of offsets found in zookeeper, trying from MySQL...");
                    BinLogPosition binLogPosition = getBinLogPosition(mysqlConfig);
                    LOGGER.info("Starting from BinLogFile {} and BinLogPosition {}", binLogPosition.getBinLogFileName(), binLogPosition.getBinLogPosition());
                    return binLogPosition;
                } else {
                    LOGGER.info("Offset Information found in Zookeeper. Starting from BinLogFile {} BinLogPosition {}", offsetInfo.getBinLogFileName(), offsetInfo.getBinLogPosition());
                    return new BinLogPosition(offsetInfo.getBinLogPosition(), offsetInfo.getBinLogFileName());
                }
            }
        } catch (Exception ex) {
                throw new RuntimeException("Could not get starting offset to read from", ex);
        }
    }

    private BinLogPosition getBinLogPosition(MySqlConfig mysqlConfig) throws SQLException {
        if (Strings.isNullOrEmpty(mysqlConfig.getBinLogFileName())) {
            return this.mySqlClient.getBinLogDetails();
        } else return new BinLogPosition(mysqlConfig.getBinLogPosition(), mysqlConfig.getBinLogFileName());
    }

    private DatabaseInfo getSchema(MySqlConfig mySqlConfig) {
        try {
            return this.mySqlClient.getDatabaseSchema(mySqlConfig.getDatabase(), mySqlConfig.getTables());
        } catch (SQLException ex) {
            throw new RuntimeException("Error reading schema information from MySQL...", ex);
        }
    }


    private OffsetInfo getDetailsFromZK(String path) {
        OffsetInfo zkOffsetInfo = null;
        try {
            zkOffsetInfo = zkClient.read(path);
            LOGGER.info("Read information from: Path {} Offset Info {}", path, zkOffsetInfo );
        } catch (Throwable e) {
            LOGGER.warn("Error reading from ZkNode: {} {}", path, e);
        }
        return zkOffsetInfo;
    }

    public void close() {
        try {
            openReplicator.stop(1, TimeUnit.SECONDS);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
