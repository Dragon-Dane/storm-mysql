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

import com.flipkart.storm.mysql.schema.ColumnInfo;
import com.flipkart.storm.mysql.schema.DatabaseInfo;
import com.google.code.or.binlog.BinlogEventListener;
import com.google.code.or.binlog.BinlogEventV4;
import com.google.code.or.binlog.impl.event.DeleteRowsEvent;
import com.google.code.or.binlog.impl.event.DeleteRowsEventV2;
import com.google.code.or.binlog.impl.event.QueryEvent;
import com.google.code.or.binlog.impl.event.TableMapEvent;
import com.google.code.or.binlog.impl.event.UpdateRowsEvent;
import com.google.code.or.binlog.impl.event.UpdateRowsEventV2;
import com.google.code.or.binlog.impl.event.WriteRowsEvent;
import com.google.code.or.binlog.impl.event.WriteRowsEventV2;
import com.google.code.or.binlog.impl.event.XidEvent;
import com.google.code.or.common.glossary.Column;
import com.google.code.or.common.glossary.Pair;
import com.google.code.or.common.glossary.Row;
import com.google.code.or.common.util.MySQLConstants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

import static java.lang.Math.toIntExact;

public class SpoutBinLogEventListener implements BinlogEventListener {

    private final TransactionEvent.builder txBuilder = CentralTxEventBuilder.INSTANCE.getBuilder();

    private final LinkedBlockingQueue<TransactionEvent> queue;
    private final DatabaseInfo                          databaseInfo;
    private final Map<Long, String>                     tableCache;

    public SpoutBinLogEventListener(LinkedBlockingQueue<TransactionEvent> queue, DatabaseInfo databaseInfo) {
        this.queue = queue;
        this.databaseInfo = databaseInfo;
        this.tableCache = new HashMap<Long, String>();
    }

    private List<Map<String, Object>> getData(String tableName, List<Row> rows) {
        List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
        for (Row row : rows) {
            Map<String, Object> data = new HashMap<String, Object>();
            List<Column> columns = row.getColumns();
            List<ColumnInfo> columnSchemaInfo = databaseInfo.getTableInfo(tableName).getRowInfo().getColumnInfo();
            for (int index = 0; index < columns.size(); ++index) {
                ColumnInfo columnInfo = columnSchemaInfo.get(index);
                Column col = columns.get(index);
                data.put(columnInfo.getName(),
                         columnInfo.getColumnDataType().getConvertedValue(columnInfo.getColumnDataType(),
                                 col.getValue()));
            }
            dataList.add(data);
        }
        return dataList;
    }

    @Override
    public void onEvents(BinlogEventV4 event) {

        String tableName = "";
        switch (event.getHeader().getEventType()) {
            case MySQLConstants.WRITE_ROWS_EVENT:
                WriteRowsEvent writeRowsEvent = (WriteRowsEvent) event;
                tableName = tableCache.get(writeRowsEvent.getTableId());
                if (tableName != null && txBuilder.getInnerTxState() == TransactionState.STARTED) {

                    DataEvent dataEvent = new DataEvent(tableName, DataEventType.INSERT,
                                                        null,
                                                        getData(tableName, writeRowsEvent.getRows()));
                    txBuilder.addDataEvent(dataEvent);
                }
                break;

            case MySQLConstants.WRITE_ROWS_EVENT_V2:
                WriteRowsEventV2 writeRowsEventV2 = (WriteRowsEventV2) event;
                tableName = tableCache.get(writeRowsEventV2.getTableId());
                if (tableName != null && txBuilder.getInnerTxState() == TransactionState.STARTED) {

                    DataEvent dataEvent = new DataEvent(tableName, DataEventType.INSERT,
                                                        null,
                                                        getData(tableName, writeRowsEventV2.getRows()));
                    txBuilder.addDataEvent(dataEvent);
                }
                break;

            case MySQLConstants.UPDATE_ROWS_EVENT:
                UpdateRowsEvent updateRowsEvent = (UpdateRowsEvent) event;
                tableName = tableCache.get(updateRowsEvent.getTableId());
                if (tableName != null && txBuilder.getInnerTxState() == TransactionState.STARTED) {

                    List<Row> oldData = new ArrayList<Row>();
                    List<Row> newData = new ArrayList<Row>();
                    for (Pair<Row> rows : updateRowsEvent.getRows()) {
                        oldData.add(rows.getBefore());
                        newData.add(rows.getAfter());
                    }
                    DataEvent dataEvent = new DataEvent(tableName, DataEventType.UPDATE,
                                                        getData(tableName, oldData),
                                                        getData(tableName, newData));
                    txBuilder.addDataEvent(dataEvent);
                }
                break;

            case MySQLConstants.UPDATE_ROWS_EVENT_V2:
                UpdateRowsEventV2 updateRowsEventV2 = (UpdateRowsEventV2) event;
                tableName = tableCache.get(updateRowsEventV2.getTableId());
                if (tableName != null && txBuilder.getInnerTxState() == TransactionState.STARTED) {

                    List<Row> oldData = new ArrayList<Row>();
                    List<Row> newData = new ArrayList<Row>();
                    for (Pair<Row> rows : updateRowsEventV2.getRows()) {
                        oldData.add(rows.getBefore());
                        newData.add(rows.getAfter());
                    }
                    DataEvent dataEvent = new DataEvent(tableName, DataEventType.UPDATE,
                                                        getData(tableName, oldData),
                                                        getData(tableName, newData));
                    txBuilder.addDataEvent(dataEvent);
                }
                break;

            case MySQLConstants.DELETE_ROWS_EVENT:
                DeleteRowsEvent deleteRowsEvent = (DeleteRowsEvent) event;
                tableName = tableCache.get(deleteRowsEvent.getTableId());
                if (tableName != null && txBuilder.getInnerTxState() == TransactionState.STARTED) {

                    DataEvent dataEvent = new DataEvent(tableName, DataEventType.DELETE,
                                                        null,
                                                        getData(tableName, deleteRowsEvent.getRows()));
                    txBuilder.addDataEvent(dataEvent);
                }
                break;

            case MySQLConstants.DELETE_ROWS_EVENT_V2:
                DeleteRowsEventV2 deleteRowsEventV2 = (DeleteRowsEventV2) event;
                tableName = tableCache.get(deleteRowsEventV2.getTableId());
                if (tableName != null && txBuilder.getInnerTxState() == TransactionState.STARTED) {

                    DataEvent dataEvent = new DataEvent(tableName, DataEventType.DELETE,
                                                        null,
                                                        getData(tableName, deleteRowsEventV2.getRows()));
                    txBuilder.addDataEvent(dataEvent);
                }
                break;

            case MySQLConstants.TABLE_MAP_EVENT:
                TableMapEvent tableMapEvent = (TableMapEvent) event;
                Long tableId = tableMapEvent.getTableId();
                if (tableCache.containsKey(tableId) == false) {
                    String databaseName = tableMapEvent.getDatabaseName().toString();
                    tableName = tableMapEvent.getTableName().toString();
                    if (databaseName.equals(this.databaseInfo.getDatabaseName())) {
                        if (this.databaseInfo.getAllTableNames().contains(tableName)) {
                            this.tableCache.put(tableId, tableName);
                        }
                    }
                }
                break;

            case MySQLConstants.QUERY_EVENT:
                QueryEvent queryEvent = (QueryEvent) event;
                String sql = queryEvent.getSql().toString();
                if ("BEGIN".equalsIgnoreCase(sql)) {
                    String databaseName = queryEvent.getDatabaseName().toString();
                    if (databaseName.equals(this.databaseInfo.getDatabaseName())) {
                        txBuilder.reset()
                                .txState(TransactionState.STARTED)
                                .txTimeStart(System.nanoTime())
                                .database(databaseName)
                                .serverId(toIntExact(queryEvent.getHeader().getServerId()))
                                .startBinLogFileName(queryEvent.getHeader().getBinlogFileName())
                                .startBinLogPos(toIntExact(queryEvent.getHeader().getPosition()));
                    }
                }
                break;

            case MySQLConstants.XID_EVENT:
                XidEvent xidEvent = (XidEvent) event;
                if (txBuilder.getInnerTxState() == TransactionState.STARTED &&
                    txBuilder.getInnerDataEvents().size() > 0 &&
                    txBuilder.getInnerServerId() == xidEvent.getHeader().getServerId()) {
                    TransactionEvent txEvent = txBuilder.txState(TransactionState.END)
                                                        .txTimeEnd(System.nanoTime())
                                                        .txId(xidEvent.getXid())
                                                        .endBinLogFileName(xidEvent.getHeader().getBinlogFileName())
                                                        .endBinLogPos(toIntExact(xidEvent.getHeader().getNextPosition()))
                                                        .build();
                    this.queue.offer(txEvent);
                }
                txBuilder.reset();
                break;
        }
    }
}
