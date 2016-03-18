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

package com.flipkart.storm.mysql.schema;

import java.util.Map;
import java.util.Set;

/**
 * Complete database schema/information.
 */
public class DatabaseInfo {

    private String                  databaseName;
    private Map<String, TableInfo>  tableInfoMap;

    /**
     * Database Schema object creation.
     *
     * @param databaseName the database name
     * @param tableInfoMap the table map
     */
    public DatabaseInfo(String databaseName, Map<String, TableInfo> tableInfoMap) {
        this.databaseName = databaseName;
        this.tableInfoMap = tableInfoMap;
    }

    /**
     * Get the complete information about the table.
     * @param tableName the name of the table.
     * @return
     */
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
