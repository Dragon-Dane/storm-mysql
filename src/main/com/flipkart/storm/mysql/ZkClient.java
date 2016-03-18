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

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;
import org.apache.zookeeper.CreateMode;
import org.json.simple.JSONValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.util.List;

/**
 * A wrapper around connecting to Zookeeper.
 */
public class ZkClient {

    private static final Logger LOGGER              = LoggerFactory.getLogger(ZkClient.class);
    private static final String DEFAULT_CHARSET     = "UTF-8";
    private CuratorFramework client;

    /**
     * Instantiating the zookeeper client.
     *
     * @param servers list of servers to connect to
     * @param port the port on which to connect
     * @param sessionTimeoutMs the session timeout
     * @param connectionTimeoutMs the connection timeout
     * @param retryTimes number of retries to zookeeper
     * @param sleepMsBetweenRetries time to sleep between retries
     */
    public ZkClient(List<String> servers, int port,
                    int sessionTimeoutMs, int connectionTimeoutMs,
                    int retryTimes, int sleepMsBetweenRetries) {
        try {
                client = CuratorFrameworkFactory.newClient(getZkServerPorts(servers, port),
                                                           sessionTimeoutMs,
                                                           connectionTimeoutMs,
                                                           new RetryNTimes(retryTimes, sleepMsBetweenRetries));

                client.start();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
    }

    /**
     * Write at zookeeper path.
     *
     * @param path zkNode Path at which to write to payload
     * @param payload the actual data
     * @param <T> payload type
     */
    public <T> void write(String path, T payload) {
        String data = JSONValue.toJSONString(payload);
        LOGGER.debug("Writing to Zookeeper Path {} Payload {}", path, data);
        writeInternal(path, data.getBytes(Charset.forName(DEFAULT_CHARSET)));
    }

    /**
     * Read from zookeeper path.
     *
     * @param path zkNode Path at which to read from
     * @param <T> type of data
     * @return data object that was read
     */
    public <T> T read(String path) {
        try {
            byte[] bytes = readInternal(path);
            if (bytes == null) {
                return null;
            }
            return (T) JSONValue.parse(new String(bytes, DEFAULT_CHARSET));
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

    private void writeInternal(String path, byte[] payload) {
        try {
            if (client.checkExists().forPath(path) == null) {
                client.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(path, payload);
            } else {
                client.setData().forPath(path, payload);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private byte[] readInternal(String path) {
        try {
            if (client.checkExists().forPath(path) != null) {
                return client.getData().forPath(path);
            } else {
                return null;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
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
