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

import java.util.List;
import java.util.Map;

/**
 * The actual data event in the bin logs.
 */
public class DataEvent {

    private final String                    tableName;
    private final DataEventType             dataEventType;
    private final List<Map<String, Object>> oldData;
    private final List<Map<String, Object>> data;

    /**
     * Create the data event.
     *
     * @param tableName the name of the table for which the bin log event was raised
     * @param dataEventType the event type
     * @param oldData this is the data that was edited, only applicable in the case of updates.
     * @param data data that was changed.
     */
    public DataEvent(String tableName,
                     DataEventType dataEventType,
                     List<Map<String, Object>> oldData,
                     List<Map<String, Object>> data) {
        this.tableName      = tableName;
        this.dataEventType  = dataEventType;
        this.oldData        = oldData;
        this.data           = data;
    }

    public String getTableName() {
        return tableName;
    }

    public DataEventType getDataEventType() {
        return dataEventType;
    }

    public List<Map<String, Object>> getOldData() {
        return oldData;
    }

    public List<Map<String, Object>> getData() {
        return data;
    }

    @Override
    public String toString() {
        return "DataEvent{" +
                "tableName='" + tableName + '\'' +
                ", dataEventType=" + dataEventType +
                ", oldData=" + oldData +
                ", data=" + data +
                '}';
    }
}
