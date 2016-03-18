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

/**
 * The datatype for the column.
 */
public enum ColumnDataType {

    /** The MySql Char Data Type. */
    CHAR,
    /** The MySql VarChar Data Type. */
    VARCHAR,
    /** The MySql Text Data Type. */
    TEXT,
    /** The MySql TinyText Data Type. */
    TINYTEXT,
    /** The MySql MediumText Data Type. */
    MEDIUMTEXT,
    /** The MySql LongText Data Type. */
    LONGTEXT,
    /** The MySql Decimal Data Type. */
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
    /** The MySql Int Data Type. */
    INT,
    /** The MySql Integer Data Type. */
    INTEGER,
    /** The MySql TinyInt Data Type. */
    TINYINT,
    /** The MySql SmallInt Data Type. */
    SMALLINT,
    /** The MySql MediumInt Data Type. */
    MEDIUMINT,
    /** The MySql Float Data Type. */
    FLOAT,
    /** The MySql Double Data Type. */
    DOUBLE,
    /** The MySql BigInt Data Type. */
    BIGINT,
    /** The MySql Date Data Type. */
    DATE,
    /** The MySql DateTime Data Type. */
    DATETIME,
    /** The MySql TimeStamp Data Type. */
    TIMESTAMP,
    /** The MySql Time Data Type. */
    TIME,
    /** The MySql Year Data Type. */
    YEAR;

    private Converter converter;

    /**
     * Prevent initialization.
     */
    ColumnDataType() { }

    /**
     * Get the converted value.
     *
     * @param colType the type of the column
     * @param value the value received from the bin logs
     * @return the converted object
     */
    public Object getConvertedValue(ColumnDataType colType, Object value) {
        return this.converter.convert(colType, value);
    }

    /**
     * Initialize the converter.
     * @return the column data types
     */
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

