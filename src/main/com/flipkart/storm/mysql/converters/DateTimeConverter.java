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

package com.flipkart.storm.mysql.converters;

import com.flipkart.storm.mysql.schema.ColumnDataType;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * Bin log event to DateTime Converter.
 */
public enum DateTimeConverter implements Converter {
    /** Ensures a singleton instance. */
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
