package es.jcyl.ita.frmdrd.configuration;
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

import es.jcyl.ita.crtrepo.EntitySource;
import es.jcyl.ita.crtrepo.EntitySourceFactory;
import es.jcyl.ita.crtrepo.Source;
import es.jcyl.ita.crtrepo.builders.EntitySourceBuilder;
import es.jcyl.ita.crtrepo.builders.RepositoryBuilder;
import es.jcyl.ita.crtrepo.builders.SQLiteGreenDAORepoBuilder;
import es.jcyl.ita.crtrepo.config.AbstractRepoConfigurationReader;
import es.jcyl.ita.crtrepo.db.DBTableEntitySource;
import es.jcyl.ita.crtrepo.db.NativeSQLEntitySource;
import es.jcyl.ita.crtrepo.db.sqlite.greendao.EntityDaoConfig;
import es.jcyl.ita.crtrepo.db.sqlite.meta.SQLiteMetaModeler;
import es.jcyl.ita.crtrepo.meta.EntityMeta;
import es.jcyl.ita.crtrepo.meta.MetaModeler;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */

public class RepositoryProjectConfReader extends AbstractRepoConfigurationReader {

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



        // create repository against spatialite database
//        eSource = sourceFactory.getEntitySource("inspecciones");
//        metaModeler = new SpatiaLiteMetaModeler();
//        meta = metaModeler.readFromSource(eSource);
//        builder = repoFactory.getBuilder(eSource);
//        conf = new EntityDaoConfig(meta, (DBTableEntitySource) eSource);
//        conf.setPropertyBinder(new SpatialitePropertyBinder());
//        conf.setTableStatementsProvider(new SpatialTableStatementsProvider());
//        builder.withProperty(SQLiteGreenDAORepoBuilder.ENTITY_CONFIG, conf);
//        builder.build();
    }

    private void createEntitySources() {
        EntitySourceBuilder builder;
//        builder = sourceFactory.getBuilder(EntitySourceFactory.SOURCE_TYPE.SQLITE);
//        builder.withProperty(DBTableEntitySource.DBTableEntitySourceBuilder.SOURCE, this.sourceFactory.getSource("ribera"));
//        builder.withProperty(DBTableEntitySource.DBTableEntitySourceBuilder.TABLE_NAME, "inspecciones");
//        builder.withProperty(DBTableEntitySource.DBTableEntitySourceBuilder.ENTITY_TYPE_ID, "inspecciones");
//        builder.build();

        builder = sourceFactory.getBuilder(EntitySourceFactory.SOURCE_TYPE.SQLITE);
        builder.withProperty(DBTableEntitySource.DBTableEntitySourceBuilder.SOURCE, this.sourceFactory.getSource("dbTest"));
        builder.withProperty(DBTableEntitySource.DBTableEntitySourceBuilder.ENTITY_TYPE_ID, "contacts");
        builder.withProperty(DBTableEntitySource.DBTableEntitySourceBuilder.TABLE_NAME, "contacts");
        builder.build();

        builder = sourceFactory.getBuilder(EntitySourceFactory.SOURCE_TYPE.SQLITE_CURSOR);
        builder.withProperty(NativeSQLEntitySource.NativeSQLEntitySourceBuilder.SOURCE, this.sourceFactory.getSource("dbTest"));
        builder.withProperty(NativeSQLEntitySource.NativeSQLEntitySourceBuilder.ENTITY_TYPE_ID, "filteredContacts");
        String query = "select * from contacts where first_name like '%${view.f0}%'";
        builder.withProperty(NativeSQLEntitySource.NativeSQLEntitySourceBuilder.QUERY, query);
        builder.build();


    }

    private void createDBSource() {
//        File dbFile = new File("/sdcard/test/ribera.sqlite");
//        SpatialiteDataBase db = new SpatialiteDataBase(dbFile.getAbsolutePath(), new Database());
//        this.sourceFactory.registerSource(new Source<>("ribera", dbFile.getAbsolutePath(), db));

        File dbFile = new File("/sdcard/test/dbTest.sqlite");
        SQLiteDatabase sqDb = SQLiteDatabase.openOrCreateDatabase(dbFile, null);
        this.sourceFactory.registerSource(new Source<>("dbTest", dbFile.getAbsolutePath(), new StandardDatabase(sqDb)));
    }
}
