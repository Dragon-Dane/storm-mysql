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

package com.flipkart.storm.mysql.schema;

import com.flipkart.storm.mysql.converters.Converter;
import com.flipkart.storm.mysql.converters.DateTimeConverter;
import com.flipkart.storm.mysql.converters.NopConverter;
import com.flipkart.storm.mysql.converters.StringConverter;

public enum ColumnDataType {

    CHAR,
    VARCHAR,
    TEXT,
    TINYTEXT,
    MEDIUMTEXT,
    LONGTEXT,
    DECIMAL,
//    SET,
//    ENUM,
//    BINARY,
//    VARBINARY,
//    BIT,
//    BLOB,
//    TINYBLOB,
//    MEDIUMBLOB,
//    LONGBLOB,
    INT,
    INTEGER,
    TINYINT,
    SMALLINT,
    MEDIUMINT,
    FLOAT,
    DOUBLE,
    BIGINT,
    DATE,
    DATETIME,
    TIMESTAMP,
    TIME,
    YEAR;

    private Converter converter;

    private ColumnDataType() { }

    public Object getConvertedValue(ColumnDataType colType, Object value) {
        return this.converter.convert(colType, value);
    }

    public ColumnDataType initialize() {
        this.converter = getConverter(this);
        return this;
    }
    private Converter getConverter(ColumnDataType colType) {
        switch (colType) {
            case CHAR:
            case VARCHAR:
            case TEXT:
            case TINYTEXT:
            case MEDIUMTEXT:
            case LONGTEXT:
                return StringConverter.INSTANCE;

            case DECIMAL:
            case INT:
            case INTEGER:
            case TINYINT:
            case SMALLINT:
            case MEDIUMINT:
            case FLOAT:
            case DOUBLE:
            case BIGINT:
                return NopConverter.INSTANCE;

            case DATE:
            case DATETIME:
            case TIMESTAMP:
            case TIME:
            case YEAR:
                return DateTimeConverter.INSTANCE;

            default:
                throw new RuntimeException("This is an unsupported MySQL data type...");
        }
    }

}

