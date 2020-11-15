package es.jcyl.ita.formic.forms.config.builders.repo;
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
import org.greenrobot.greendao.database.StandardDatabase;

import java.io.File;

import es.jcyl.ita.formic.forms.config.ConfigurationException;
import es.jcyl.ita.formic.forms.config.builders.AbstractComponentBuilder;
import es.jcyl.ita.formic.forms.config.builders.BuilderHelper;
import es.jcyl.ita.formic.forms.config.elements.RepoConfig;
import es.jcyl.ita.formic.forms.config.reader.ConfigNode;
import es.jcyl.ita.formic.repo.Repository;
import es.jcyl.ita.formic.repo.builders.EntitySourceBuilder;
import es.jcyl.ita.formic.repo.builders.RepositoryBuilder;
import es.jcyl.ita.formic.repo.db.builders.SQLiteGreenDAORepoBuilder;
import es.jcyl.ita.formic.repo.db.source.DBTableEntitySource;
import es.jcyl.ita.formic.repo.db.sqlite.greendao.EntityDaoConfig;
import es.jcyl.ita.formic.repo.db.sqlite.meta.SQLiteMetaModeler;
import es.jcyl.ita.formic.repo.meta.EntityMeta;
import es.jcyl.ita.formic.repo.meta.MetaModeler;
import es.jcyl.ita.formic.repo.source.EntitySource;
import es.jcyl.ita.formic.repo.source.EntitySourceFactory;
import es.jcyl.ita.formic.repo.source.Source;

import static es.jcyl.ita.formic.forms.config.DevConsole.error;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class RepoConfigBuilder extends AbstractComponentBuilder<RepoConfig> {

    public RepoConfigBuilder(String tagName) {
        super(tagName, RepoConfig.class);
    }

    @Override
    protected void doWithAttribute(RepoConfig element, String name, String value) {

    }

    @Override
    protected void setupOnSubtreeStarts(ConfigNode<RepoConfig> node) {
        // check if there a direct repository definition with dbFile and dbTable attributes
        RepoConfig element = node.getElement();
        String dbFile = element.getDbFile();
        String dbTable = element.getDbTable();
        boolean isDbFileSet = StringUtils.isNotBlank(dbFile);
        boolean isTableNameSet = StringUtils.isNotBlank(dbTable);

        Repository repo = null;
        if (isDbFileSet ^ isTableNameSet) {
            throw new ConfigurationException(error(String.format("Incorrect repository definition, both 'dbFile' and 'dbTable' " +
                    "must be set in tag ${tag} id [%s].", element.getId())));
        } else if (isDbFileSet && isTableNameSet) {
            // try to create a repository from current configuration
            try {
                repo = createFromFile(element.getId(), dbFile, dbTable);
            } catch (Exception e) {
                throw new ConfigurationException(error(String.format("An error occurred while trying to create SQLite " +
                                "repo with table [%s] in dbFile [%s] referenced in configuration file [${file}] with id [%s].",
                        dbTable, dbFile, element.getId())), e);
            }
        }

        // find first parent that admits "repo" attribute and if doesn't have a repo already defined by
        // attribute "repo", set current repo to it
        ConfigNode parent = BuilderHelper.findParentRepo(node);
        if (parent == null) {
            return;
        } else if (!parent.hasAttribute("repo")) {
            parent.setAttribute("repo", repo.getId());
            BuilderHelper.setElementValue(parent.getElement(), "repo", repo);
        }
    }

    @Override
    protected void setupOnSubtreeEnds(ConfigNode<RepoConfig> node) {
        // check if threre's a meta configuration to override the default
        ConfigNode<Object> meta = BuilderHelper.findNodeByTag(node, "meta");
        if (meta == null) {
            return;
        }
    }

    /**
     * @param filePath
     * @param tableName
     * @return
     */
    public Repository createFromFile(String entityId, String filePath, String tableName) {
        EntitySourceFactory sourceFactory = getFactory().getSourceFactory();
        File dbFile = new File(filePath);
        if (!dbFile.exists()) {
            throw new ConfigurationException(error(String.format("File doesn't exists [%s] " +
                    "referenced in ${file}", filePath)));
        }
        // check if exists another repository against that source
        String absPath = dbFile.getAbsolutePath();
        Source source = sourceFactory.getSource(absPath);
        if (source == null) {
            SQLiteDatabase sqDb = SQLiteDatabase.openOrCreateDatabase(dbFile, null);
            // use absolute path as source Id
            source = new Source<>(absPath, absPath, new StandardDatabase(sqDb));
            sourceFactory.registerSource(source);
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
        RepositoryBuilder builder = getFactory().getRepoFactory().getBuilder(eSource);
        builder.withProperty(SQLiteGreenDAORepoBuilder.ENTITY_CONFIG, conf);
        return builder.build();
    }


}
