package es.jcyl.ita.formic.forms.config.reader.dummy;
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

import org.greenrobot.greendao.database.StandardDatabase;

import java.io.File;

import es.jcyl.ita.formic.repo.builders.EntitySourceBuilder;
import es.jcyl.ita.formic.repo.builders.RepositoryBuilder;
import es.jcyl.ita.formic.repo.config.AbstractRepoConfigurationReader;
import es.jcyl.ita.formic.repo.db.builders.SQLiteGreenDAORepoBuilder;
import es.jcyl.ita.formic.repo.db.source.DBTableEntitySource;
import es.jcyl.ita.formic.repo.db.source.NativeSQLEntitySource;
import es.jcyl.ita.formic.repo.db.spatialite.SpatialitePropertyBinder;
import es.jcyl.ita.formic.repo.db.spatialite.greendao.SpatialTableStatementsProvider;
import es.jcyl.ita.formic.repo.db.spatialite.greendao.SpatialiteDataBase;
import es.jcyl.ita.formic.repo.db.spatialite.meta.SpatiaLiteMetaModeler;
import es.jcyl.ita.formic.repo.db.sqlite.greendao.EntityDaoConfig;
import es.jcyl.ita.formic.repo.db.sqlite.meta.SQLiteMetaModeler;
import es.jcyl.ita.formic.repo.meta.EntityMeta;
import es.jcyl.ita.formic.repo.meta.MetaModeler;
import es.jcyl.ita.formic.repo.source.EntitySource;
import es.jcyl.ita.formic.repo.source.EntitySourceFactory;
import es.jcyl.ita.formic.repo.source.Source;

import static es.jcyl.ita.formic.repo.builders.AbstractEntitySourceBuilder.ENTITY_TYPE_ID;
import static es.jcyl.ita.formic.repo.builders.AbstractEntitySourceBuilder.SOURCE;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 *
 * Helper to create repositories during the config. reading process.
 */


public class RepositoryConfReaderHelper extends AbstractRepoConfigurationReader {


    @Override
    public void read() {
        // smoke-default implementations
        createDBSource();
        createEntitySources();
        createRepositories();
    }

    private void createRepositories() {
        // create repository
        EntitySource eSource = sourceFactory.getEntitySource("contacts");
        MetaModeler metaModeler = new SQLiteMetaModeler();
        EntityMeta meta = metaModeler.readFromSource(eSource);
        RepositoryBuilder builder = repoFactory.getBuilder(eSource);
        EntityDaoConfig conf = new EntityDaoConfig(meta, (DBTableEntitySource) eSource);
        builder.withProperty(SQLiteGreenDAORepoBuilder.ENTITY_CONFIG, conf);
        builder.build();

        eSource = sourceFactory.getEntitySource("filteredContacts");
        builder = repoFactory.getBuilder(eSource);
        builder.build();

        eSource = sourceFactory.getEntitySource("provincia");
        metaModeler = new SQLiteMetaModeler();
        meta = metaModeler.readFromSource(eSource);
        builder = repoFactory.getBuilder(eSource);
        conf = new EntityDaoConfig(meta, (DBTableEntitySource) eSource);
        builder.withProperty(SQLiteGreenDAORepoBuilder.ENTITY_CONFIG, conf);
        builder.build();

        eSource = sourceFactory.getEntitySource("municipio");
        metaModeler = new SQLiteMetaModeler();
        meta = metaModeler.readFromSource(eSource);
        builder = repoFactory.getBuilder(eSource);
        conf = new EntityDaoConfig(meta, (DBTableEntitySource) eSource);
        builder.withProperty(SQLiteGreenDAORepoBuilder.ENTITY_CONFIG, conf);
        builder.build();

        eSource = sourceFactory.getEntitySource("agents");
        metaModeler = new SQLiteMetaModeler();
        meta = metaModeler.readFromSource(eSource);
        builder = repoFactory.getBuilder(eSource);
        conf = new EntityDaoConfig(meta, (DBTableEntitySource) eSource);
        builder.withProperty(SQLiteGreenDAORepoBuilder.ENTITY_CONFIG, conf);
        builder.build();

//         create repository against spatialite database
        eSource = sourceFactory.getEntitySource("inspecciones");
        metaModeler = new SpatiaLiteMetaModeler();
        meta = metaModeler.readFromSource(eSource);
        builder = repoFactory.getBuilder(eSource);
        conf = new EntityDaoConfig(meta, (DBTableEntitySource) eSource);
        conf.setPropertyBinder(new SpatialitePropertyBinder());
        conf.setTableStatementsProvider(new SpatialTableStatementsProvider());
        builder.withProperty(SQLiteGreenDAORepoBuilder.ENTITY_CONFIG, conf);
        builder.build();
    }

    private void createEntitySources() {
        EntitySourceBuilder builder;
        builder = sourceFactory.getBuilder(EntitySourceFactory.SOURCE_TYPE.SQLITE);
        builder.withProperty(SOURCE, this.sourceFactory.getSource("ribera"));
        builder.withProperty(DBTableEntitySource.DBTableEntitySourceBuilder.TABLE_NAME, "inspecciones");
        builder.withProperty(ENTITY_TYPE_ID, "inspecciones");
        builder.build();

        builder = sourceFactory.getBuilder(EntitySourceFactory.SOURCE_TYPE.SQLITE);
        builder.withProperty(SOURCE, this.sourceFactory.getSource("dbTest"));
        builder.withProperty(ENTITY_TYPE_ID, "contacts");
        builder.withProperty(DBTableEntitySource.DBTableEntitySourceBuilder.TABLE_NAME, "contacts");
        builder.build();

        builder = sourceFactory.getBuilder(EntitySourceFactory.SOURCE_TYPE.SQLITE_CURSOR);
        builder.withProperty(SOURCE, this.sourceFactory.getSource("dbTest"));
        builder.withProperty(ENTITY_TYPE_ID, "filteredContacts");
        String query = "select * from contacts where first_name like '%${view.f0}%'";
        builder.withProperty(NativeSQLEntitySource.NativeSQLEntitySourceBuilder.QUERY, query);
        builder.build();

        builder = sourceFactory.getBuilder(EntitySourceFactory.SOURCE_TYPE.SQLITE);
        builder.withProperty(SOURCE, this.sourceFactory.getSource("dbTest"));
        builder.withProperty(ENTITY_TYPE_ID, "provincia");
        builder.withProperty(DBTableEntitySource.DBTableEntitySourceBuilder.TABLE_NAME, "provincia");
        builder.build();

        builder = sourceFactory.getBuilder(EntitySourceFactory.SOURCE_TYPE.SQLITE);
        builder.withProperty(SOURCE, this.sourceFactory.getSource("dbTest"));
        builder.withProperty(ENTITY_TYPE_ID, "municipio");
        builder.withProperty(DBTableEntitySource.DBTableEntitySourceBuilder.TABLE_NAME, "municipio");
        builder.build();


        builder = sourceFactory.getBuilder(EntitySourceFactory.SOURCE_TYPE.SQLITE);
        builder.withProperty(SOURCE, this.sourceFactory.getSource("dbTest"));
        builder.withProperty(ENTITY_TYPE_ID, "agents");
        builder.withProperty(DBTableEntitySource.DBTableEntitySourceBuilder.TABLE_NAME, "assigned_agents");
        builder.build();
    }

    private void createDBSource() {
        File dbFile = new File("/sdcard/test/ribera.sqlite");
        SQLiteDatabase.openOrCreateDatabase(dbFile, null);
        SpatialiteDataBase db = new SpatialiteDataBase(dbFile.getAbsolutePath(), new jsqlite.Database());
        this.sourceFactory.registerSource(new Source<>("ribera", dbFile.getAbsolutePath(), db));

        dbFile = new File("/sdcard/test/dbTest.sqlite");
        SQLiteDatabase sqDb = SQLiteDatabase.openOrCreateDatabase(dbFile, null);
        this.sourceFactory.registerSource(new Source<>("dbTest", dbFile.getAbsolutePath(), new StandardDatabase(sqDb)));
    }
}
