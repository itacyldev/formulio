package es.jcyl.ita.formic.repo.db.sqlite;
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
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import es.jcyl.ita.formic.core.context.VarSubstitutor;
import es.jcyl.ita.formic.repo.AbstractBaseRepository;
import es.jcyl.ita.formic.repo.CursorPropertyReader;
import es.jcyl.ita.formic.repo.Entity;
import es.jcyl.ita.formic.repo.db.SQLQueryFilter;
import es.jcyl.ita.formic.repo.db.meta.DBPropertyType;
import es.jcyl.ita.formic.repo.db.source.NativeSQLEntitySource;
import es.jcyl.ita.formic.repo.db.sqlite.converter.SQLitePropertyConverter;
import es.jcyl.ita.formic.repo.db.sqlite.meta.SQLiteCursorMetaModeler;
import es.jcyl.ita.formic.repo.db.sqlite.sql.SQLBuilder;
import es.jcyl.ita.formic.repo.db.sqlite.sql.SQLSelectBuilder;
import es.jcyl.ita.formic.repo.meta.EntityMeta;
import es.jcyl.ita.formic.repo.meta.MetaModeler;
import es.jcyl.ita.formic.repo.source.EntitySource;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */

/**
 * Basic repository implementation to extract entities from a SQLite dataSource using a
 * native SQL query.
 */
public class RawSQLiteRepository extends AbstractBaseRepository<Entity, SQLQueryFilter> {

    private final NativeSQLEntitySource source;
    private String query;
    private EntityMeta meta;
    private Map<String, SQLitePropertyConverter> converters;

    private CursorPropertyReader propertyReader;

    public RawSQLiteRepository(NativeSQLEntitySource source) {
        this(source, null);
    }

    public RawSQLiteRepository(NativeSQLEntitySource source, Map<String, SQLitePropertyConverter> converters) {
        this.source = source;
        this.query = source.getQuery();
        this.converters = converters;
        propertyReader = new CursorPropertyReader();
    }

    @Override
    public List<Entity> doFind(SQLQueryFilter filter) {
        // TODO: filter is not yet used, pagination and additional filtering must be implemented
        // evaluate Query
        String effQuery = SQLSelectBuilder.paginate(this.query, filter);
        effQuery = VarSubstitutor.replace(effQuery, this.context);
        Cursor cursor = source.getDb().rawQuery(effQuery, null);
        return loadAllAndCloseCursor(cursor);
    }

    @Override
    public long count(SQLQueryFilter filter) {
        String effQuery = SQLBuilder.countQuery(this.query);
        Cursor cursor = source.getDb().rawQuery(effQuery, null);
        try {
            cursor.moveToNext();
            return cursor.getLong(1);
        } finally {
            cursor.close();
        }
    }

    @Override
    protected List<Entity> doListAll() {
        return find(null);
    }

    /**
     * Reads entity metadata from Cursor information
     *
     * @param cursor
     * @return
     */
    private EntityMeta readEntityMeta(Cursor cursor) {
        MetaModeler modeler = new SQLiteCursorMetaModeler();
        EntityMeta meta = modeler.readFromSource(cursor);
        // load default converters per each data type
        return meta;
    }

    protected List<Entity> loadAllAndCloseCursor(Cursor cursor) {
        List var2;
        try {
            var2 = this.loadAllFromCursor(cursor);
        } finally {
            cursor.close();
        }
        return var2;
    }

    protected List<Entity> loadAllFromCursor(Cursor cursor) {
        int count = ((Cursor) cursor).getCount();
        if (count == 0) {
            return new ArrayList();
        } else {
            List<Entity> list = new ArrayList(count);
            if (cursor.moveToFirst()) {
                if (this.meta == null) {
                    try {
                        this.meta = readEntityMeta(cursor);
                    } catch (Exception e) {
                        Log.e("dbRepo", "Error while trying to read entity meta [%s]."
                                + e.getLocalizedMessage());
                        throw e;
                    }
                }
                Entity entity;
                do {
                    entity = this.readEntity(cursor, 0);
                    list.add(entity);
                } while (cursor.moveToNext());
            }
            return list;
        }
    }

    protected Entity readEntity(Cursor cursor, int offset) {
        Entity entity = new Entity(source, meta, 0);
        readEntity(cursor, entity);
        return entity;
    }

    protected void readEntity(Cursor cursor, Entity entity) {
        SQLitePropertyConverter converter;
        int i = 0;
        EntityMeta<DBPropertyType> meta = entity.getMetadata();
        for (DBPropertyType p : meta.getProperties()) {
            // read db value according to defined persistence type
//            Object value = readPropertyValue(cursor, p, i);
            Object value = propertyReader.readPropertyValue(cursor, p, i);
            entity.set(p.getName(), value);
            i++;
        }
    }
//
//    private Object readPropertyValue(Cursor cursor, PropertyType p, int position) {
//        Object value = null;
//        if (cursor.isNull(position)) {
//            value = null;
//        } else {
//            // TODO: apply converters
//            value = cursor.getString(position);
//        }
//        return value;
//    }


    @Override
    public EntitySource getSource() {
        return null;
    }

    @Override
    public EntityMeta doGetMeta() {
        if (this.meta == null) {
            // execute a one record query to extract metadata information
            SQLQueryFilter f = new SQLQueryFilter();
            f.setPageSize(1);
            this.find(f);
        }
        // after the first read, the meta object is already fulfill
        return meta;
    }

    @Override
    public Object getImplementor() {
        return this;
    }

    @Override
    public Class<SQLQueryFilter> getFilterClass() {
        return SQLQueryFilter.class;
    }
}
