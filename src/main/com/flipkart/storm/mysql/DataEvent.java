package com.flipkart.storm.mysql;

import java.util.List;
import java.util.Map;

public class DataEvent {

    private final String                    tableName;
    private final DataEventType             dataEventType;
    private final List<Map<String, Object>> oldData;
    private final List<Map<String, Object>> data;

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
