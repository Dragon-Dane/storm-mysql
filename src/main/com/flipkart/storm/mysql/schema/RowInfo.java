package com.flipkart.storm.mysql.schema;

import java.util.List;

public class RowInfo {

    private final List<ColumnInfo> columnInfoList;

    public RowInfo(List<ColumnInfo> columnInfoList) {
        this.columnInfoList = columnInfoList;
    }

    public List<ColumnInfo> getColumnInfo() {
        return columnInfoList;
    }
}
