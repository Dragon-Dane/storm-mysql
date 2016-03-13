package com.flipkart.storm.mysql.schema;

public class TableInfo {

    private final String    tableName;
    private final RowInfo   rowInfo;

    public TableInfo(String tableName, RowInfo rowInfo) {
        this.tableName  = tableName;
        this.rowInfo    = rowInfo;
    }

    public RowInfo getRowInfo() {
        return rowInfo;
    }

    @Override
    public String toString() {
        return "TableInfo{" +
                "tableName='" + tableName + '\'' +
                ", rowInfo=" + rowInfo +
                '}';
    }
}
