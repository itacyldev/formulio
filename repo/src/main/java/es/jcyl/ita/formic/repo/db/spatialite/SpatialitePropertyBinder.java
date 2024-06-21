package es.jcyl.ita.formic.repo.db.spatialite;
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

import es.jcyl.ita.formic.repo.CursorPropertyBinder;
import es.jcyl.ita.formic.repo.meta.types.Geometry;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */

public class SpatialitePropertyBinder extends CursorPropertyBinder {


    protected void bindBlobValue(SQLiteStatement stmt, int columnIndex, Object value) {
        if (value instanceof Geometry) {
            // set geometry wkt as string
            stmt.bindString(columnIndex, ((Geometry) value).getValue());
        } else {
            super.bindBlobValue(stmt, columnIndex, value);
        }
    }

    protected void bindBlobValue(DatabaseStatement stmt, int columnIndex, Object value) {
        if (value instanceof Geometry) {
            // set geometry wkt as string
            stmt.bindString(columnIndex, ((Geometry) value).getValue());
        } else {
            super.bindBlobValue(stmt, columnIndex, value);
        }
    }
}
