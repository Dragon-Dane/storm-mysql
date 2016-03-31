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

import com.flipkart.storm.mysql.schema.ColumnInfo;
import com.flipkart.storm.mysql.schema.DatabaseInfo;
import com.flipkart.storm.mysql.schema.RowInfo;
import com.flipkart.storm.mysql.schema.TableInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Client for connecting to MySql.
 */
public class MySqlClient {

    private MySqlConnectionFactory connectionFactory;
    private static final String GET_MYSQL_TABLE_SCHEMA =
            "SELECT * FROM information_schema.columns WHERE table_schema = ? and table_name = ?";
    private static final String SHOW_MASTER_STATUS = "SHOW MASTER STATUS";

    /** The logger. */
    public static final Logger LOGGER = LoggerFactory.getLogger(MySqlClient.class);

    /**
     * Instantiate the mysql client with the connection factory.
     * @param connectionFactory the connection factory
     */
    public MySqlClient(MySqlConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    /**
     * Determines the database schema for the database.
     * If no tables are provided all tables are considered.
     * @param databaseName the database name
     * @param tableNames the table name
     * @return Complete schema for the database
     * @throws SQLException
     */
    public DatabaseInfo getDatabaseSchema(String databaseName, Set<String> tableNames) throws SQLException {
        if (tableNames == null || tableNames.size() == 0) {
            LOGGER.info("Since no tables config was provided, considering all tables in database : {}", databaseName);
            tableNames = getAllTables();
        }
        LOGGER.info("Considering following tables {}", tableNames);
        Map<String, TableInfo> tableSchemas = new HashMap<String, TableInfo>();
        for (String tableName : tableNames) {
            List<ColumnInfo> columnInfoList = getColumnInfo(databaseName, tableName);
            LOGGER.info("Got column list for table : {}, columns {}", tableName, columnInfoList);

            RowInfo rowInfo = new RowInfo(columnInfoList);
            TableInfo tableInfo = new TableInfo(tableName, rowInfo);
            //Converting to lowercase cause open replicator seems to return
            //all bin log events with tables in lowercase. The comparison for
            //filtering events works correctly then.
            tableSchemas.put(tableName.toLowerCase(), tableInfo);
        }
        return new DatabaseInfo(databaseName, tableSchemas);
    }

    /**
     * Get current bin log details from MySql.
     * @return bin log position
     * @throws SQLException
     */
    public BinLogPosition getBinLogDetails() throws SQLException {
        ResultSet resultSet = connectionFactory.getConnection().createStatement().executeQuery(SHOW_MASTER_STATUS);
        resultSet.next();
        BinLogPosition binLogPosition = new BinLogPosition(resultSet.getInt("Position"), resultSet.getString("File"));
        resultSet.close();
        return binLogPosition;
    }

    /**
     * Release the connection factory.
     */
    public void close() {
        connectionFactory.cleanup();
        connectionFactory = null;
    }

    private List<ColumnInfo> getColumnInfo(String databaseName, String tableName) throws SQLException {
        List<ColumnInfo> columnInfoList = new ArrayList<ColumnInfo>();
        PreparedStatement columnDetailsStatement = connectionFactory.getConnection()
                                                                    .prepareStatement(GET_MYSQL_TABLE_SCHEMA);
        columnDetailsStatement.setString(1, databaseName);
        columnDetailsStatement.setString(2, tableName);

        LOGGER.info("Executing column info query : {}", columnDetailsStatement.toString());
        ResultSet resultSet = columnDetailsStatement.executeQuery();

        while (resultSet.next()) {
            String colName    = resultSet.getString("COLUMN_NAME");
            String colType    = resultSet.getString("DATA_TYPE");
            int colPos        = resultSet.getInt("ORDINAL_POSITION") - 1;
            columnInfoList.add(new ColumnInfo(colName, colPos, colType));
        }
        resultSet.close();
        columnDetailsStatement.close();
        return columnInfoList;
    }

    private Set<String> getAllTables() throws SQLException {
        DatabaseMetaData meta = connectionFactory.getConnection().getMetaData();
        ResultSet resultSet = meta.getTables(null, null, null, new String[] {"TABLE"});
        Set<String> tableSet = new HashSet<String>();
        while (resultSet.next()) {
            tableSet.add(resultSet.getString("TABLE_NAME"));
        }
        resultSet.close();
        return tableSet;
    }
}
