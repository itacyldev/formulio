package es.jcyl.ita.formic.repo.db.builders;
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

import org.greenrobot.greendao.database.Database;

import java.util.HashMap;
import java.util.Map;

import es.jcyl.ita.formic.repo.RepositoryFactory;
import es.jcyl.ita.formic.repo.builders.AbstractRepositoryBuilder;
import es.jcyl.ita.formic.repo.db.source.DBTableEntitySource;
import es.jcyl.ita.formic.repo.db.sqlite.SQLiteRepository;
import es.jcyl.ita.formic.repo.db.sqlite.greendao.DaoMaster;
import es.jcyl.ita.formic.repo.db.sqlite.greendao.EntityDao;
import es.jcyl.ita.formic.repo.db.sqlite.greendao.EntityDaoConfig;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */

public class SQLiteGreenDAORepoBuilder extends AbstractRepositoryBuilder<DBTableEntitySource, SQLiteRepository> {
    public static final String ENTITY_CONFIG = "ENTITY_CONFIG";

    Map<String, DaoMaster> mastersMap = new HashMap<String, DaoMaster>();
    Map<String, Database> dbInstances = new HashMap<String, Database>();


    public SQLiteGreenDAORepoBuilder(RepositoryFactory factory) {
        super(factory);
    }

    @Override
    protected SQLiteRepository doBuild() {
        EntityDaoConfig daoConfig = this.getProperty(ENTITY_CONFIG, EntityDaoConfig.class);
        if (daoConfig != null) {
            return fromEntityDaoConfig(daoConfig);
        }
        throw new UnsupportedOperationException("TODO: additional methods to build instance " +
                "using each parameter separately.");
    }

    private SQLiteRepository fromEntityDaoConfig(EntityDaoConfig daoConfig) {
        String sourceId = daoConfig.getSource().getSourceId();
        String entityType = daoConfig.getSource().getEntityTypeId();
        if (!mastersMap.containsKey(sourceId)) {
            // create data base and daoMaster
            mastersMap.put(sourceId, new DaoMaster(daoConfig.getSource().getDb()));
            dbInstances.put(sourceId, daoConfig.getSource().getDb());
        }
        DaoMaster master = mastersMap.get(sourceId);
        if (!master.isRegistered(entityType)) {
            // store sourceId to reuse
            master.register(entityType, daoConfig);
        }
        EntityDao dao = master.newSession().getEntityDao(entityType);
        return new SQLiteRepository(daoConfig.getSource(), dao);
    }
}

