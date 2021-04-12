package es.jcyl.ita.formic.repo;
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
import java.util.List;

import es.jcyl.ita.formic.core.context.Context;
import es.jcyl.ita.formic.core.context.ContextAwareComponent;
import es.jcyl.ita.formic.repo.el.JexlUtils;
import es.jcyl.ita.formic.repo.meta.AggregatedMeta;
import es.jcyl.ita.formic.repo.meta.EntityMeta;
import es.jcyl.ita.formic.repo.meta.PropertyType;
import es.jcyl.ita.formic.repo.query.Filter;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public abstract class AbstractBaseRepository<T extends Entity, F extends Filter>
        implements Repository<T, F>, ContextAwareComponent {
    protected String id;
    protected Context context;
    protected List<EntityMapping> mappings;
    protected AggregatedMeta delegateMeta;

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        if (this.id != null) {
            return this.id;
        } else {
            return String.format("%s#%s", this.getSource().getSourceId(),
                    this.getSource().getEntityTypeId());
        }
    }


    @Override
    public long count() {
        return count(null);
    }

    public void setContext(Context ctx) {
        this.context = ctx;
    }

    public List<EntityMapping> getMappings() {
        return mappings;
    }

    public void setMappings(List<EntityMapping> mappings) {
        this.mappings = mappings;
    }

    public void addMapping(EntityMapping mapping) {
        if (this.mappings == null) {
            this.mappings = new ArrayList<EntityMapping>();
        }
        this.mappings.add(mapping);
        if (mapping.isRetrieveMeta()) {
            // enrich meta info with the new repo relation
            if (delegateMeta == null) {
                delegateMeta = new AggregatedMeta(this.doGetMeta());
            }
            delegateMeta.addEntityMappings(mapping.getProperty(), mapping.getRepo().getMeta());
        }
    }

    public final EntityMeta getMeta() {
        if (!this.hasMappings()) {
            return this.doGetMeta();
        } else {
            // use delegate to access related Entity metas
            return this.delegateMeta;
        }
    }

    protected abstract EntityMeta doGetMeta();

    /**
     * Executes the filter on the repository creating a greendao Query object.
     *
     * @param filter
     * @return
     */
    public List<T> find(F filter) {
        List<T> entities = doFind(filter);
        if (hasMappings()) {
            for (T entity : entities) {
                loadRelated(entity);
            }
        }
        return entities;
    }

    protected abstract List<T> doFind(F filter);


    public List<T> listAll() {
        List<T> entities = doListAll();
        if (hasMappings()) {
            for (T entity : entities) {
                loadRelated(entity);
            }
        }
        return entities;
    }

    protected abstract List<T> doListAll();


    protected void loadRelated(T mainEntity) {
        Entity relEntity = null;
        if (!hasMappings()) {
            return;
        }
        for (EntityMapping mapping : mappings) {
            // Use relation expression to obtain the entity Id
            Object relEntityId;
            if (mapping.isFkExpression()) {
                relEntityId = JexlUtils.eval(mainEntity, mapping.getFk());
            } else {
                relEntityId = mainEntity.get(mapping.getFk());
            }

            if (relEntityId != null) {
                // TODO: else set a proxy to evaluate the expression lazyly during the
                // TODO: rendering process
                // set related entity as transient
                relEntity = findRelatedEntity(mainEntity, mapping.getRepo(),
                        mapping.getFilter(), relEntityId);
                mainEntity.set(mapping.getProperty(), relEntity, true);
            }
        }
    }


    /**
     * Loads current entity depending on the repository type.
     *
     * @param entityId
     * @return
     */
    protected Entity findRelatedEntity(Entity mainEntity, Repository repo,
                                       Filter filter, Object entityId) {
        if (repo instanceof EditableRepository) {
            // convert the entity Id if needed
            if (repo.getMeta().hasMulticolumnKey()) {
                throw new RepositoryException(String.format("Sorry, multicolumn PK not supported " +
                        "for related entities: [%s], use single column repository.", repo.getMeta().getName()));
            }
            Object pk = convertIfNeeded(repo.getMeta(), repo.getMeta().getIdPropertiesName()[0], entityId);
            return ((EditableRepository) repo).findById(pk);
        } else {
            // if there's a filter defined in the form, use the filter to find the entity
            throw new UnsupportedOperationException("Not  supported yet, check FormEntityLoader for implementation reference.");
        }
    }

    /**
     * Checks the type of the value received as entity Id against he meta
     *
     * @param meta
     * @param value
     * @return
     */
    protected Object convertIfNeeded(EntityMeta meta, String propertyName, Object value) {
        PropertyType prpType = meta.getPropertyByName(propertyName);
        if (value.getClass() == prpType.type) {
            return value;
        } else {
            return ConvertUtils.convert(value, prpType.type);
        }
    }

    protected boolean hasMappings() {
        return this.mappings != null && this.mappings.size() > 0;
    }
}
