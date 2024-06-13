package es.jcyl.ita.formic.repo;
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

import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.database.DatabaseStatement;

import es.jcyl.ita.formic.repo.db.meta.DBPropertyType;
import es.jcyl.ita.formic.repo.db.sqlite.meta.types.SQLiteDBValue;
import es.jcyl.ita.formic.repo.meta.types.ByteArray;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */

public class CursorPropertyBinder {

    public void bindValue(DatabaseStatement stmt, DBPropertyType property, int columnIndex, Object value) {
        SQLiteDBValue dbValue = property.getConverter().toPersistence(value);
        if (dbValue.value == null) {
            stmt.bindNull(columnIndex);
        } else {
            switch (dbValue.dbType) {
                case INTEGER:
                    stmt.bindLong(columnIndex, (long) dbValue.value);
                    break;
                case TEXT:
                    stmt.bindString(columnIndex, (String) dbValue.value);
                    break;
                case REAL:
                    stmt.bindDouble(columnIndex, (double) dbValue.value);
                    break;
                case BLOB:
                    bindBlobValue(stmt, columnIndex, dbValue.value);
                    break;
            }
        }
    }

    public void bindValue(SQLiteStatement stmt, DBPropertyType property, int columnIndex, Object value) {
        SQLiteDBValue dbValue = property.getConverter().toPersistence(value);
        if (dbValue.value == null) {
            stmt.bindNull(columnIndex);
        } else {
            switch (dbValue.dbType) {
                case INTEGER:
                    stmt.bindLong(columnIndex, (long) dbValue.value);
                    break;
                case TEXT:
                    stmt.bindString(columnIndex, (String) dbValue.value);
                    break;
                case REAL:
                    stmt.bindDouble(columnIndex, (double) dbValue.value);
                    break;
                case BLOB:
                    bindBlobValue(stmt, columnIndex, dbValue.value);
                    break;
            }
        }
    }

    protected void bindBlobValue(SQLiteStatement stmt, int columnIndex, Object value) {
        ByteArray ba = (ByteArray) value;
        stmt.bindBlob(columnIndex, ba.getValue());
    }

    protected void bindBlobValue(DatabaseStatement stmt, int columnIndex, Object value) {
        ByteArray ba = (ByteArray) value;
        stmt.bindBlob(columnIndex, ba.getValue());
    }
}
