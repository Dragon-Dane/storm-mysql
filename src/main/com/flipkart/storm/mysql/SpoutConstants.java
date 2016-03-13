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

public class SpoutConstants {

    public static final String DEFAULT_MYSQL_USER           = "sa";
    public static final String DEFAULT_MYSQL_PASSWORD       = "";
    public static final String DEFAULT_MYSQl_HOST           = "localhost";
    public static final int    DEFAULT_MYSQL_PORT           = 3306;
    public static final int    DEFAULT_MYSQL_SERVERID       = 1;
    public static final int    DEFAULT_BINLOGPOSITION       = 4;
    public static final String DEFAULT_BINLOG_FILENAME      = "";
    public static final String DEFAULT_ZKROOT               = "mysql-binlog-spout";
    public static final int    DEFAULT_ZK_UPDATE_RATE_MS    = 30000;
    public static final String ZK_SEPARATOR                 = "/";
    public static final int    DEFAULT_ZKPORT               = 2181;
    public static final int    DEFAULT_TIMEBUCKETSIZEINSECS = 30;
    public static final String METRIC_FAILURECOUNT          = "failure_count";
    public static final String METRIC_SUCCESSCOUNT          = "success_count";
    public static final String METRIC_SIDELINECOUNT         = "sideline_count";
    public static final String METRIC_TXPROCESSTIME         = "tx_process_mean";
    public static final int    DEFAULT_NUMMAXRETRIES        = 10;
    public static final int    DEFAULT_NUMMAXTOTFAILALLOWED = 1000000;

}
