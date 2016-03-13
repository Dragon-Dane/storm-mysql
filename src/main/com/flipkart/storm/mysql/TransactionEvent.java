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

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.ArrayList;
import java.util.List;

public class TransactionEvent {

    private final TransactionState  txState;
    private final String            databaseName;
    private final int               serverId;
    private final int               startBinLogPosition;
    private final String            startBinLogFileName;
    private final int               endBinLogPosition;
    private final String            endBinLogFileName;
    private final Long              txId;
    private final List<DataEvent>   dataEvents;
    private final long              startTimeInNanos;
    private final long              endTimeInNanos;

    private TransactionEvent(builder builder) {
        this.databaseName           = builder.innerDataBaseName;
        this.serverId               = builder.innerServerId;
        this.startBinLogPosition    = builder.innerStartBinLogPos;
        this.endBinLogPosition      = builder.innerEndBinLogPos;
        this.startBinLogFileName    = builder.innerStartBinLogFileName;
        this.endBinLogFileName      = builder.innerEndBinLogFileName;
        this.txId                   = builder.innerTxId;
        this.txState                = builder.innerTxState;
        this.dataEvents             = builder.innerDataEventList;
        this.startTimeInNanos       = builder.innerStartTimeInNanos;
        this.endTimeInNanos         = builder.innerEndTimeInNanos;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public int getServerId() {
        return serverId;
    }

    @JsonIgnore
    public int getStartBinLogPosition() {
        return startBinLogPosition;
    }

    @JsonIgnore
    public String getStartBinLogFileName() {
        return startBinLogFileName;
    }

    @JsonIgnore
    public int getEndBinLogPosition() {
        return endBinLogPosition;
    }

    @JsonIgnore
    public String getEndBinLogFileName() {
        return endBinLogFileName;
    }

    public List<DataEvent> getDataEvents() {
        return dataEvents;
    }

    @JsonIgnore
    public Long getTransactionId() {
        return txId;
    }

    @JsonIgnore
    public TransactionState getTransactionState() {
        return txState;
    }

    @JsonIgnore
    public long getStartTimeInNanos() {
        return startTimeInNanos;
    }

    @JsonIgnore
    public long getEndTimeInNanos() {
        return endTimeInNanos;
    }

    @Override
    public String toString() {
        return "TransactionEvent{" +
                "txState=" + txState +
                ", databaseName='" + databaseName + '\'' +
                ", serverId=" + serverId +
                ", startBinLogPosition=" + startBinLogPosition +
                ", startBinLogFileName='" + startBinLogFileName + '\'' +
                ", endBinLogPosition=" + endBinLogPosition +
                ", endBinLogFileName='" + endBinLogFileName + '\'' +
                ", txId=" + txId +
                ", dataEvents=" + dataEvents +
                '}';
    }

    public static class builder {

        private String              innerDataBaseName           = "";
        private int                 innerServerId               = -1;
        private int                 innerStartBinLogPos         = -1;
        private int                 innerEndBinLogPos           = -1;
        private String              innerStartBinLogFileName    = "";
        private String              innerEndBinLogFileName      = "";
        private Long                innerTxId                   = null;
        private long                innerStartTimeInNanos       = 0l;
        private long                innerEndTimeInNanos         = 0l;
        private TransactionState    innerTxState                = TransactionState.NONE;
        private List<DataEvent>     innerDataEventList          = new ArrayList<DataEvent>();

        public builder database(String database) {
            this.innerDataBaseName = database;
            return this;
        }

        public builder serverId(int serverId) {
            this.innerServerId = serverId;
            return this;
        }

        public builder startBinLogPos(int pos) {
            this.innerStartBinLogPos = pos;
            return this;
        }

        public builder endBinLogPos(int pos) {
            this.innerEndBinLogPos = pos;
            return this;
        }

        public builder startBinLogFileName(String fileName) {
            this.innerStartBinLogFileName = fileName;
            return this;
        }

        public builder endBinLogFileName(String fileName) {
            this.innerEndBinLogFileName = fileName;
            return this;
        }

        public builder addDataEvent(DataEvent dataEvent) {
            this.innerDataEventList.add(dataEvent);
            return this;
        }

        public builder txId(Long innerTxId) {
            this.innerTxId = innerTxId;
            return this;
        }

        public builder txState(TransactionState state) {
            this.innerTxState = state;
            return this;
        }

        public builder txTimeStart(long time) {
            this.innerStartTimeInNanos = time;
            return this;
        }

        public builder txTimeEnd(long time) {
            this.innerEndTimeInNanos = time;
            return this;
        }

        public TransactionState getInnerTxState() {
            return this.innerTxState;
        }

        public List<DataEvent> getInnerDataEvents() {
            return this.innerDataEventList;
        }

        public int getInnerServerId() {
            return this.innerServerId;
        }

        public builder reset() {
            this.innerDataBaseName           = "";
            this.innerServerId               = -1;
            this.innerStartBinLogPos         = -1;
            this.innerEndBinLogPos           = -1;
            this.innerStartBinLogFileName    = "";
            this.innerEndBinLogFileName      = "";
            this.innerTxId                   = null;
            this.innerStartTimeInNanos       = 0l;
            this.innerEndTimeInNanos         = 0l;
            this.innerTxState                = TransactionState.NONE;
            this.innerDataEventList = new ArrayList<DataEvent>();
            return this;
        }

        public TransactionEvent build() {
            TransactionEvent txEvent =  new TransactionEvent(this);
            return txEvent;
        }

    }
}
