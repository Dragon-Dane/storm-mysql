package com.flipkart.storm.mysql;

import com.flipkart.storm.mysql.schema.ColumnInfo;
import com.flipkart.storm.mysql.schema.DatabaseInfo;
import com.flipkart.storm.mysql.schema.RowInfo;
import com.flipkart.storm.mysql.schema.TableInfo;

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

public class MySqlClient {

    private MySqlConnectionFactory connectionFactory;

    public MySqlClient(MySqlConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    public DatabaseInfo getDatabaseSchema(String databaseName, Set<String> tableNames) throws SQLException {
        if (tableNames.size() == 0) tableNames = getAllTables();
        Map<String, TableInfo> tableSchemas = new HashMap<String, TableInfo>();
        for (String tableName : tableNames) {
            RowInfo rowInfo = new RowInfo(getColumnInfo(databaseName, tableName));
            TableInfo tableInfo = new TableInfo(tableName, rowInfo);
            tableSchemas.put(tableName, tableInfo);
        }
        return new DatabaseInfo(databaseName, tableSchemas);
    }

    public BinLogPosition getBinLogDetails() throws SQLException {
        ResultSet resultSet = connectionFactory.getConnection().createStatement().executeQuery("SHOW MASTER STATUS");
        resultSet.next();
        BinLogPosition binLogPosition = new BinLogPosition(resultSet.getInt("Position"), resultSet.getString("File"));
        resultSet.close();
        return binLogPosition;
    }

    public void close() {
        connectionFactory.cleanup();
        connectionFactory = null;
    }

    private List<ColumnInfo> getColumnInfo(String databaseName, String tableName) throws SQLException {
        List<ColumnInfo> columnInfoList = new ArrayList<ColumnInfo>();
        PreparedStatement columnDetailsStatement = connectionFactory.getConnection().prepareStatement("SELECT * FROM information_schema.columns WHERE table_schema = ? and table_name = ?");
        columnDetailsStatement.setString(1, databaseName);
        columnDetailsStatement.setString(2, tableName);
        ResultSet resultSet = columnDetailsStatement.executeQuery();

        while(resultSet.next()) {
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
