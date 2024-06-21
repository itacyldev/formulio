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

import org.greenrobot.greendao.DaoException;

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

public class SQLiteRealConverter extends SQLitePropertyConverter {

    public SQLiteRealConverter(Class javaType) {
        super(SQLiteType.REAL, javaType);
    }

    @Override
    public SQLiteDBValue doToPersistence(Object value) {
        // REAL db data type is written as Doubles
        Double bValue = null;
        // convert object value to integer
        if (value instanceof Integer) {
            bValue = new Double((Integer) value);
        } else if (value instanceof Long) {
            bValue = new Double((Long) value);
        } else if (value instanceof Double) {
            bValue = (Double) value;
        } else if (value instanceof Float) {
            bValue = new Double((Float) value);
        } else if (value instanceof String) {
            bValue = Double.parseDouble((String) value);
        } else if (value instanceof Boolean) {
            bValue = ((Boolean) value) ? 1.0d : 0.0d;
        } else if (value instanceof Date) {
            // Java Unix time
            bValue = new Double(((Date) value).getTime());
        } else if (bValue == null) {
            throw new RepositoryException(String.format("Object type not supported: [%s].", value.getClass().getName()));
        }
        return new SQLiteDBValue(this.dbType, bValue);
    }

    /**
     * Converts to different Java types from SQLite INTEGER data type
     *
     * @param dbValue
     * @return
     */
    @Override
    public Object doToJava(SQLiteDBValue dbValue) {
        // REAL db data type is read as Doubles
        Double bValue;
        if (dbValue.value instanceof Double) {
            bValue = (Double) dbValue.value;
        } else if (dbValue.value instanceof Float) {
            bValue = new Double((float) dbValue.value);
        } else {
            throw new DaoException("Unexpected DB datatype to represent REAL value: " + dbValue.value.getClass().getName());
        }
        if (this.javaType == Long.class) {
            return bValue.longValue();
        } else if (this.javaType == Integer.class) {
            return bValue.intValue();
        } else if (this.javaType == Boolean.class) {
            // convert from current time millis
            return bValue > 0;
        } else if (this.javaType == Float.class) {
            // convert from current time millis
            return new Float(bValue);
        } else if (this.javaType == Double.class) {
            // convert from current time millis
            return bValue;
        } else if (this.javaType == Date.class) {
            return new Date(bValue.longValue());
        }
        throw new RepositoryException(String.format("Database type not supported: [%s].", this.javaType));
    }
}
