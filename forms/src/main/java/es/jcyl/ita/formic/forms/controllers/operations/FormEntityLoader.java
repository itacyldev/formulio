package es.jcyl.ita.formic.forms.controllers.operations;
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


import org.apache.commons.beanutils.ConvertUtils;

import java.util.List;

import es.jcyl.ita.formic.core.context.CompositeContext;
import es.jcyl.ita.formic.core.context.Context;
import es.jcyl.ita.formic.forms.components.form.UIForm;
import es.jcyl.ita.formic.forms.controllers.FormException;
import es.jcyl.ita.formic.forms.repo.query.FilterHelper;
import es.jcyl.ita.formic.forms.view.ViewConfigException;
import es.jcyl.ita.formic.repo.EditableRepository;
import es.jcyl.ita.formic.repo.Entity;
import es.jcyl.ita.formic.repo.Repository;
import es.jcyl.ita.formic.repo.meta.EntityMeta;
import es.jcyl.ita.formic.repo.query.Filter;

/**
 * Stateless helper to retrieve form's main entity from repository.
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
                throw new FormException("No entity found using id=[" + entityId + "]");
            }
        }
        return entity;
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
