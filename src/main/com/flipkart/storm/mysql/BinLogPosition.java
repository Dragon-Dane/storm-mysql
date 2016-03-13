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
