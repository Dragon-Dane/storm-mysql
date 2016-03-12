package com.flipkart.storm.mysql;

public class OffsetInfo {

    private final long      scn;
    private final String    topologyName;
    private final String    topologyInstanceId;
    private final String    databaseName;
    private final int       binLogPosition;
    private final String    binLogFileName;


    public OffsetInfo(long scn,
                      String topologyName,
                      String topologyInstanceId,
                      String databaseName,
                      int binLogPosition,
                      String binLogFileName) {
        this.scn = scn;
        this.topologyName = topologyName;
        this.topologyInstanceId = topologyInstanceId;
        this.databaseName = databaseName;
        this.binLogPosition = binLogPosition;
        this.binLogFileName = binLogFileName;
    }


    public int getBinLogPosition() {
        return binLogPosition;
    }

    public String getBinLogFileName() {
        return binLogFileName;
    }
}
