package com.flipkart.storm.mysql;

import java.io.Serializable;

public class MySqlSpoutConfig implements Serializable {

    private final MySqlConfig           mysqlConfig;
    private final ZkBinLogStateConfig   zkBinLogStateConfig;
    private final FailureConfig         failureConfig;

    public MySqlSpoutConfig(MySqlConfig mysqlConfig, ZkBinLogStateConfig zkBinLogStateConfig) {
        this (mysqlConfig, zkBinLogStateConfig,
              new FailureConfig(SpoutConstants.DEFAULT_NUMMAXRETRIES, SpoutConstants.DEFAULT_NUMMAXTOTFAILALLOWED));
    }

    public MySqlSpoutConfig(MySqlConfig mysqlConfig, ZkBinLogStateConfig zkBinLogStateConfig,
                            FailureConfig failureConfig) {
        this.mysqlConfig = mysqlConfig;
        this.zkBinLogStateConfig = zkBinLogStateConfig;
        this.failureConfig = failureConfig;
    }

    public MySqlConfig getMysqlConfig() {
        return mysqlConfig;
    }

    public ZkBinLogStateConfig getZkBinLogStateConfig() {
        return zkBinLogStateConfig;
    }

    public FailureConfig getFailureConfig() {
        return failureConfig;
    }
}
