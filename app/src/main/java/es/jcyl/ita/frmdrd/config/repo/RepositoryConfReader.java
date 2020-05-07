package es.jcyl.ita.frmdrd.config.repo;
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
import es.jcyl.ita.crtrepo.Repository;
import es.jcyl.ita.crtrepo.Source;
import es.jcyl.ita.crtrepo.builders.EntitySourceBuilder;
import es.jcyl.ita.crtrepo.builders.RepositoryBuilder;
import es.jcyl.ita.crtrepo.builders.SQLiteGreenDAORepoBuilder;
import es.jcyl.ita.crtrepo.config.AbstractRepoConfigurationReader;
import es.jcyl.ita.crtrepo.db.DBTableEntitySource;
import es.jcyl.ita.crtrepo.db.NativeSQLEntitySource;
import es.jcyl.ita.crtrepo.db.spatialite.SpatialitePropertyBinder;
import es.jcyl.ita.crtrepo.db.spatialite.greendao.SpatialTableStatementsProvider;
import es.jcyl.ita.crtrepo.db.spatialite.greendao.SpatialiteDataBase;
import es.jcyl.ita.crtrepo.db.spatialite.meta.SpatiaLiteMetaModeler;
import es.jcyl.ita.crtrepo.db.sqlite.greendao.EntityDaoConfig;
import es.jcyl.ita.crtrepo.db.sqlite.meta.SQLiteMetaModeler;
import es.jcyl.ita.crtrepo.meta.EntityMeta;
import es.jcyl.ita.crtrepo.meta.MetaModeler;
import es.jcyl.ita.frmdrd.config.ConfigurationException;
import es.jcyl.ita.frmdrd.config.DevConsole;

import static es.jcyl.ita.frmdrd.config.DevConsole.error;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */

public class RepositoryConfReader extends AbstractRepoConfigurationReader {


    private final String baseFolder;

    public RepositoryConfReader(String folder) {
        this.baseFolder = folder;
    }

    @Override
    public void read() {
        // smoke-default implementations
        createDBSource();
        createEntitySources();
        createRepositories();
    }

    private File locateFile(String path) {
        File f = new File(path);
        if (f.isAbsolute()) {
            if (!f.exists()) {
                throw new ConfigurationException(error("Error during RepoConfReader " +
                        "initialization, file not found: " + path));
            }
        } else {
            f = new File(baseFolder, path);
            if (!f.exists()) {
                throw new ConfigurationException(error(String.format("Error during " +
                        "RepoConfReader initialization, file not found in base folder [%s]: ", baseFolder) + path));
            }
        }
        return f;
    }

    public Repository createFromFile(String filePath, String tableName) {
        File dbFile = locateFile(filePath);
        // check if exists another repository against that source
        String absPath = dbFile.getAbsolutePath();
        Source source = this.sourceFactory.getSource(absPath);
        if (source == null) {
            SQLiteDatabase sqDb = SQLiteDatabase.openOrCreateDatabase(dbFile, null);
            // use absolute path as source Id
            source = new Source<>(absPath, absPath, new StandardDatabase(sqDb));
            this.sourceFactory.registerSource(source);
        }

        // create entity source
        String entityId = absPath + "#" + tableName;
        EntitySource eSource = sourceFactory.getEntitySource(entityId);
        if (eSource == null) {
            EntitySourceBuilder builder;
            builder = sourceFactory.getBuilder(EntitySourceFactory.SOURCE_TYPE.SQLITE);
            builder.withProperty(DBTableEntitySource.DBTableEntitySourceBuilder.SOURCE, source);
            builder.withProperty(DBTableEntitySource.DBTableEntitySourceBuilder.TABLE_NAME, tableName);
            builder.withProperty(DBTableEntitySource.DBTableEntitySourceBuilder.ENTITY_TYPE_ID, entityId);
            builder.build();
        }

        // create repository
        MetaModeler metaModeler = new SQLiteMetaModeler();
        EntityMeta meta = metaModeler.readFromSource(eSource);
        EntityDaoConfig conf = new EntityDaoConfig(meta, (DBTableEntitySource) eSource);
        RepositoryBuilder builder = repoFactory.getBuilder(eSource);
        builder.withProperty(SQLiteGreenDAORepoBuilder.ENTITY_CONFIG, conf);
        return builder.build();
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
        builder.withProperty(DBTableEntitySource.DBTableEntitySourceBuilder.SOURCE, this.sourceFactory.getSource("ribera"));
        builder.withProperty(DBTableEntitySource.DBTableEntitySourceBuilder.TABLE_NAME, "inspecciones");
        builder.withProperty(DBTableEntitySource.DBTableEntitySourceBuilder.ENTITY_TYPE_ID, "inspecciones");
        builder.build();

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

        builder = sourceFactory.getBuilder(EntitySourceFactory.SOURCE_TYPE.SQLITE);
        builder.withProperty(DBTableEntitySource.DBTableEntitySourceBuilder.SOURCE, this.sourceFactory.getSource("dbTest"));
        builder.withProperty(DBTableEntitySource.DBTableEntitySourceBuilder.ENTITY_TYPE_ID, "provincia");
        builder.withProperty(DBTableEntitySource.DBTableEntitySourceBuilder.TABLE_NAME, "provincia");
        builder.build();

        builder = sourceFactory.getBuilder(EntitySourceFactory.SOURCE_TYPE.SQLITE);
        builder.withProperty(DBTableEntitySource.DBTableEntitySourceBuilder.SOURCE, this.sourceFactory.getSource("dbTest"));
        builder.withProperty(DBTableEntitySource.DBTableEntitySourceBuilder.ENTITY_TYPE_ID, "municipio");
        builder.withProperty(DBTableEntitySource.DBTableEntitySourceBuilder.TABLE_NAME, "municipio");
        builder.build();


        builder = sourceFactory.getBuilder(EntitySourceFactory.SOURCE_TYPE.SQLITE);
        builder.withProperty(DBTableEntitySource.DBTableEntitySourceBuilder.SOURCE, this.sourceFactory.getSource("dbTest"));
        builder.withProperty(DBTableEntitySource.DBTableEntitySourceBuilder.ENTITY_TYPE_ID, "agents");
        builder.withProperty(DBTableEntitySource.DBTableEntitySourceBuilder.TABLE_NAME, "assigned_agents");
        builder.build();
    }

    private void createDBSource() {
        File dbFile = new File("/sdcard/test/ribera.sqlite");
        SQLiteDatabase sqDb = SQLiteDatabase.openOrCreateDatabase(dbFile, null);
        SpatialiteDataBase db = new SpatialiteDataBase(dbFile.getAbsolutePath(), new jsqlite.Database());
        this.sourceFactory.registerSource(new Source<>("ribera", dbFile.getAbsolutePath(), db));

        dbFile = new File("/sdcard/test/dbTest.sqlite");
        sqDb = SQLiteDatabase.openOrCreateDatabase(dbFile, null);
        this.sourceFactory.registerSource(new Source<>("dbTest", dbFile.getAbsolutePath(), new StandardDatabase(sqDb)));
    }
}
