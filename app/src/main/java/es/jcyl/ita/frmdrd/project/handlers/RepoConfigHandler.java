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


    public RepositoryFactory getRepoFactory() {
        return this.repoFactory;
    }

}
