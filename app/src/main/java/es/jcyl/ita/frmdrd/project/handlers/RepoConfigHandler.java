package es.jcyl.ita.frmdrd.project.handlers;
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
import android.net.Uri;

import org.greenrobot.greendao.database.StandardDatabase;

import java.io.File;

import es.jcyl.ita.crtrepo.EditableRepository;
import es.jcyl.ita.crtrepo.Repository;
import es.jcyl.ita.crtrepo.RepositoryFactory;
import es.jcyl.ita.crtrepo.builders.EntitySourceBuilder;
import es.jcyl.ita.crtrepo.builders.RepositoryBuilder;
import es.jcyl.ita.crtrepo.config.AbstractRepoConfigurationReader;
import es.jcyl.ita.crtrepo.db.builders.SQLiteGreenDAORepoBuilder;
import es.jcyl.ita.crtrepo.db.source.DBTableEntitySource;
import es.jcyl.ita.crtrepo.db.sqlite.greendao.EntityDaoConfig;
import es.jcyl.ita.crtrepo.db.sqlite.meta.SQLiteMetaModeler;
import es.jcyl.ita.crtrepo.meta.EntityMeta;
import es.jcyl.ita.crtrepo.meta.MetaModeler;
import es.jcyl.ita.crtrepo.source.EntitySource;
import es.jcyl.ita.crtrepo.source.EntitySourceFactory;
import es.jcyl.ita.crtrepo.source.Source;
import es.jcyl.ita.frmdrd.config.ConfigurationException;
import es.jcyl.ita.frmdrd.config.reader.ReadingProcessListener;
import es.jcyl.ita.frmdrd.config.reader.xml.XmlConfigFileReader;
import es.jcyl.ita.frmdrd.project.ProjectResource;

import static es.jcyl.ita.frmdrd.config.DevConsole.error;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class RepoConfigHandler extends AbstractRepoConfigurationReader implements ProjectResourceHandler {

    private ReadingProcessListener listener;

    @Override
    public Object handle(ProjectResource resource) {
        XmlConfigFileReader reader = new XmlConfigFileReader();
        reader.setListener(this.listener);
        reader.read(Uri.fromFile(resource.file));
        // repos are registered during reading process, just check entities metadata
        checkRepos();

        return null;
    }

    private void checkRepos() {
        for (String repoId : repoFactory.getRepoIds()) {
            validate(repoFactory.getRepo(repoId));
        }
    }

    private void validate(Repository repo) {
        // TODO: if this grows, create separated validators or create an implementor
        //  for each repo implementation
        if (repo instanceof EditableRepository) {
            // make sure the repo has a pk
            if (repo.getMeta() == null || !repo.getMeta().hasIdProperties()) {
                throw new ConfigurationException(error(String.format("The repository [%s] has no primary key defined. " +
                        "Can't create an editable repository without a proper PK column (And it should be a one-column PK). " +
                        "Check your SQLite database.", repo.getId())));
            }
        }
    }

    @Override
    public void setListener(ReadingProcessListener listener) {
        this.listener = listener;
    }

    @Override
    @Deprecated
    public void read() {
        throw new UnsupportedOperationException();
    }


    /**
     * @param filePath
     * @param tableName
     * @return
     */
    public Repository createFromFile(String entityId, String filePath, String tableName) {
        File dbFile = new File(filePath);
        if (!dbFile.exists()) {
            throw new ConfigurationException(error(String.format("File doesn't exists [%s] " +
                    "referenced in ${file}", filePath)));
        }
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
        EntitySource eSource = sourceFactory.getEntitySource(entityId);
        if (eSource == null) {
            EntitySourceBuilder builder;
            builder = sourceFactory.getBuilder(EntitySourceFactory.SOURCE_TYPE.SQLITE);
            builder.withProperty(DBTableEntitySource.DBTableEntitySourceBuilder.SOURCE, source);
            builder.withProperty(DBTableEntitySource.DBTableEntitySourceBuilder.TABLE_NAME, tableName);
            builder.withProperty(DBTableEntitySource.DBTableEntitySourceBuilder.ENTITY_TYPE_ID, entityId);
            eSource = builder.build();
        }

        // create repository
        MetaModeler metaModeler = new SQLiteMetaModeler();
        EntityMeta meta = metaModeler.readFromSource(eSource);
        EntityDaoConfig conf = new EntityDaoConfig(meta, (DBTableEntitySource) eSource);
        RepositoryBuilder builder = repoFactory.getBuilder(eSource);
        builder.withProperty(SQLiteGreenDAORepoBuilder.ENTITY_CONFIG, conf);
        return builder.build();
    }

    public RepositoryFactory getRepoFactory() {
        return this.repoFactory;
    }

}
