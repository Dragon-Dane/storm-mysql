package com.flipkart.storm.mysql;

import java.io.Serializable;
import java.util.Collections;
import java.util.Set;

public class MySqlConfig implements Serializable {

    private final String    user;
    private final String    password;
    private final String    host;
    private final int       port;
    private final int       serverId;
    private final int       binLogPosition;
    private final String    binLogFileName;
    private final String    database;
    private final Set<String> includeTables;

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

    public static class Builder {

        private String  innerUser           = SpoutConstants.DEFAULT_MYSQL_USER;
        private String  innerPassword       = SpoutConstants.DEFAULT_MYSQL_PASSWORD;
        private String  innerHost           = SpoutConstants.DEFAULT_MYSQl_HOST;
        private int     innerPort           = SpoutConstants.DEFAULT_MYSQL_PORT;
        private int     innerServerId       = SpoutConstants.DEFAULT_MYSQL_SERVERID;
        private int     innerBinLogPosition = SpoutConstants.DEFAULT_BINLOGPOSITION;
        private String  innerBinLogFileName = SpoutConstants.DEFAULT_BINLOG_FILENAME;
        private Set<String>  innerTables    = Collections.emptySet();
        private final String innerDatabase;

        public Builder(String databaseName) {
            this.innerDatabase = databaseName;
        }

        public Builder user(String user) {
            this.innerUser = user;
            return this;
        }

        public Builder password(String password) {
            this.innerPassword = password;
            return this;
        }

        public Builder host(String host) {
            this.innerHost = host;
            return this;
        }

        public Builder port(int port) {
            this.innerPort = port;
            return this;
        }

        public Builder serverId(int serverId) {
            this.innerServerId = serverId;
            return this;
        }

        public Builder binLogPosition(int position) {
            this.innerBinLogPosition = position;
            return this;
        }

        public Builder binLogFilename(String fileName) {
            this.innerBinLogFileName = fileName;
            return this;
        }

        public Builder includeTables(Set<String> tables) {
            this.innerTables = tables;
            return this;
        }

        public MySqlConfig build() {
            MySqlConfig mySqlConfig =  new MySqlConfig(this);
            return mySqlConfig;
        }

    }
}
