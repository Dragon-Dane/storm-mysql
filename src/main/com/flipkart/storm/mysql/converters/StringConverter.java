package com.flipkart.storm.mysql.converters;

import com.flipkart.storm.mysql.schema.ColumnDataType;

public enum StringConverter implements Converter {
    INSTANCE;

    @Override
    public Object convert(ColumnDataType columnDataType, Object value) {
        if ( value instanceof String ) {
            return value;
        }
        return new String((byte[])value);
    }
}
