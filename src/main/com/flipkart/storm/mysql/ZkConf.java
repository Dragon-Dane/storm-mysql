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


import org.apache.storm.Config;

import java.util.List;
import java.util.Map;

/**
 * Responsible for keeping the Zookeeper Configuration that is in
 * use by the zookeeper client.
 */
public class ZkConf {

    private List<String> zkServers;
    private Integer zkPort;
    private Integer zkSessionTimeout;
    private Integer zkConnectionTimeout;
    private Integer retryTimes;
    private Integer sleepMsBetweenRetries;

    /**
     * Create the configuration.
     *
     * @param stormConf Storm Configuration.
     * @param zkBinLogStateConfig User provided zookeeper configuration.
     */
    public ZkConf(Map stormConf, ZkBinLogStateConfig zkBinLogStateConfig) {
        this.zkServers = zkBinLogStateConfig.getZkServers();
        if (this.zkServers == null) {
            this.zkServers = (List<String>) stormConf.get(Config.STORM_ZOOKEEPER_SERVERS);
        }

        this.zkPort = zkBinLogStateConfig.getZkPort();
        if (this.zkPort == null) {
            this.zkPort = ((Number) stormConf.get(Config.STORM_ZOOKEEPER_PORT)).intValue();
        }

        this.zkSessionTimeout = zkBinLogStateConfig.getZkSessionTimeoutInMs();
        if (this.zkSessionTimeout == null) {
            this.zkSessionTimeout = ((Number) stormConf.get(Config.STORM_ZOOKEEPER_SESSION_TIMEOUT)).intValue();
        }

        this.zkConnectionTimeout = zkBinLogStateConfig.getZkConnectionTimeoutInMs();
        if (this.zkConnectionTimeout == null) {
            this.zkConnectionTimeout = ((Number) stormConf.get(Config.STORM_ZOOKEEPER_CONNECTION_TIMEOUT)).intValue();
        }

        this.retryTimes = zkBinLogStateConfig.getZkRetryTimes();
        if (this.retryTimes == null) {
            this.retryTimes = ((Number) stormConf.get(Config.STORM_ZOOKEEPER_RETRY_TIMES)).intValue();
        }

        this.sleepMsBetweenRetries = zkBinLogStateConfig.getZkSleepMsBetweenRetries();
        if (this.sleepMsBetweenRetries == null) {
            this.sleepMsBetweenRetries = ((Number) stormConf.get(Config.STORM_ZOOKEEPER_RETRY_INTERVAL)).intValue();
        }
    }


    public List<String> getZkServers() {
        return zkServers;
    }

    public Integer getZkPort() {
        return zkPort;
    }

    public Integer getZkSessionTimeout() {
        return zkSessionTimeout;
    }

    public Integer getZkConnectionTimeout() {
        return zkConnectionTimeout;
    }

    public Integer getRetryTimes() {
        return retryTimes;
    }

    public Integer getSleepMsBetweenRetries() {
        return sleepMsBetweenRetries;
    }
}
