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

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * The mysql connection factory responsible for keeping all connections.
 */
public class MySqlConnectionFactory {

    private HikariDataSource dataSource;

    /**
     * Instantiate the connection factory with the mysql configuration.
     * @param sqlConfig
     */
    public MySqlConnectionFactory(MySqlConfig sqlConfig) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(getJdbcUrl(sqlConfig));
        config.setUsername(sqlConfig.getUser());
        config.setPassword(sqlConfig.getPassword());
        this.dataSource = new HikariDataSource(config);
    }

    /**
     * Retrieve a connection.
     *
     * @return a mysql connection object.
     */
    public Connection getConnection() {
        try {
            return this.dataSource.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Clean up the connection factory.
     */
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
