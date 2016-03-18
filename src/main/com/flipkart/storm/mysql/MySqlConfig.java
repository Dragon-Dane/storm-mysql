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

import java.io.Serializable;
import java.util.Collections;
import java.util.Set;

/**
 * MySql Configuration for the spout.
 */
public final class MySqlConfig implements Serializable {

    private final String    user;
    private final String    password;
    private final String    host;
    private final int       port;
    private final int       serverId;
    private final int       binLogPosition;
    private final String    binLogFileName;
    private final String    database;
    private final Set<String> includeTables;

    /**
     * The Builder class for MySql configuration.
     */
    public static class Builder {

        private String  innerUser           = SpoutConstants.DEFAULT_MYSQL_USER;
        private String  innerPassword       = SpoutConstants.DEFAULT_MYSQL_PASSWORD;
        private String  innerHost           = SpoutConstants.DEFAULT_MYSQL_HOST;
        private int     innerPort           = SpoutConstants.DEFAULT_MYSQL_PORT;
        private int     innerServerId       = SpoutConstants.DEFAULT_MYSQL_SERVERID;
        private int     innerBinLogPosition = SpoutConstants.DEFAULT_BINLOGPOSITION;
        private String  innerBinLogFileName = SpoutConstants.DEFAULT_BINLOG_FILENAME;
        private Set<String>  innerTables    = Collections.emptySet();
        private final String innerDatabase;

        /**
         * Set mandatory database name.
         * .
         * @param databaseName the name of the database to start replication from.
         */
        public Builder(String databaseName) {
            this.innerDatabase = databaseName;
        }

        /**
         * Set user to be used for connecting to the database.
         *
         * @param user the user.
         * @return the builder object to continue building
         */
        public Builder user(String user) {
            this.innerUser = user;
            return this;
        }

        /**
         * Set password to be used for connecting to the database.
         *
         * @param password the password.
         * @return the builder object to continue building
         */
        public Builder password(String password) {
            this.innerPassword = password;
            return this;
        }

        /**
         * Set host to be used for connecting to the database.
         *
         * @param host the host.
         * @return the builder object to continue building
         */
        public Builder host(String host) {
            this.innerHost = host;
            return this;
        }

        /**
         * Set port to be used for connecting to the database.
         *
         * @param port the port.
         * @return the builder object to continue building
         */
        public Builder port(int port) {
            this.innerPort = port;
            return this;
        }

        /**
         * Set serverid to be used for connecting to the database.
         *
         * @param serverId the serverId.
         * @return the builder object to continue building
         */
        public Builder serverId(int serverId) {
            this.innerServerId = serverId;
            return this;
        }

        /**
         * Set bin log position to start replicating from.
         *
         * @param position the position.
         * @return the builder object to continue building
         */
        public Builder binLogPosition(int position) {
            this.innerBinLogPosition = position;
            return this;
        }

        /**
         * Set bin log filename to start replicating from.
         *
         * @param fileName the fileName.
         * @return the builder object to continue building
         */
        public Builder binLogFilename(String fileName) {
            this.innerBinLogFileName = fileName;
            return this;
        }

        /**
         * Include these tables to start replicating from.
         *
         * @param tables the table set.
         * @return the builder object to continue building
         */
        public Builder includeTables(Set<String> tables) {
            this.innerTables = tables;
            return this;
        }

        /**
         * Build the complete object with properties that were set.
         * @return the mysql config object
         */
        public MySqlConfig build() {
            MySqlConfig mySqlConfig =  new MySqlConfig(this);
            return mySqlConfig;
        }

    }

    private MySqlConfig(Builder builder) {
        this.user           = builder.innerUser;
        this.password       = builder.innerPassword;
        this.host           = builder.innerHost;
        this.port           = builder.innerPort;
        this.serverId       = builder.innerServerId;
        this.binLogPosition = builder.innerBinLogPosition;
        this.binLogFileName = builder.innerBinLogFileName;
        this.database       = builder.innerDatabase;
        this.includeTables  = builder.innerTables;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public int getServerId() {
        return serverId;
    }

    public int getBinLogPosition() {
        return binLogPosition;
    }

    public String getBinLogFileName() {
        return binLogFileName;
    }

    public String getDatabase() {
        return database;
    }

    public Set<String> getTables() {
        return includeTables;
    }
}
