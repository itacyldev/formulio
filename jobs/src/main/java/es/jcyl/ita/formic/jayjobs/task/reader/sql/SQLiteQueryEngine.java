package es.jcyl.ita.formic.jayjobs.task.reader.sql;
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

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import es.jcyl.ita.formic.core.context.CompositeContext;
import es.jcyl.ita.formic.jayjobs.task.models.PaginatedList;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class SQLiteQueryEngine {

    public PaginatedList execute(String query, String dbFile, CompositeContext globalContext, Integer pageSize, Integer offset) {
        PaginatedList<String> lst = new PaginatedList<>();

        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(dbFile, null);

        try (Cursor res = db.rawQuery(query, null)) {
            res.moveToFirst();
            while (res.isAfterLast() == false) {
                lst.add(res.getString(res.getColumnIndex("a")));
                res.moveToNext();
            }
        }
        return lst;
    }
}
