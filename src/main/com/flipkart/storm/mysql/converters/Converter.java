package com.flipkart.storm.mysql.converters;

import com.flipkart.storm.mysql.schema.ColumnDataType;

public interface Converter {
    public Object convert(ColumnDataType columnDataType, Object value);
}
