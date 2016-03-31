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

/**
 * Constant class to keep all constants required by the spout.
 */
public final class SpoutConstants {

    /**
     * To prevent this util class getting instantiated.
     */
    @Deprecated
    private SpoutConstants() {
    }

    /**
     * This is the default mysql user for most installations.
     */
    public static final String DEFAULT_MYSQL_USER           = "sa";

    /**
     * Open replicator does not seem to work well with password less users.
     * It is advisable to have a user with a password during dev/qa phases as well.
     */
    public static final String DEFAULT_MYSQL_PASSWORD       = "";

    /**
     * Keeping the host localhost by default.
     */
    public static final String DEFAULT_MYSQL_HOST           = "localhost";

    /**
     * Most installations of mysql take this port as default.
     */
    public static final int    DEFAULT_MYSQL_PORT           = 3306;

    /**
     * The default mysql server id.
     */
    public static final int    DEFAULT_MYSQL_SERVERID       = 6789;

    /**
     * The default mysql bin log position.
     */
    public static final int    DEFAULT_BINLOGPOSITION       = 4;

    /**
     * Default bin log name being kept as empty.
     * The spout should automatically figure it out if not provided for.
     */
    public static final String DEFAULT_BINLOG_FILENAME      = "";

    /**
     * The default zk root node path under which all information for the spout will be saved.
     */
    public static final String DEFAULT_ZKROOT               = "mysql-binlog-spout";

    /**
     * The default zk update rate for bin log offsets.
     */
    public static final int    DEFAULT_ZK_UPDATE_RATE_MS    = 30000;

    /**
     * The default zk node path separator.
     */
    public static final String ZK_SEPARATOR                 = "/";

    /**
     * Most installations of ZK take this as the default port.
     */
    public static final int    DEFAULT_ZKPORT               = 2181;

    /**
     * Metrics to be calculated at this default bucket size.
     */
    public static final int    DEFAULT_TIMEBUCKETSIZEINSECS = 30;

    /**
     * Metrics for failure count.
     */
    public static final String METRIC_FAILURECOUNT          = "failure_count";

    /**
     * Metrics for success count.
     */
    public static final String METRIC_SUCCESSCOUNT          = "success_count";

    /**
     * Metrics for sidelining.
     */
    public static final String METRIC_SIDELINECOUNT         = "sideline_count";

    /**
     * Metrics for transaction process time.
     */
    public static final String METRIC_TXPROCESSTIME         = "tx_process_mean";

    /**
     * The default time the spout retries an transaction event, before sidelining.
     */
    public static final int    DEFAULT_NUMMAXRETRIES        = 10;

    /**
     * The default max failures allowed at one point in time.
     */
    public static final int    DEFAULT_NUMMAXTOTFAILALLOWED = 1000000;

}
