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

import es.jcyl.ita.formic.repo.el.JexlUtils;
import es.jcyl.ita.formic.repo.query.Filter;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public abstract class AbstractEditableRepository<T extends Entity, ID, F extends Filter>
        extends AbstractBaseRepository<T, F>
        implements EditableRepository<T, ID, F> {

    public void save(T entity) {
        doSave(entity);
        boolean mainEntityIsUpdated = saveRelated(entity);
        if (mainEntityIsUpdated) {
            doSave(entity);
        }
    }

    /**
     * Saves relates entities, returns true if main entity needs to be updated
     *
     * @param mainEntity
     * @return
     */
    private boolean saveRelated(T mainEntity) {
        boolean mainEntityUpdated = false;
        if (!hasMappings()) {
            return mainEntityUpdated;
        }
        for (EntityMapping mapping : mappings) {
            Entity relEntity = (Entity) mainEntity.get(mapping.getName());

            if (relEntity == null) {
                // set the related property field a null
                continue;
            }
            if (!(mapping.getRepo() instanceof EditableRepository)) {
                throw new RepositoryException(String.format("Error while saving entity [%s#%s], " +
                                "Cannot save the related entity base on mapping for property [%s], " +
                                "the repository defined is not Editable.",
                        mainEntity.getMetadata().getName(), mainEntity.getId(),
                        mapping.getFk()));
            }
            EditableRepository relRepo = (EditableRepository) mapping.getRepo();
            // eval calculated properties if needed
            if (mapping.hasCalcProps()) {
                updateEntityProps(mainEntity, (Entity) relEntity, mapping);
            }
            if (isNewEntity(relEntity)) {
                if (!mapping.isInsertable()) {
                    continue;
                }
                // Obtain a new entity from repo to get repo EntityMeta
                Entity newEntity = relRepo.newEntity();
                newEntity.setProperties(relEntity.getProperties());
                mainEntity.set(mapping.getName(), newEntity, true);
                relEntity = newEntity;
            } else {
                if (!mapping.isUpdatable()) {
                    continue;
                }
            }
            // insert/update related entity
            relRepo.save(relEntity);
            if (!mapping.isFkExpression()) {
                // if the fk is a mainEntity property and not an expression based on
                // mainEntity's fields, update the property with the new entity Id
                Object relPKValue = convertIfNeeded(mainEntity.getMetadata(), mapping.getFk(), relEntity.getId());
                mainEntity.set(mapping.getFk(), relPKValue);
                mainEntityUpdated = true;
            }
            // TODO: mainEntity.fk must be equal to relEntity.pk. Check
        }
        return mainEntityUpdated;
    }


    private void updateEntityProps(Entity mainEntity, Entity relatedEntity, EntityMapping relation) {
        for (CalculatedProperty cp : relation.getCalcProps()) {
            relatedEntity.set(cp.property, JexlUtils.eval(mainEntity, cp.expression));
        }
    }

    private boolean isNewEntity(Entity relEntity) {
        return relEntity.getId() == null || relEntity.getMetadata() == null;
    }

    protected abstract void doSave(T entity);

    public T findById(ID key) {
        T entity = doFindById(key);
        loadRelated(entity);
        return entity;
    }

    protected abstract T doFindById(ID key);

    public boolean existsById(ID key) {
        return doFindById(key) != null;
    }


    @Override
    public void delete(T entity) {
        doDelete(entity);
        if (hasMappings()) {
            deleteRelated(entity);
        }
    }

    protected void deleteRelated(T mainEntity) {
        boolean mainEntityUpdated = false;
        for (EntityMapping mapping : mappings) {
            Entity relEntity = (Entity) mainEntity.get(mapping.getName());

            if (relEntity == null || !mapping.isDeletable()) {
                // set the related property field a null
                continue;
            }
            if (!(mapping.getRepo() instanceof EditableRepository)) {
                throw new RepositoryException(String.format("Error while saving entity [%s#%s], " +
                                "Cannot save the related entity base on mapping for property [%s], " +
                                "the repository defined is not Editable.",
                        mainEntity.getMetadata().getName(), mainEntity.getId(),
                        mapping.getFk()));
            }
            EditableRepository relRepo = (EditableRepository) mapping.getRepo();
            relRepo.delete(relEntity);
        }
    }

    protected abstract void doDelete(T entity);

    @Override
    public void deleteById(ID key) {
        doDeleteById(key);
        if (hasMappings()) {
            T entity = findById(key);
            deleteRelated(entity);
        }
    }

    protected abstract void doDeleteById(ID key);

    @Override
    public void deleteAll() {
        doDeleteAll();
    }

    protected abstract void doDeleteAll();

}
