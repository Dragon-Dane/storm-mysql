package com.flipkart.storm.mysql;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

public class ZkBinLogStateConfig implements Serializable {

    private final List<String>  zkServers;
    private final int           zkPort;
    private final String        zkRoot;
    private final String        zkSpoutId;
    private final String        zkScnCommitPath;
    private final int           zkScnUpdateRateInMs;
    private final Integer       zkSessionTimeoutInMs;
    private final Integer       zkConnectionTimeoutInMs;
    private final Integer       zkRetryTimes;
    private final Integer       zkSleepMsBetweenRetries;
    private final boolean       zkIgnoreBinLogPosition;

    private ZkBinLogStateConfig(Builder builder) {
        this.zkServers                  = builder.innerZkServers;
        this.zkPort                     = builder.innerZkPort;
        this.zkRoot                     = builder.innerZkRoot;
        this.zkSpoutId                  = builder.innerZkSpoutId;
        this.zkScnCommitPath            = SpoutConstants.ZK_SEPARATOR +
                                          this.zkRoot + SpoutConstants.ZK_SEPARATOR +
                                          this.zkSpoutId;
        this.zkScnUpdateRateInMs        = builder.innerZkScnUpdateRateInMs;
        this.zkSessionTimeoutInMs       = builder.innerZkSessionTimeoutInMs;
        this.zkConnectionTimeoutInMs    = builder.innerZkConnectionTimeoutInMs;
        this.zkRetryTimes               = builder.innerZkRetryTimes;
        this.zkSleepMsBetweenRetries    = builder.innerZkSleepMsBetweenRetries;
        this.zkIgnoreBinLogPosition     = builder.innerZkIgnoreBinLogPosition;
    }

    public List<String> getZkServers() {
        return zkServers;
    }

    public int getZkPort() {
        return zkPort;
    }

    public String getZkRoot() {
        return zkRoot;
    }

    public String getZkSpoutId() {
        return zkSpoutId;
    }

    public String getZkScnCommitPath() {
        return zkScnCommitPath;
    }

    public int getZkScnUpdateRateInMs() {
        return zkScnUpdateRateInMs;
    }

    public Integer getZkSessionTimeoutInMs() {
        return zkSessionTimeoutInMs;
    }

    public Integer getZkConnectionTimeoutInMs() {
        return zkConnectionTimeoutInMs;
    }

    public Integer getZkRetryTimes() {
        return zkRetryTimes;
    }

    public Integer getZkSleepMsBetweenRetries() {
        return zkSleepMsBetweenRetries;
    }

    public boolean isZkIgnoreBinLogPosition() {
        return zkIgnoreBinLogPosition;
    }

    public static class Builder {

        private final String innerZkSpoutId;

        private List<String>    innerZkServers                  = Collections.singletonList("localhost");
        private int             innerZkPort                     = SpoutConstants.DEFAULT_ZKPORT;
        private String          innerZkRoot                     = SpoutConstants.DEFAULT_ZKROOT;
        private int             innerZkScnUpdateRateInMs        = SpoutConstants.DEFAULT_ZK_UPDATE_RATE_MS;
        private Integer         innerZkSessionTimeoutInMs       = null;
        private Integer         innerZkConnectionTimeoutInMs    = null;
        private Integer         innerZkRetryTimes               = null;
        private Integer         innerZkSleepMsBetweenRetries    = null;
        private boolean         innerZkIgnoreBinLogPosition     = false;

        public Builder (String zkSpoutId) {
            this.innerZkSpoutId = zkSpoutId;
        }

        public Builder servers(List<String> zkServers) {
            this.innerZkServers = zkServers;
            return this;
        }

        public Builder port(int port) {
            this.innerZkPort = port;
            return this;
        }

        public Builder root(String zkRoot) {
            this.innerZkRoot = zkRoot;
            return this;
        }

        public Builder updateRate(int updateRate) {
            this.innerZkScnUpdateRateInMs = updateRate;
            return this;
        }

        public Builder sessionTimeOutInMs(int timeOut) {
            this.innerZkSessionTimeoutInMs = timeOut;
            return this;
        }

        public Builder connectionTimeOutInMs(int timeOut) {
            this.innerZkConnectionTimeoutInMs = timeOut;
            return this;
        }

        public Builder retryTimes(int retryTimes) {
            this.innerZkRetryTimes = retryTimes;
            return this;
        }

        public Builder sleepMsBetweenRetries(int sleepMs) {
            this.innerZkSleepMsBetweenRetries = sleepMs;
            return this;
        }

        public Builder ignoreZkBinLogPosition(boolean ignore) {
            this.innerZkIgnoreBinLogPosition = ignore;
            return this;
        }

        public ZkBinLogStateConfig build() {
            ZkBinLogStateConfig zkBinLogStateConfig =  new ZkBinLogStateConfig(this);
            return zkBinLogStateConfig;
        }

    }
}
