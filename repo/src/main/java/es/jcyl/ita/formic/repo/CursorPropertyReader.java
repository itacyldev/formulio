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

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */

import android.database.Cursor;

import es.jcyl.ita.formic.repo.db.meta.DBPropertyType;
import es.jcyl.ita.formic.repo.db.sqlite.meta.types.SQLiteDBValue;
import es.jcyl.ita.formic.repo.db.sqlite.meta.types.SQLiteType;

/**
 * Helper class to read entities from and SQLite cursor
 */
public class CursorPropertyReader {

    public Object readPropertyValue(Cursor cursor, DBPropertyType p, int position) {
        Object value = null;
        if (cursor.isNull(position)) {
            value = null;
        } else {
            SQLiteType sqLiteType = SQLiteType.getType(p.getPersistenceType());
            switch (sqLiteType) {
                case INTEGER:
                    value = cursor.getLong(position);
                    break;
                case TEXT:
                    value = cursor.getString(position);
                    break;
                case REAL:
                    value = cursor.getDouble(position);
                    break;
                case BLOB:
                    value = cursor.getBlob(position);
                    break;
            }
            // convert value before setting in entity properties
            value = p.getConverter().toJava(new SQLiteDBValue(sqLiteType, value));
        }
        return value;
    }

}
