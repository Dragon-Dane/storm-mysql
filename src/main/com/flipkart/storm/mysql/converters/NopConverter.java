package com.flipkart.storm.mysql.converters;

import com.flipkart.storm.mysql.schema.ColumnDataType;

public enum NopConverter implements Converter {
    INSTANCE;

    @Override
    public Object convert(ColumnDataType columnDataType, Object value) {
       return value;
    }
}
