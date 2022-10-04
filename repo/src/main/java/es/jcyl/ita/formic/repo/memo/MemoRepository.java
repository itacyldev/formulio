package es.jcyl.ita.formic.repo.memo;
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

import org.mini2Dx.beanutils.ConvertUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.jcyl.ita.formic.repo.AbstractEditableRepository;
import es.jcyl.ita.formic.repo.Entity;
import es.jcyl.ita.formic.repo.el.JexlEntityUtils;
import es.jcyl.ita.formic.repo.memo.source.MemoSource;
import es.jcyl.ita.formic.repo.meta.EntityMeta;
import es.jcyl.ita.formic.repo.meta.PropertyType;
import es.jcyl.ita.formic.repo.query.BaseFilter;
import es.jcyl.ita.formic.repo.query.Expression;
import es.jcyl.ita.formic.repo.query.JexlEntityExpression;
import es.jcyl.ita.formic.repo.source.EntitySource;

/**
 * Implements memory base entity repository.
 *
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class MemoRepository extends AbstractEditableRepository<Entity, Object, BaseFilter> {

    private EntitySource source;
    Map<Object, Entity> repo = new HashMap<>();
    private Long keyCounter = 0l;
    private EntityMeta meta;


    public MemoRepository(MemoSource source) {
        // basic metadata with id
        PropertyType[] props = new PropertyType[1];
        props[0] = new PropertyType("id", Object.class, null, true);
        this.meta = new EntityMeta("defaultMemoMeta", props, new String[]{"id"});
        this.source = source;
    }

    @Override
    protected EntityMeta doGetMeta() {
        return meta;
    }

    @Override
    public Entity newEntity() {
        return new MemoEntity(this.getSource(), this.meta);
    }

    @Override
    protected void doSave(Entity entity) {
        if (entity.getId() == null) {
            entity.setId(generateKey());
        }
        this.repo.put(entity.getId(), entity);
    }

    @Override
    protected Entity doFindById(Object key) {
        return this.repo.get(key);
    }

    @Override
    protected void doDelete(Entity entity) {
        this.repo.remove(entity.getId());
    }

    @Override
    protected void doDeleteById(Object key) {
        this.repo.remove(key);
    }

    @Override
    protected void doDeleteAll() {
        this.repo.clear();
    }


    @Override
    protected List<Entity> doFind(BaseFilter filter) {
        List<Entity> values = new ArrayList(this.repo.values());
        // TODO: FORMIC-218 Add pagination and ordering to MemoRepo
        List<Entity> retList = new ArrayList<>();

        // TODO: additional evaluators?
        Expression expr = filter.getExpression();
        if(filter == null || expr == null){
            return values;
        }
        if (expr != null && !(expr instanceof JexlEntityExpression)) {
            throw new UnsupportedOperationException("Just JexlEntityExpression is supported as " +
                    "expression implementor!.");
        }
        JexlEntityExpression expression = (JexlEntityExpression) expr;
        Object[] indexes = JexlEntityUtils.bulkEval(values, expression);
        int i = 0;
        for (Entity entity : values) {
            Boolean include = (Boolean) ConvertUtils.convert(indexes[i], Boolean.class);
            if (include) {
                retList.add(entity);
            }
            i++;
        }
        return retList;
    }

    @Override
    protected List<Entity> doListAll() {
        return new ArrayList(this.repo.values());
    }

    @Override
    public long count(BaseFilter filter) {
        return this.repo.size();
    }

    @Override
    public EntitySource getSource() {
        return source;
    }

    @Override
    public Object getImplementor() {
        return null;
    }

    @Override
    public Class<BaseFilter> getFilterClass() {
        return BaseFilter.class;
    }

    protected Object generateKey() {
        keyCounter++;
        return keyCounter;
    }

    /***
     * Methods to allow dynamic metadata handling
     */
    public void setMeta(EntityMeta meta) {
        this.meta = meta;
    }

    public void setPropertyNames(String... propNames) {
        List<PropertyType> props = new ArrayList<>();
        int i = 0;
        boolean includesId = false;
        for (String name : propNames) {
            props.add(new PropertyType(name, Object.class, null, false));
            if (name.toLowerCase().equals("id")) {
                includesId = true;
            }
            i++;
        }
        // if "id" is not included in the properties, add it
        if (!includesId) {
            props.add(0, new PropertyType("id", Object.class, null, false));
        }
        EntityMeta meta = new EntityMeta(this.getId(), props.toArray(new PropertyType[props.size()]),
                new String[]{"id"});
        this.meta = meta;
    }
}
