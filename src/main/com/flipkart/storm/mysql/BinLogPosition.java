package com.flipkart.storm.mysql;

public class BinLogPosition {

    private final int       binLogPosition;
    private final String    binLogFileName;

    public BinLogPosition(int binLogPosition, String binLogFileName) {
        this.binLogPosition = binLogPosition;
        this.binLogFileName = binLogFileName;
    }

    public long getSCN() {
        int bingLogFileNumericSuffix = extractFileNumber(this.binLogFileName);
        long scn = bingLogFileNumericSuffix;
        scn <<= 32;
        scn |= this.binLogPosition;
        return scn;
    }

    private int extractFileNumber(String mysqlBinLogFileName) {
        String[] split = mysqlBinLogFileName.split("\\.");
        return Integer.parseInt(split[split.length - 1]);
    }

    public int getBinLogPosition() {
        return binLogPosition;
    }

    public String getBinLogFileName() {
        return binLogFileName;
    }
}
