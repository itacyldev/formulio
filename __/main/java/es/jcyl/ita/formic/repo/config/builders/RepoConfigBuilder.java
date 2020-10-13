package es.jcyl.ita.formic.repo.config.builders;
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

import es.jcyl.ita.crtrepo.Repository;
import es.jcyl.ita.crtrepo.builders.EntitySourceBuilder;
import es.jcyl.ita.crtrepo.builders.RepositoryBuilder;
import es.jcyl.ita.crtrepo.db.builders.SQLiteGreenDAORepoBuilder;
import es.jcyl.ita.crtrepo.db.source.DBTableEntitySource;
import es.jcyl.ita.crtrepo.db.sqlite.greendao.EntityDaoConfig;
import es.jcyl.ita.crtrepo.db.sqlite.meta.SQLiteMetaModeler;
import es.jcyl.ita.crtrepo.meta.EntityMeta;
import es.jcyl.ita.crtrepo.meta.MetaModeler;
import es.jcyl.ita.crtrepo.source.EntitySource;
import es.jcyl.ita.crtrepo.source.EntitySourceFactory;
import es.jcyl.ita.crtrepo.source.Source;
import es.jcyl.ita.formic.repo.config.ConfigurationException;
import es.jcyl.ita.formic.repo.config.meta.TagDef;
import es.jcyl.ita.formic.repo.config.reader.ConfigNode;
import es.jcyl.ita.formic.repo.config.elements.RepoConfig;

import static es.jcyl.ita.formic.repo.config.DevConsole.error;

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
            repo = createFromFile(element.getId(), dbFile, dbTable);
        }

        // find first parent that admits "repo" attribute and if doesn't have a repo already defined by
        // attribute "repo", set current repo to it
        ConfigNode parent = findRepoParent(node);
        if (parent == null) {
            return;
        } else if (!parent.hasAttribute("repo")) {
            parent.setAttribute("repo", repo.getId());
            UIBuilderHelper.setElementValue(parent.getElement(), "repo", repo);
        }
    }

    @Override
    protected void setupOnSubtreeEnds(ConfigNode<RepoConfig> node) {

    }

    /**
     * Finds first parent element that supports "repo" attribute
     *
     * @param node
     * @return
     */
    private ConfigNode findRepoParent(ConfigNode<RepoConfig> node) {
        ConfigNode parent = node.getParent();
        if (parent == null) {
            return null;
        } else {
            while (parent != null) {
                if (TagDef.supportsAttribute(parent.getName(), "repo")) {
                    return parent;
                }
                parent = parent.getParent();
            }
            return null;
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
