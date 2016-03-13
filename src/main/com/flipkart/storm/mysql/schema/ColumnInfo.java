package com.flipkart.storm.mysql.schema;

public class ColumnInfo {

    private final String            name;
    private final int               position;
    private final ColumnDataType    columnDataType;

    public ColumnInfo(String name, int position, String mysqlType) {
        this.name = name;
        this.position = position;
        this.columnDataType = ColumnDataType.valueOf(mysqlType.toUpperCase()).initialize();
    }

    public String getName() {
        return name;
    }

    public int getPosition() {
        return position;
    }

    public ColumnDataType getColumnDataType() {
        return columnDataType;
    }

    @Override
    public String toString() {
        return "ColumnInfo{" +
                "name='" + name + '\'' +
                ", position=" + position +
                ", columnDataType=" + columnDataType +
                '}';
    }
}
