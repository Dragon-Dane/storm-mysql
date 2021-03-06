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

/**
 * Complete table schema/information of a mysql table.
 */
public class TableInfo {

    private final String    tableName;
    private final RowInfo   rowInfo;

    /**
     * Table info creation.
     *
     * @param tableName The table name
     * @param rowInfo Complete row information
     */
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
