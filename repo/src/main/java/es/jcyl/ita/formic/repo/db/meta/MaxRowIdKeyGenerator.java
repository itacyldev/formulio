package es.jcyl.ita.formic.repo.db.meta;
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

import org.greenrobot.greendao.database.Database;
import org.mini2Dx.beanutils.ConvertUtils;

import java.util.Date;

import es.jcyl.ita.formic.repo.Entity;
import es.jcyl.ita.formic.repo.db.sqlite.greendao.EntityDao;
import es.jcyl.ita.formic.repo.db.sqlite.sql.SQLBuilder;
import es.jcyl.ita.formic.repo.meta.types.ByteArray;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 * <p>
 * Generates a new key for the entity returning the max value of rowid increased by 1.
 */
public class MaxRowIdKeyGenerator extends KeyGeneratorStrategy {
    private static final Class[] SUPPORTED_TYPES = {Date.class, Integer.class, Long.class, Short.class, Double.class, Float.class};

    public MaxRowIdKeyGenerator() {
        super(TYPE.MAXROWID);
    }

    @Override
    public boolean supports(Class type) {
        return type != ByteArray.class;
    }

    @Override
    protected <T> T doGetKey(EntityDao dao, Entity entity, Class<T> expectedType) {
        // query max row id for current table
        String query = SQLBuilder.createMaxRowId(entity);
        Database sqliteDB = (Database) dao.getDatabase();
        Cursor cursor = sqliteDB.rawQuery(query, new String[0]);

        long rowId;
        boolean available = cursor.moveToFirst();
        if (!available) {
            rowId = -1l;
        } else {
            rowId = cursor.getLong(0) + 1;
        }
        cursor.close();
        return (T) ConvertUtils.convert(rowId, expectedType);
    }

    @Override
    protected Class[] getSupportedTypes() {
        return SUPPORTED_TYPES;
    }
}
