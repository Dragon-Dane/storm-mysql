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

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;
import org.apache.zookeeper.CreateMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.util.List;

/**
 * A wrapper around connecting to Zookeeper.
 */
public class ZkClient {

    private static final Logger         LOGGER              = LoggerFactory.getLogger(ZkClient.class);
    private static final ObjectMapper   MAPPER              = new ObjectMapper();
    private static final String         DEFAULT_CHARSET     = "UTF-8";
    private CuratorFramework client;

    /**
     * Instantiating the zookeeper client.
     *
     * @param zkConf The Zookeeper Configuration.
     */
    public ZkClient(ZkConf zkConf) {

        client = CuratorFrameworkFactory.newClient(getZkServerPorts(zkConf.getZkServers(),
                                                    zkConf.getZkPort()),
                                                    zkConf.getZkSessionTimeout(),
                                                    zkConf.getZkConnectionTimeout(),
                                                    new RetryNTimes(zkConf.getRetryTimes(),
                                                    zkConf.getSleepMsBetweenRetries()));
    }


    /**
     * Write at zookeeper path.
     *
     * @param path zkNode Path at which to write to payload
     * @param offsetInfo the offset info
     * @throws ZkException
     */
    public void write(String path, OffsetInfo offsetInfo) throws ZkException {
        try {
            String data = MAPPER.writeValueAsString(offsetInfo);
            LOGGER.debug("Writing to Zookeeper Path {} OffsetInfo {}", path, data);
            writeInternal(path, data.getBytes(Charset.forName(DEFAULT_CHARSET)));
        } catch (Exception ex) {
            LOGGER.error("Error writing to Zookeeper..Path {} Payload {}", path, offsetInfo);
            throw new ZkException("Error writing to Zookeeper..Path: " + path + " OffsetInfo: " + offsetInfo, ex);
        }
    }

    /**
     * Read from zookeeper path.
     *
     * @param path zkNode Path at which to read from
     * @return the offset info
     * @throws ZkException
     */
   public OffsetInfo read(String path) throws ZkException {
        try {
            byte[] bytes = readInternal(path);
            if (bytes == null) {
                return null;
            }
            String data = new String(bytes, DEFAULT_CHARSET);
            LOGGER.debug("Reading from Zookeeper Path {} Payload {}", path, data);
            OffsetInfo offsetInfo = MAPPER.readValue(data, OffsetInfo.class);
            return offsetInfo;
        } catch (Exception ex) {
            LOGGER.error("Error while reading from Zk Path..{} Exception {}", path, ex.getMessage());
            throw new ZkException("Error reading from Zookeeper..Path: " + path, ex);
        }
    }

    /**
     * Start the zookeeper client.
     */
    public void start() {
        try {
            client.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Close the zookeeper client.
     */
    public void close() {
        client.close();
        client = null;
    }

    private void writeInternal(String path, byte[] payload) throws Exception {
        if (client.checkExists().forPath(path) == null) {
            client.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(path, payload);
        } else {
            client.setData().forPath(path, payload);
        }
    }

    private byte[] readInternal(String path) throws Exception {
        if (client.checkExists().forPath(path) != null) {
            return client.getData().forPath(path);
        } else {
            return null;
        }
    }

    private String getZkServerPorts(List<String> servers, int port) {
        String serverPorts = "";
        for (String server : servers) {
            serverPorts = serverPorts + server + ":" + port + ",";
        }
        return serverPorts;
    }
}
