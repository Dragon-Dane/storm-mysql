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
 * The MySql Bin Log Position.
 */
public class BinLogPosition {

    private final int           binLogPosition;
    private final String        binLogFileName;
    private static final int    SHIFT_LEFT_BITS = 32;

    /**
     * Instantiate the bin log position.
     *
     * @param binLogPosition the position in the bin log
     * @param binLogFileName the bin log file name
     */
    public BinLogPosition(int binLogPosition, String binLogFileName) {
        this.binLogPosition = binLogPosition;
        this.binLogFileName = binLogFileName;
    }

    /**
     * The offset that combines both bin log position and bin log filename.
     * The most significant 32 bits would be the bin log filename number
     * and the least significant 32 bits would be the position in the file.
     *
     * @return the offset.
     */
    public long getSCN() {
        int bingLogFileNumericSuffix = extractFileNumber(this.binLogFileName);
        long scn = bingLogFileNumericSuffix;
        scn <<= SHIFT_LEFT_BITS;
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
