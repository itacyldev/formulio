package es.jcyl.ita.formic.repo.db.source;
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

import android.database.sqlite.SQLiteDatabase;

import org.apache.commons.lang3.StringUtils;
import org.greenrobot.greendao.database.Database;

import es.jcyl.ita.formic.repo.RepositoryException;
import es.jcyl.ita.formic.repo.builders.AbstractEntitySourceBuilder;
import es.jcyl.ita.formic.repo.db.spatialite.greendao.SpatialiteDataBase;
import es.jcyl.ita.formic.repo.source.AbstractEntitySource;
import es.jcyl.ita.formic.repo.source.EntitySource;
import es.jcyl.ita.formic.repo.source.EntitySourceFactory;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */

public class NativeSQLEntitySource extends AbstractEntitySource implements EntitySource {

    private String query;
    private Database db;

    public NativeSQLEntitySource() {
    }

    public NativeSQLEntitySource(Database db, String query) {
        this.db = db;
        this.query = query;
        if (StringUtils.isBlank(query)) {
            throw new IllegalArgumentException("The query provided is null!");
        }
        // TODO: create entityType from query
        this.entityTypeId = "" + query.hashCode();
    }

    @Override
    public String toString() {
        return String.format("%s@query_%s", this.db.toString(), this.entityTypeId);
    }

    @Override
    public String getSourceId() {
        Object rawDB = this.db.getRawDatabase();
        if (rawDB instanceof SQLiteDatabase) {
            return ((SQLiteDatabase) rawDB).getPath();
        }
        if (this.db instanceof SpatialiteDataBase) {
            return ((SpatialiteDataBase) this.db).getPath();
        }
        throw new RepositoryException("Unsupported database type: " + rawDB.getClass().getName());
    }

    @Override
    public String getEntityTypeId() {
        return this.entityTypeId;
    }

    public String getQuery() {
        return query;
    }

    public Database getDb() {
        return db;
    }


    public class NativeSQLEntitySourceBuilder extends AbstractEntitySourceBuilder<NativeSQLEntitySource> {

        public static final String QUERY = "QUERY";

        public NativeSQLEntitySourceBuilder(EntitySourceFactory factory) {
            super(factory);
        }

        @Override
        protected NativeSQLEntitySource doBuild() {
            NativeSQLEntitySource instance = new NativeSQLEntitySource();
            Object implementor = this.getSource().getImplementor();
            if (!(implementor instanceof Database)) {
                throw new IllegalArgumentException("The source object has and unexpected class type: "
                        + implementor.getClass().getName() + ". Expected: " + Database.class.getName()
                );
            }
            String query = this.getProperty(QUERY, String.class);
            if (StringUtils.isBlank(query)) {
                throw new IllegalArgumentException("No query provided for the EntitySource, " +
                        "set property QUERY to the builder.");
            }
            Database db = (Database) implementor;
            instance.db = db;
            instance.query = query;
            instance.source = this.getSource();
            instance.entityTypeId = this.getEntityTypeId();
            return instance;
        }
    }
}
