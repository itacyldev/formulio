package es.jcyl.ita.frmdrd.utils;
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
import org.mockito.Mockito;

import java.io.File;

import es.jcyl.ita.crtrepo.EditableRepository;
import es.jcyl.ita.crtrepo.EntitySource;
import es.jcyl.ita.crtrepo.EntitySourceFactory;
import es.jcyl.ita.crtrepo.Repository;
import es.jcyl.ita.crtrepo.RepositoryFactory;
import es.jcyl.ita.crtrepo.Source;
import es.jcyl.ita.crtrepo.builders.EntitySourceBuilder;
import es.jcyl.ita.crtrepo.builders.RepositoryBuilder;
import es.jcyl.ita.crtrepo.builders.SQLiteGreenDAORepoBuilder;
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

import static es.jcyl.ita.frmdrd.config.DevConsole.error;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */

public class RepositoryUtils {

    private static RepositoryFactory repoFactory = RepositoryFactory.getInstance();

    public static void register(String entityId, Repository repo) {
        repoFactory.register(entityId, repo);
    }

    public static Repository registerMock(String id){
        EditableRepository mock = Mockito.mock(EditableRepository.class);
        register(id, mock);
        return mock;
    }
}
