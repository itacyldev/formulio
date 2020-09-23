package es.jcyl.ita.frmdrd.forms.operations;
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

import java.util.List;

import es.jcyl.ita.crtrepo.EditableRepository;
import es.jcyl.ita.crtrepo.Entity;
import es.jcyl.ita.crtrepo.Repository;
import es.jcyl.ita.crtrepo.context.CompositeContext;
import es.jcyl.ita.crtrepo.context.Context;
import es.jcyl.ita.crtrepo.meta.EntityMeta;
import es.jcyl.ita.crtrepo.query.Filter;
import es.jcyl.ita.frmdrd.context.impl.EntityContext;
import es.jcyl.ita.frmdrd.el.JexlUtils;
import es.jcyl.ita.frmdrd.forms.FormException;
import es.jcyl.ita.frmdrd.repo.EntityRelation;
import es.jcyl.ita.frmdrd.repo.query.FilterHelper;
import es.jcyl.ita.frmdrd.ui.components.form.UIForm;
import es.jcyl.ita.frmdrd.view.ViewConfigException;

/**
 * Staleless helper to retrieve form's main entity from repository.
 *
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */

public class FormEntityLoader {


    public Entity load(CompositeContext globalCtx, UIForm form) {
        Entity entity;
        Repository repo = form.getRepo();

        // load current form entity
        Object entityId = getEntityIdFromContext(globalCtx, form.getEntityId());
        if (entityId == null) {
            // create empty entity
            // TODO: recover this piece of code when the ActionContext is ready and the operation type is
            // accesible
            //            if (isReadOnly()) {
            //                throw new ViewConfigException(String.format("The form [%s] is readOnly, no new " +
            //                        "entities can be created. Use an EditableRepository or set the readOnly " +
            //                        "attribute to false.", this.getId()));
            //            }
            entity = new Entity(repo.getSource(), repo.getMeta());
        } else {
            // what if its null? throw an Exception?
            entity = findEntity(globalCtx, repo, form.getFilter(), entityId);
            if (entity == null) {
                throw new FormException("No entity found with the id: " + entityId);
            }
        }
        form.setEntity(entity);

        // create temporal entity context, so expressions "${entity.property} can be used during
        // related entities loading
        EntityContext entityContext = new EntityContext(entity);
        globalCtx.addContext(entityContext);
        loadRelatedEntities(globalCtx, form, entity);
        globalCtx.removeContext(entityContext);

        return entity;
    }

    /**
     * Loads related entity to form's main entity using the repository attached to the UIComponent.
     * Related entities are attached to the main entity using transient properties identificated
     * by the component "id".
     *
     * @param globalCtx
     * @param form
     * @param entity
     */
    protected void loadRelatedEntities(Context globalCtx, UIForm form, Entity entity) {
        Entity relEntity = null;
        if (form.getEntityRelations() != null) {
            for (EntityRelation relation : form.getEntityRelations()) {
                // Use relation expression to obtain the entity Id
                Object relEntityId = JexlUtils.eval(globalCtx, relation.getEntityPropertyExpr());
                if (relEntityId != null) {
                    relEntity = findEntity(globalCtx, relation.getRepo(),
                            relation.getFilter(), relEntityId);
                } // TODO: else set a proxy to evaluate the expression lazyly during the
                // TODO: rendering process

                // set related entity as transient
                entity.set(relation.getName(), relEntity, true);
                if (relation.getEntityHolder() != null) {
                    relation.getEntityHolder().setEntity(relEntity);
                }
            }
        }
    }


    /**
     * Loads current entity depending on the repository type.
     *
     * @param entityId
     * @return
     */
    private Entity findEntity(Context globalContext, Repository repo,
                              Filter filter, Object entityId) {
        if (repo instanceof EditableRepository) {
            // convert the entity Id if needed
            Object pk = convertIfNeeded(repo.getMeta(), entityId);
            return ((EditableRepository) repo).findById(pk);
        } else {
            // if there's a filter defined in the form, use the filter to find the entity
            if (filter == null) {
                throw new ViewConfigException(String.format("You are using a readOnly repository in " +
                                "form [%s] but no repofilter has been configured to define the query to " +
                                "find the entity from its id. Add a repofilter tag with an eq condition" +
                                " with the expression ${params.entityId}.",
                        globalContext.get("form.id")));
            }
            Filter f = setupFilter(globalContext, repo, filter);
            List<Entity> list = repo.find(f);
            if (list.size() == 0) {
                throw new ViewConfigException(String.format("No entity found with the filter [%s], " +
                                "check the repofilter defined in the form [%s].", f.toString(),
                        globalContext.get("form.id")));
            }
            return list.get(0);
        }
    }

    private Object getEntityIdFromContext(Context context, String entityIdProp) {
        Object entityId;
        try {
            entityId = context.get(entityIdProp);
        } catch (Exception e) {
            throw new FormException(String.format("An error occurred while trying to obtain the " +
                            "entity id from params context for form [%s]." +
                            " It seems the property 'entityId' is not properly set: [%s].",
                    context.get("form.id"),
                    entityIdProp));
        }
        return entityId;
    }


    private Filter setupFilter(Context context, Repository repo, Filter filter) {
        Filter f = FilterHelper.createInstance(repo);
        FilterHelper.evaluateFilter(context, filter, f);
        return f;
    }


    /**
     * Checks the type of the value received as entity Id against he meta
     *
     * @param meta
     * @param entityId
     * @return
     */
    private Object convertIfNeeded(EntityMeta meta, Object entityId) {
        //
        if (!meta.hasIdProperties()) {
            return entityId;
        } else {
            // TODO: multicolumn support
            Class pkType = meta.getIdProperties()[0].getType();
            if (entityId.getClass() == pkType) {
                return entityId;
            } else {
                return ConvertUtils.convert(entityId, pkType);
            }
        }
    }
}
