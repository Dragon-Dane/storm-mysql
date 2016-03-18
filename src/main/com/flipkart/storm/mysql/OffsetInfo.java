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

/**
 * Information about bin log offsets stored in zookeeper.
 */
public class OffsetInfo {

    private final long      scn;
    private final String    topologyName;
    private final String    topologyInstanceId;
    private final String    databaseName;
    private final int       binLogPosition;
    private final String    binLogFileName;

    /**
     * Initialization of offset info.
     * @param scn the source control number, a combination of bin log filename and position.
     * @param topologyName the name of the storm topology.
     * @param topologyInstanceId the topology instance id.
     * @param databaseName the database name.
     * @param binLogPosition the bin log position.
     * @param binLogFileName the bin log filename.
     */
    public OffsetInfo(long scn,
                      String topologyName,
                      String topologyInstanceId,
                      String databaseName,
                      int binLogPosition,
                      String binLogFileName) {
        this.scn = scn;
        this.topologyName = topologyName;
        this.topologyInstanceId = topologyInstanceId;
        this.databaseName = databaseName;
        this.binLogPosition = binLogPosition;
        this.binLogFileName = binLogFileName;
    }


    public int getBinLogPosition() {
        return binLogPosition;
    }

    public String getBinLogFileName() {
        return binLogFileName;
    }
}
