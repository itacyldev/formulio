package es.jcyl.ita.formic.repo.db.sqlite;
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

import org.greenrobot.greendao.query.Query;

import java.util.List;

import es.jcyl.ita.formic.core.context.Context;
import es.jcyl.ita.formic.repo.AbstractEditableRepository;
import es.jcyl.ita.formic.repo.EditableRepository;
import es.jcyl.ita.formic.repo.Entity;
import es.jcyl.ita.formic.repo.db.SQLQueryFilter;
import es.jcyl.ita.formic.repo.db.source.DBTableEntitySource;
import es.jcyl.ita.formic.repo.db.sqlite.greendao.EntityDao;
import es.jcyl.ita.formic.repo.meta.EntityMeta;
import es.jcyl.ita.formic.repo.source.EntitySource;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */

/**
 * Repository extension that uses green dao as implementor to access SQLite databases
 * using default Android libraries.
 */
public class SQLiteRepository extends AbstractEditableRepository<Entity, Object, SQLQueryFilter>
        implements EditableRepository<Entity, Object, SQLQueryFilter> {

    private final EntitySource source;
    private final EntityDao dao;

    public SQLiteRepository(DBTableEntitySource source, EntityDao dao) {
        this.source = source;
        this.dao = dao;
    }

    @Override
    protected void doSave(Entity entity) {
        this.dao.insertOrReplaceInTx(entity);
    }

    @Override
    protected Entity doFindById(Object key) {
        return this.dao.load(key);
    }

    /**
     * Executes the filter on the repository creating a greendao Query object.
     *
     * @param filter
     * @return
     */
    @Override
    public List<Entity> doFind(SQLQueryFilter filter) {
        List<Entity> entities = null;
        if (filter == null) {
            entities = dao.loadAll();
        } else {
            Query<Entity> query = filter.getQuery(this.dao);
            entities = query.list();
        }
        return entities;
    }

    @Override
    public long count(SQLQueryFilter filter) {
        if (filter == null) {
            return dao.count();
        } else {
            return filter.getCountQuery(this.dao);
        }
    }

    @Override
    protected List<Entity> doListAll() {
        return this.dao.loadAll();
    }

    @Override
    public EntitySource getSource() {
        return this.source;
    }

    @Override
    public void doDelete(Entity entity) {
        this.dao.delete(entity);
    }

    @Override
    public void doDeleteById(Object key) {
        this.dao.deleteByKey(key);
    }

    @Override
    public void doDeleteAll() {
        this.dao.deleteAll();
    }

    @Override
    public Entity newEntity() {
        return new Entity(getSource(), this.getMeta());
    }

    @Override
    public EntityMeta doGetMeta() {
        return this.dao.entityConfig().getMeta();
    }

    @Override
    public Object getImplementor() {
        return this.dao;
    }

    @Override
    public Class<SQLQueryFilter> getFilterClass() {
        return SQLQueryFilter.class;
    }

    @Override
    public void setContext(Context ctx) {
        super.setContext(ctx);
        this.dao.setContext(ctx);
    }

    public void clearCache() {
        this.dao.detachAll();
    }
}
