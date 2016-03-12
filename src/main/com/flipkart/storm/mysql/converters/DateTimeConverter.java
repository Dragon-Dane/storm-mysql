package com.flipkart.storm.mysql.converters;

import com.flipkart.storm.mysql.schema.ColumnDataType;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public enum DateTimeConverter implements Converter {
    INSTANCE;

    @Override
    public Object convert(ColumnDataType columnDataType, Object value) {
        if (value instanceof Date) {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            return df.format((Date) value);
        } else if (value instanceof Timestamp) {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return df.format((Timestamp) value);
        } else if ((value instanceof Integer) || (value instanceof Time)) {
            return value.toString();
        } else if (value instanceof java.util.Date) {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return df.format((java.util.Date) value);
        }
        return value;
    }
}
