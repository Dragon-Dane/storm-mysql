<img src="https://github.com/flipkart-incubator/storm-mysql/blob/master/images/StormSpout.png">


#Storm MySql

[![][travis img]][travis]
[![][license img]][license]

Provides a Storm Spout implementation for consuming data from MySql Bin Logs.

Based on [OpenReplicator](https://github.com/whitesock/open-replicator)

##Tuples

All tuples are of the form

    [databaseName, txEvent]

The first field/value in the tuple is the "databaseName".

The second field/value is the actual event that contains all the details regarding the operation that was performed.
Henceforth in the discussion this second value of the tuple is mentioned as an event.

Events generated are in Json Format. If there are multiple tables involved in a transaction, only one event will be raised with
appropriate details. For multi update scenarios as well, only one event will be raised.

Lets illustrate with examples:

A Table "ATable" with (`id` int(11) DEFAULT NULL, `salary` int(11) DEFAULT NULL, `name` varchar(32) DEFAULT NULL, `dept` varchar(32) DEFAULT NULL )

#### An Insert

    mysql> insert into ATable(id, salary, name, dept) values(100001, 50000, 'Emp001', 'Finance');
    
Event Generated

```javascript
{
    "databaseName": "test",
    "serverId": 1,
    "dataEvents": [{
        "tableName": "atable",
        "dataEventType": "INSERT",
        "oldData": null,
        "data": [{
            "name": "Emp001",
            "id": 100001,
            "dept": "Finance",
            "salary": 50000
        }]
    }]
}
```
    
#### An Update

    mysql> update ATable set salary=60000, dept='Sales' where id=100001;
    
Event Generated

```javascript
{
    "databaseName": "test",
    "serverId": 1,
    "dataEvents": [{
        "tableName": "atable",
        "dataEventType": "UPDATE",
        "oldData": [{
            "name": "Emp001",
            "id": 100001,
            "dept": "Finance",
            "salary": 50000
        }],
        "data": [{
            "name": "Emp001",
            "id": 100001,
            "dept": "Sales",
            "salary": 60000
        }]
    }]
}
```
    
#### A Delete

    mysql> delete from ATable where id=100001; 
    
Event Generated

```javascript
{
     "databaseName": "test",
     "serverId": 1,
     "dataEvents": [{
         "tableName": "atable",
         "dataEventType": "DELETE",
         "oldData": null,
         "data": [{
             "name": "Emp001",
             "id": 100001,
             "dept": "Sales",
             "salary": 60000
         }]
     }]
}
```

For more event examples [Events.md](https://github.com/flipkart-incubator/storm-mysql/blob/master/EVENTS.md)


##The Spout

In order to initialize your MySql spout, you need to construct an instance of the MySqlSpoutConfig.

    public MySqlSpoutConfig(MySqlConfig mysqlConfig, ZkBinLogStateConfig zkBinLogStateConfig)
    public MySqlSpoutConfig(MySqlConfig mysqlConfig, ZkBinLogStateConfig zkBinLogStateConfig, FailureConfig failureConfig)

### MySqlConfig

MySqlConfig contains the connection information to MySql and various other parameters that controls how the spout filters
and reads data off the bin logs.

Parameters that are currently supported are:

    private final String    user;               //Default : "sa"
    private final String    password;           //Default : ""
    private final String    host;               //Default : "localhost"
    private final int       port;               //Default : 3306
    private final int       serverId;           //Default : 1
    private final int       binLogPosition;     //Default : 4
    private final String    binLogFileName;
    private final String    database;
    private final Set<String> includeTables;

Most of the parameters are self explanatory, but for binlogFileName and tables.
Database is the only mandatory parameter that has to provided for. All others have been provided sensible defaults.

Tables is the list of tables where any insert, update or delete needs to be emitted to the storm stream.
If no includeTables is provided for, all tables of the provided database are taken into account i.e. there will be no
filtering.

####Example

If there are are a total of three tables in the database 'A', 'B' and 'C' and if only 'A' and 'B' were in the include table list,
the spout will filter out all events pertaining to table 'C'.
Please also note that even if there is a transaction involving all three tables, 
    
    start transaction
        insert into 'A'
        update some row in 'C'
        update some row in 'B'
    commit

then also the event for 'C' will be filtered out. The stream will only contain two events:

    insert into 'A'
    update some row in 'B'

The "binlogFileName" is the file, where the spout will start reading from the "binLogPosition".
If this is not provided, the spout will take this data from the MySql Server automatically.
So if you are unsure, do not provide this and binlogPosition as well.

If you want to have a look at where your MySql is currently storing its binlog offsets, just do a

    mysql> show master status;
    
####Very Important

For the spout and all this to work correctly, please do ensure that binary logging is turned on.
Please consider the parameters below as an example, to be set in your "mysql.cnf" file.

    binlog-checksum                = NONE
    expire_logs_days               = 4
    sync_binlog                    = 1
    server-id                      = 1
    log-bin                        = /var/lib/mysql/mysql-bin.log
    binlog_format                  = ROW
    
####Creating a Java Instance, an Example

    MySqlConfig mySqlConfig = new MySqlConfig.Builder("testDatabase")
                                                 .user("testUser")
                                                 .password("testPass")
                                                 .host("localhost")
                                                 .port(3306)
                                                 .serverId(1)
                                                 .binLogFilename("mysql-bin.000017")
                                                 .binLogPosition(4)
                                                 .tables(Sets.newHashSet("ATable", "BTable"))
                                                 .build();
                                                 
### ZkBinLogStateConfig

ZkBinLogStateConfig contains the connection information to Zookeeper and various other parameters that controls how the spout
stores and reads its binlog offset from Zookeeper.

Parameters that are currently supported are:

    private final List<String>  zkServers;
    private final int           zkPort;
    private final String        zkRoot;
    private final String        zkSpoutId;
    private final int           zkScnUpdateRateInMs;
    private final Integer       zkSessionTimeoutInMs;
    private final Integer       zkConnectionTimeoutInMs;
    private final Integer       zkRetryTimes;
    private final Integer       zkSleepMsBetweenRetries;
    private final boolean       zkIgnoreBinLogPosition;

Let us understand all of them one by one:

####zkServers

The ips of your Zookeeper ensemble. If this not provided, the spout will automatically default to the
zookeeper being used for storm as configured in "storm.zookeeper.servers".

####zkPort

The port for zookeeper connection. If this not provided, the spout will automatically pick the port as
mentioned in "storm.zookeeper.port".

####zkRoot

This will be used as root node path in ZK to store bin log file offsets. Default for this is "mysql-binlog-spout"

####zkSpoutId

This field is mandatory, as it identifies the complete path of the ZK node that stores the bin log offsets.

    /{zkRoot}/{zkSpoutId}
    
is the ZK Node path where the bin log offsets are stored and read from.
 
####zkScnUpdateRateInMs

This field indicates the period of time after which the spout will update offsets in ZK. By Default the
value of this field is "30000" i.e. 30sec. A very low figure like 1ms or 10ms might impact throughput.

####ZK Connection Settings

zkSessionTimeoutInMs - The Zk session timeout, Defaults to "storm.zookeeper.session.timeout"

zkConnectionTimeoutInMs - The Zk Connection timeout, Defaults to "storm.zookeeper.connection.timeout"

zkRetryTimes - The Zk retry, Defaults to "storm.zookeeper.retry.times"

zkSleepMsBetweenRetries - The amount of time to sleep before retrying, Defaults to "storm.zookeeper.retry.interval"

####zkIgnoreBinLogPosition

This field is very important, and indicates how the spout when started reads the initial
Bin Log Position and FileName from ZooKeeper or from MySql. Default is "false"

If zkIgnoreBinLogPosition == false

    Get the BinLogPosition and FileName from the ZK Node "/{zkRoot}/{zkSpoutId}"
    If no data is found on that node:
        Get the data from the MySQL Server.

If zkIgnoreBinLogPosition == true

    Get the data from the MySQL Server.
    
This field is only supposed to be to true, when you want to ignore the ZK Node Data at "/{zkRoot}/{zkSpoutId}" and would
want to start from the current file and position as in your Mysql.

####Creating a Java Instance, an Example

This example contains all fields, please remove the fields when building an instance, which you are think are not required
and defaults would suffice.

    ZkBinLogStateConfig zkBinLogStateConfig = new ZkBinLogStateConfig.Builder("my-spout")
                                                                     .servers(Lists.newArrayList("localhost"))
                                                                     .port(2181)
                                                                     .root("mysql-binlog-spout")
                                                                     .ignoreZkBinLogPosition(false)
                                                                     .sessionTimeOutInMs(100)
                                                                     .retryTimes(5)
                                                                     .connectionTimeOutInMs(100)
                                                                     .updateRate(1000)
                                                                     .build();


### FailureConfig

FailureConfig contains the information on what the spout should do for failed messages. 

Parameters that are currently being considered:

    private final int               numMaxRetries;
    private final long              numMaxTotalFailAllowed;
    private final SidelineStrategy  sidelineStrategy;

When a failureConfig is not provided a default configuration is taken, where

    numMaxRetries = 10;
    numMaxTotalFailAllowed = 1000000;
    sidelineStrategy = Log as error

####numMaxRetries

The max number of times an event is allowed to fail before being sidelined(removed) from the stream.

####numMaxTotalFailAllowed

The max total number of failure events in the internal buffer at any one time. If this count exceeds the spout
would crash.

####sidelineStrategy

The strategy that decides what to do with a failedMessage > numMaxRetries.
The default mechanism just logs as error and removes it from the stream.
A custom implementation would just involve extending the "SidelineStrategy" Interface.
Please do note that as soon as the "sideline()" function returns, the event would be removed from the stream.
Do ensure you have a record of that event somewhere.

####Creating a Java Instance, an Example

    FailureConfig failureConfig = new FailureConfig(10, 1000000, new SidelineStrategy() {
            @Override
            public void sideline(TransactionEvent txEvent) {
                log.error(txEvent.toString());
            }
        });
        
##Complete Spout Example

    MySqlConfig mySqlConfig = new MySqlConfig.Builder("testDatabase")
                                             .user("testUser")
                                             .password("testPass")
                                             .host("localhost")
                                             .port(3306)
                                             .serverId(1)
                                             .binLogFilename("mysql-bin.000017")
                                             .binLogPosition(4)
                                             .tables(Sets.newHashSet("ATable", "BTable"))
                                             .build();

    ZkBinLogStateConfig zkBinLogStateConfig = new ZkBinLogStateConfig.Builder("my-spout")
                                                                     .servers(Lists.newArrayList("localhost"))
                                                                     .port(2181)
                                                                     .root("mysql-binlog-spout")
                                                                     .ignoreZkBinLogPosition(false)
                                                                     .sessionTimeOutInMs(100)
                                                                     .retryTimes(5)
                                                                     .connectionTimeOutInMs(100)
                                                                     .updateRate(1000)
                                                                     .build();

    FailureConfig failureConfig = new FailureConfig(10, 1000000, new SidelineStrategy() {
                    @Override
                    public void sideline(TransactionEvent txEvent) {
                        log.error(txEvent.toString());
                    }
                });


    MySqlSpoutConfig spoutConfig = new MySqlSpoutConfig(mySqlConfig, zkBinLogStateConfig, failureConfig);
    MySqlBinLogSpout mySqlBinLogSpout = new MySqlBinLogSpout(spoutConfig);
    
    TopologyBuilder topologyBuilder = new TopologyBuilder();
    topologyBuilder.setSpout("mysqlspout", mySqlBinLogSpout);
    //Add Bolts
    
    Config conf = new Config();
    StormSubmitter.submitTopology("mysqlSpoutTest", conf, topologyBuilder.createTopology());
    
    
##Limitations

Currently tested ONLY with tables created with InnoDB storage engine.

MySql dataTypes supported currently:

    CHAR,
    VARCHAR,
    TEXT,
    TINYTEXT,
    MEDIUMTEXT,
    LONGTEXT,
    DECIMAL,
    INT,
    INTEGER,
    TINYINT,
    SMALLINT,
    MEDIUMINT,
    FLOAT,
    DOUBLE,
    BIGINT,
    DATE,
    DATETIME,
    TIMESTAMP,
    TIME,
    YEAR
    
Not Supported

    SET,
    ENUM,
    BINARY,
    VARBINARY,
    BIT,
    BLOB,
    TINYBLOB,
    MEDIUMBLOB,
    LONGBLOB
  
##Committers

* Sourav Mitra(@souravmitra)

[travis]:https://travis-ci.org/flipkart-incubator/storm-mysql
[travis img]:https://travis-ci.org/flipkart-incubator/storm-mysql.svg?branch=master
[license]:LICENSE
[license img]:https://img.shields.io/badge/License-Apache%202-blue.svg
