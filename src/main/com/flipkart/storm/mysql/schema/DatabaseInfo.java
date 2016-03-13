package com.flipkart.storm.mysql.schema;

import java.util.Map;
import java.util.Set;

public class DatabaseInfo {

    private String                  databaseName;
    private Map<String, TableInfo>  tableInfoMap;

    public DatabaseInfo(String databaseName, Map<String, TableInfo> tableInfoMap) {
        this.databaseName = databaseName;
        this.tableInfoMap = tableInfoMap;
    }

    public TableInfo getTableInfo(String tableName) {
        return this.tableInfoMap.get(tableName);
    }

    public Set<String> getAllTableNames() {
        return tableInfoMap.keySet();
    }

    public String getDatabaseName() {
        return databaseName;
    }

    @Override
    public String toString() {
        return "DatabaseInfo{" +
                "databaseName='" + databaseName + '\'' +
                ", tableInfoMap=" + tableInfoMap +
                '}';
    }
}
