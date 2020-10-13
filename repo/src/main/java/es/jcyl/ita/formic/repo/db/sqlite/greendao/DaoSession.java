package es.jcyl.ita.formic.repo.db.sqlite.greendao;
/*
 * Copyright 2011-2020 the original author or authors.
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

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 * Based on the original work of Markus Junginger, greenrobot (http://greenrobot.org)
 * https://github.com/greenrobot/greenDAO/blob/V3.2.2/DaoCore/src/main/java/org/greenrobot/greendao/AbstractDao.java
 */

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.AbstractDaoSession;
import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.identityscope.IdentityScopeType;
import org.greenrobot.greendao.query.QueryBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.jcyl.ita.formic.repo.Entity;

public class DaoSession extends AbstractDaoSession {

    private Map<String, EntityDao> entityToDao;

    public DaoSession(Database db, IdentityScopeType type, Map<String, EntityDaoConfig> daoConfigMap) {
        super(db);
        this.entityToDao = new HashMap<String, EntityDao>();

        EntityDaoConfig config = null;
        EntityDao dao = null;
        for (Map.Entry<String, EntityDaoConfig> entry : daoConfigMap.entrySet()) {
            String entityId = entry.getKey();
            config = entry.getValue();
            config.initIdentityScope(type);
            // create dao for current entity Type and register it
            dao = new EntityDao(config);
            registerEntityDao(entityId, dao);
        }
    }

    /**
     * Maps entity types to corresponding daos
     * @param entityType
     * @param dao
     */
    private void registerEntityDao(String entityType, EntityDao dao) {
        this.entityToDao.put(entityType, dao);
    }

    /** Override GreenDao AbstractSessionDao to provide methods that retrieve Entity using its id instead of Entity class*/


    /**
     * Convenient call for {@link AbstractDao#insert(Object)}.
     */
    public long insert(Entity entity) {
        EntityDao dao = getEntityDao(entity.getType());
        return dao.insertWithoutSettingPk(entity);
    }

    public long insertOrReplace(Entity entity) {
        EntityDao dao = getEntityDao(entity.getType());
        return dao.insertOrReplace(entity);
    }

    public void refresh(Entity entity) {
        EntityDao dao = getEntityDao(entity.getType());
        dao.refresh(entity);
    }

    /**
     * Convenient call for {@link AbstractDao#update(Object)}.
     */
    public void update(Entity entity) {
        EntityDao dao = getEntityDao(entity.getType());
        dao.update(entity);
    }

    /**
     * Convenient call for {@link AbstractDao#delete(Object)}.
     */
    public void delete(Entity entity) {
        EntityDao dao = getEntityDao(entity.getType());
        dao.delete(entity);
    }

    /**
     * Convenient call for {@link AbstractDao#deleteAll()}.
     */
    public void deleteAll(String entityType) {
        EntityDao dao = getEntityDao(entityType);
        dao.deleteAll();
    }

    /**
     * Convenient call for {@link AbstractDao#load(Object)}.
     */
    public Entity load(String entityType, Object key) {
        EntityDao dao = getEntityDao(entityType);
        return dao.load(key);
    }

    /**
     * Convenient call for {@link AbstractDao#loadAll()}.
     */
    public List<Entity> loadAll(String entityType) {
        EntityDao dao = getEntityDao(entityType);
        return dao.loadAll();
    }

    /**
     * Convenient call for {@link AbstractDao#queryRaw(String, String...)}.
     */
    public List<Entity> queryRaw(String entityType, String where, String... selectionArgs) {
        EntityDao dao = getEntityDao(entityType);
        return dao.queryRaw(where, selectionArgs);
    }

    /**
     * Convenient call for {@link AbstractDao#queryBuilder()}.
     */
    public QueryBuilder<Entity> queryBuilder(String entityType) {
        EntityDao dao = getEntityDao(entityType);
        return dao.queryBuilder();
    }

    public EntityDao getEntityDao(String entityType) {
        EntityDao dao = entityToDao.get(entityType);
        if (dao == null) {
            throw new DaoException(String.format("No DAO registered for entity with Type = [%s]", entityType));
        }
        return dao;
    }
}
