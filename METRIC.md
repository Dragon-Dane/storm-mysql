##A MySql Spout Metric Consumer Example

####This example uses Codahale Metrics library and pushes to Jmx.

####Create a Registry

```java

public class MetricsUtil {

    private static final String METRICS_REGISTRY = "metrics_registry";

    //This creates the metrics registry and starts the jmx reporter
    public static MetricRegistry init() {
        //only initialise if the metrics registry is already not
        if (!SharedMetricRegistries.names().contains(METRICS_REGISTRY)) {
            MetricRegistry metricRegistry = new MetricRegistry();
            SharedMetricRegistries.add(METRICS_REGISTRY, metricRegistry);
            startReporter(metricRegistry);
        }
        return SharedMetricRegistries.getOrCreate(METRICS_REGISTRY);
    }

    private static void startReporter(MetricRegistry metricRegistry) {
        JmxReporter jmxReporter = JmxReporter.forRegistry(metricRegistry).build();
        jmxReporter.start();
    }

    public static <T> void gauge(MetricRegistry metricRegistry, String name, Gauge<T> gauge ) {
        if (!metricRegistry.getGauges().containsKey(name)) {
            try {
                metricRegistry.register(name, gauge);
            } catch (IllegalArgumentException e) {
                //Do nothing
            }
        }
    }
}
```

####Now a Consumer

```java

public class MySqlSpoutMetricConsumer implements IMetricsConsumer {

    public static final Logger LOG = LoggerFactory.getLogger(MySqlSpoutMetricConsumer.class);
    private String metricsBaseName = "MySqlSpout";
    private long successCount;
    private long failureCount;
    private long sidelineCount;
    private long internalBufferSize;
    private long pendingMessageSize;
    private int binLogFileNum;
    private int binLogFilePosition;
    private double txProcessTime;
    private double txFailMsgTimeInTopology;

    @Override
    public void prepare(Map stormConf, Object registrationArgument,
                        TopologyContext context, IErrorReporter errorReporter) {
        buildMetrics();
    }


    @Override
    public void handleDataPoints(TaskInfo taskInfo, Collection<DataPoint> dataPoints) {
        if (taskInfo.srcComponentId.equals("mysqlspout")) {
            for (DataPoint p : dataPoints) {
                LOG.info("Mysql Spout Metric Consumer Data Point--->" +p.toString());
                switch(p.name) {
                    case SpoutConstants.METRIC_SUCCESSCOUNT:
                        successCount = Long.parseLong(p.value.toString());
                        break;
                    case SpoutConstants.METRIC_FAILURECOUNT:
                        failureCount = Long.parseLong(p.value.toString());
                        break;
                    case SpoutConstants.METRIC_SIDELINECOUNT:
                        sidelineCount = Long.parseLong(p.value.toString());
                        break;
                    case SpoutConstants.METRIC_BUFFER_SIZE:
                        internalBufferSize = Long.parseLong(p.value.toString());
                        break;
                    case SpoutConstants.METRIC_PENDING_MESSAGES:
                        pendingMessageSize = Long.parseLong(p.value.toString());
                        break;
                    case SpoutConstants.METRIC_TXPROCESSTIME:
                        txProcessTime = Double.parseDouble(p.value.toString());
                        break;
                    case SpoutConstants.METRIC_FAIL_MSG_IN_TOPOLOGY:
                        txFailMsgTimeInTopology = Double.parseDouble(p.value.toString());
                        break;
                    case SpoutConstants.METRIC_BINLOG_FILE_NUM:
                        binLogFileNum = Integer.parseInt(p.value.toString());
                        break;
                    case SpoutConstants.METRIC_BIN_LOG_FILE_POS:
                        binLogFilePosition = Integer.parseInt(p.value.toString());
                        break;
                }
            }

        }

    }

    private void buildMetrics() {
        MetricRegistry metricRegistry = MetricsUtil.init();

        MetricsUtil.gauge(metricRegistry, MetricRegistry.name(metricsBaseName, "_success"),
                () -> successCount );

        MetricsUtil.gauge(metricRegistry, MetricRegistry.name(metricsBaseName, "_failure"),
                () -> failureCount );

        MetricsUtil.gauge(metricRegistry, MetricRegistry.name(metricsBaseName, "_sideline"),
                () -> sidelineCount );

        MetricsUtil.gauge(metricRegistry, MetricRegistry.name(metricsBaseName, "_internalbuffersize"),
                () -> internalBufferSize );

        MetricsUtil.gauge(metricRegistry, MetricRegistry.name(metricsBaseName, "_pendingsize"),
                () -> pendingMessageSize );

        MetricsUtil.gauge(metricRegistry, MetricRegistry.name(metricsBaseName, "_txeventprocesstime"),
                () -> txProcessTime );

        MetricsUtil.gauge(metricRegistry, MetricRegistry.name(metricsBaseName, "_txEventFailTimeInTopology"),
                () -> txFailMsgTimeInTopology );

        MetricsUtil.gauge(metricRegistry, MetricRegistry.name(metricsBaseName, "_bin_log_file_num"),
                () -> binLogFileNum );

        MetricsUtil.gauge(metricRegistry, MetricRegistry.name(metricsBaseName, "_bin_log_file_pos"),
                () -> binLogFilePosition );

    }

    @Override
    public void cleanup() { }
}
```

Add to the config

```java
config.registerMetricsConsumer(MySqlSpoutMetricConsumer.class, 1);
```
