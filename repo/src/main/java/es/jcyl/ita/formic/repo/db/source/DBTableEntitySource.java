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

import org.apache.commons.lang3.StringUtils;
import org.greenrobot.greendao.database.Database;

import es.jcyl.ita.formic.repo.builders.AbstractEntitySourceBuilder;
import es.jcyl.ita.formic.repo.source.AbstractEntitySource;
import es.jcyl.ita.formic.repo.source.EntitySource;
import es.jcyl.ita.formic.repo.source.EntitySourceFactory;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */

public class DBTableEntitySource extends AbstractEntitySource implements EntitySource {

    private String tableName;
    private Database db;


    public DBTableEntitySource() {
    }

    public DBTableEntitySource(Database db, String tableName) {
        this.db = db;
        this.tableName = tableName;
    }

    public String getTableName() {
        return tableName;
    }

    public Database getDb() {
        return db;
    }

    @Override
    public String toString() {
        return String.format("%s@%s", this.source.getURI(), this.tableName);
    }


    public class DBTableEntitySourceBuilder extends AbstractEntitySourceBuilder<DBTableEntitySource> {

        public static final String TABLE_NAME = "TABLE_NAME";

        public DBTableEntitySourceBuilder(EntitySourceFactory factory) {
            super(factory);
        }

        @Override
        protected DBTableEntitySource doBuild() {
            DBTableEntitySource instance = new DBTableEntitySource();
            Object implementor = this.getSource().getImplementor();
            if (!(implementor instanceof Database)) {
                throw new IllegalArgumentException("The source object has and unexpected class type: "
                        + implementor.getClass().getName() + ". Expected: " + Database.class.getName()
                );
            }
            String tableName = this.getProperty(TABLE_NAME, String.class);
            if (StringUtils.isBlank(tableName)) {
                throw new IllegalArgumentException("No tableName provided for the EntitySource, " +
                        "set property TABLE_NAME to the builder.");
            }
            Database db = (Database) implementor;
            instance.db = db;
            instance.tableName = tableName;
            instance.source = this.getSource();
            instance.entityTypeId = this.getEntityTypeId();
            return instance;
        }

    }
}
