package com.flipkart.storm.mysql;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class MySqlConnectionFactory {

    private HikariDataSource dataSource;

    public MySqlConnectionFactory(MySqlConfig sqlConfig) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(getJdbcUrl(sqlConfig));
        config.setUsername(sqlConfig.getUser());
        config.setPassword(sqlConfig.getPassword());
        this.dataSource = new HikariDataSource(config);
    }

    public Connection getConnection() {
        try {
            return this.dataSource.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void cleanup() {
        if (dataSource != null) {
            dataSource.close();
        }
    }

    private String getJdbcUrl(MySqlConfig config) {
        StringBuilder jdbcUrlBuilder = new StringBuilder("jdbc:mysql://");
        jdbcUrlBuilder.append(config.getHost());
        jdbcUrlBuilder.append(":");
        jdbcUrlBuilder.append(config.getPort());
        jdbcUrlBuilder.append("/");
        jdbcUrlBuilder.append(config.getDatabase());
        return jdbcUrlBuilder.toString();
    }
}
