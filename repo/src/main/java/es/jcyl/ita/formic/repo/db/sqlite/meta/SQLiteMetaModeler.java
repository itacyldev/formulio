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

import org.greenrobot.greendao.database.Database;

import java.util.ArrayList;
import java.util.List;

import es.jcyl.ita.formic.repo.RepositoryException;
import es.jcyl.ita.formic.repo.db.source.DBTableEntitySource;
import es.jcyl.ita.formic.repo.db.meta.DBPropertyType;
import es.jcyl.ita.formic.repo.db.sqlite.converter.SQLiteConverterFactory;
import es.jcyl.ita.formic.repo.db.sqlite.converter.SQLitePropertyConverter;
import es.jcyl.ita.formic.repo.db.sqlite.meta.types.SQLiteType;
import es.jcyl.ita.formic.repo.meta.EntityMeta;
import es.jcyl.ita.formic.repo.meta.MetaModeler;

/**
 * Reads entities meta description information from an SQLite source
 *
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */

public class SQLiteMetaModeler implements MetaModeler<DBTableEntitySource> {

    private static final String ONE = "1";
    private static final String PRAGMA_STATEMENT = "pragma table_info('%s')";
    SQLiteConverterFactory convFactory = SQLiteConverterFactory.getInstance();

    /**
     * Reads spatialite table metadata using pragma table_info statement and creates property
     * definitions using default converters.
     *
     * @param source: sqlite entity source pointing to table
     * @return
     */
    @Override
    public EntityMeta readFromSource(DBTableEntitySource source) {
        Database db = source.getDb();
        EntityMeta meta = null;
        String descStatement = String.format(PRAGMA_STATEMENT, source.getTableName());
        Cursor cursor = db.rawQuery(descStatement, null);

        boolean isPk, isNotNull;
        String name, type;
        List<String> idProperties = new ArrayList<String>();
        List<DBPropertyType> props = new ArrayList<DBPropertyType>();
        try {
            while (cursor.moveToNext()) {
                // cid, name, type, notnull, defaultValue, pk
                name = cursor.getString(1);
                type = cursor.getString(2);
                isNotNull = ONE.equalsIgnoreCase(cursor.getString(3));
                isPk = ONE.equalsIgnoreCase(cursor.getString(5));
                props.add(createPropertyFromColumnDef(name, type, isNotNull, isPk, source));
                if (isPk) {
                    idProperties.add(name);
                }
            }
        } finally {
            cursor.close();
        }
        if (props.size() == 0) {
            throw new RepositoryException(String.format("No table found with name [%s] in source [%s]",
                    source.getTableName(), source.getSourceId()));
        }
        DBPropertyType[] aProps = props.toArray(new DBPropertyType[props.size()]);
        meta = new EntityMeta(source.getTableName(), aProps, idProperties.toArray(new String[0]));
        return meta;
    }

    /**
     * Extension point to provide column definitions different from standard ones.
     *
     * @param name
     * @param type
     * @param isNotNull
     * @param isPk
     * @param source
     * @return
     */
    public DBPropertyType createPropertyFromColumnDef(String name, String type,
                                                         boolean isNotNull, boolean isPk,
                                                         DBTableEntitySource source) {
        SQLiteType dbType = SQLiteType.getType(type);
        SQLitePropertyConverter converter = convFactory.getDefaultConverter(dbType);
        DBPropertyType property = new DBPropertyType.DBPropertyTypeBuilder(name, converter.javaType(), dbType.name(), isPk)
                .withConverter(converter).build();
        property.setMandatory(isNotNull);
        return property;
    }

}
