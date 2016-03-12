package com.flipkart.storm.mysql;

import java.util.List;
import java.util.Map;

public class DataEvent {

    private final String                    tableName;
    private final DataEventType             dataEventType;
    private final List<Map<String, Object>> oldData;
    private final List<Map<String, Object>> newData;

    public DataEvent(String tableName,
                     DataEventType dataEventType,
                     List<Map<String, Object>> oldData,
                     List<Map<String, Object>> newData) {
        this.tableName      = tableName;
        this.dataEventType  = dataEventType;
        this.oldData        = oldData;
        this.newData        = newData;
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

    public List<Map<String, Object>> getNewData() {
        return newData;
    }

    @Override
    public String toString() {
        return "DataEvent{" +
                "tableName='" + tableName + '\'' +
                ", dataEventType=" + dataEventType +
                ", oldData=" + oldData +
                ", newData=" + newData +
                '}';
    }
}
