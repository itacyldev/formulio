package es.jcyl.ita.formic.repo.db.sqlite.converter;
/*
 * Copyright 2020 Gustavo Río (gustavo.rio@itacyl.es), ITACyL (http://www.itacyl.es).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.util.Date;

import es.jcyl.ita.formic.repo.RepositoryException;
import es.jcyl.ita.formic.repo.db.sqlite.meta.types.SQLiteDBValue;
import es.jcyl.ita.formic.repo.db.sqlite.meta.types.SQLiteType;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */

/**
 * Implements data type conversion from-to TEXT SQLite data type and Java objects
 */

public class SQLiteIntegerConverter extends SQLitePropertyConverter {


    public SQLiteIntegerConverter(Class javaType) {
        super(SQLiteType.INTEGER, javaType);
    }

    @Override
    public SQLiteDBValue doToPersistence(Object value) {
        // INTEGER db data type is written as Long
        Long lValue = null;
        // convert object value to integer
        if (value instanceof Integer) {
            lValue = ((Integer) value).longValue();
        } else if (value instanceof Long) {
            lValue = (Long) value;
        } else if (value instanceof Double) {
            lValue = ((Double) value).longValue();
        } else if (value instanceof Float) {
            lValue = ((Float) value).longValue();
        } else if (value instanceof String) {
            lValue = Long.parseLong((String) value);
        } else if (value instanceof Boolean) {
            lValue = ((Boolean) value) ? 1l : 0l;
        } else if (value instanceof Date) {
            // Java Unix time
            lValue = ((Date) value).getTime();
        }
        return new SQLiteDBValue(this.dbType, lValue);
    }


    /**
     * Converts to different Java types from SQLite INTEGER data type
     *
     * @param dbValue
     * @return
     */
    @Override
    public Object doToJava(SQLiteDBValue dbValue) {
        // INTEGER db data type is read as cursor.readLong
        Long lValue = (Long) dbValue.value;
        if (this.javaType == Long.class) {
            return lValue;
        } else if (this.javaType == Integer.class) {
            return lValue.intValue();
        } else if (this.javaType == Boolean.class) {
            // convert from current time millis
            return lValue > 0;
        } else if (this.javaType == Float.class) {
            // convert from current time millis
            return new Float(lValue);
        } else if (this.javaType == Double.class) {
            // convert from current time millis
            return new Double(lValue);
        } else if (this.javaType == Date.class) {
            // convert from current time millis Java Unix time
            return new Date(lValue);
        }
        throw new RepositoryException(String.format("Database type not supported: [%s].", this.javaType));
    }
}
