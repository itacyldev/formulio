package es.jcyl.ita.frmdrd.config.reader;
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

import es.jcyl.ita.crtrepo.EntitySource;
import es.jcyl.ita.crtrepo.EntitySourceFactory;
import es.jcyl.ita.crtrepo.Repository;
import es.jcyl.ita.crtrepo.Source;
import es.jcyl.ita.crtrepo.builders.EntitySourceBuilder;
import es.jcyl.ita.crtrepo.builders.RepositoryBuilder;
import es.jcyl.ita.crtrepo.builders.SQLiteGreenDAORepoBuilder;
import es.jcyl.ita.crtrepo.config.AbstractRepoConfigurationReader;
import es.jcyl.ita.crtrepo.db.DBTableEntitySource;
import es.jcyl.ita.crtrepo.db.sqlite.greendao.EntityDaoConfig;
import es.jcyl.ita.crtrepo.db.sqlite.meta.SQLiteMetaModeler;
import es.jcyl.ita.crtrepo.meta.EntityMeta;
import es.jcyl.ita.crtrepo.meta.MetaModeler;
import es.jcyl.ita.frmdrd.config.ConfigurationException;
import es.jcyl.ita.frmdrd.config.reader.listener.ReadingProcessListener;
import es.jcyl.ita.frmdrd.config.reader.xml.XmlConfigFileReader;
import es.jcyl.ita.frmdrd.project.ProjectResource;

import static es.jcyl.ita.frmdrd.config.DevConsole.error;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class RepositoriesReader extends AbstractRepoConfigurationReader implements ProjectResourceReader {

    private ReadingProcessListener listener;

    @Override
    public void clear() {
        repoFactory.clear();
        sourceFactory.clear();
//        converterFactory.clear(); //keep it
    }


    @Override
    public Object read(ProjectResource resource) {
        XmlConfigFileReader reader = new XmlConfigFileReader();
        reader.setListener(this.listener);
        reader.read(Uri.fromFile(resource.file));
        // nothing to do, repos are registered during reading process
        return null;
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
     *
     * @param filePath
     * @param tableName
     * @return
     */
    public Repository createFromFile(String entityId, String filePath, String tableName) {
        File dbFile = new File(filePath);
        if(!dbFile.exists()){
            throw new ConfigurationException(error(String.format("File doesn't exist [%s] referenced in ${file}", filePath)));
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

}
