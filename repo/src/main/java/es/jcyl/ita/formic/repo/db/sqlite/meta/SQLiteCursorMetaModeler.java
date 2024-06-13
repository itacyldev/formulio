package es.jcyl.ita.formic.repo.db.sqlite.meta;
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

import es.jcyl.ita.formic.repo.converter.PropertyConverter;
import es.jcyl.ita.formic.repo.db.meta.DBPropertyType;
import es.jcyl.ita.formic.repo.db.sqlite.converter.SQLiteConverterFactory;
import es.jcyl.ita.formic.repo.db.sqlite.converter.SQLitePropertyConverter;
import es.jcyl.ita.formic.repo.db.sqlite.meta.types.SQLiteType;
import es.jcyl.ita.formic.repo.meta.EntityMeta;
import es.jcyl.ita.formic.repo.meta.MetaModeler;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */

/**
 * Read entities meta description information from an SQLite source
 */
public class SQLiteCursorMetaModeler implements MetaModeler<Cursor> {

    @Override
    public EntityMeta readFromSource(Cursor cursor) {
        SQLiteConverterFactory convFactory = SQLiteConverterFactory.getInstance();

        String[] columnsNames = cursor.getColumnNames();

        DBPropertyType[] props = new DBPropertyType[columnsNames.length];
        int index = 0;
        for (String name : columnsNames) {
            int type = cursor.getType(index);
            SQLiteType dbType = SQLiteType.getType(type);
            PropertyConverter converter = convFactory.getDefaultConverter(dbType);
//            props[index] = new DBPropertyType(name, converter.javaType(), dbType.name(), false);
            DBPropertyType.DBPropertyTypeBuilder builder = new DBPropertyType.DBPropertyTypeBuilder(name,
                    converter.javaType(), dbType.name(), false);
            props[index] = builder.withConverter((SQLitePropertyConverter) converter).build();// ummm
            index++;
        }
        EntityMeta<DBPropertyType> meta = new EntityMeta("cursor", props, new String[]{});

        return meta;
    }
}
