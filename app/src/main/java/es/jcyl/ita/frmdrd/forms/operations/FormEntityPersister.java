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

import es.jcyl.ita.crtrepo.EditableRepository;
import es.jcyl.ita.crtrepo.Entity;
import es.jcyl.ita.crtrepo.Repository;
import es.jcyl.ita.crtrepo.context.Context;
import es.jcyl.ita.frmdrd.config.DevConsole;
import es.jcyl.ita.frmdrd.el.JexlUtils;
import es.jcyl.ita.frmdrd.el.ValueBindingExpression;
import es.jcyl.ita.frmdrd.repo.CalculatedProperty;
import es.jcyl.ita.frmdrd.repo.EntityRelation;
import es.jcyl.ita.frmdrd.ui.components.form.UIForm;

/**
 * Staleless helper class to handle save/delete operations on form entities.
 *
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */

public class FormEntityPersister {

    public void save(Context context, UIForm form) {
        Entity entity = form.getEntity();
        EditableRepository repo = getEditableRepo(form);
        repo.save(entity);

        // save related entities
        if (form.getEntityRelations() != null) {
            for (EntityRelation relation : form.getEntityRelations()) {
                EditableRepository relatedRepo = getEditableRepo(relation.getRepo());
                // get related entity
                Object relatedEntity = entity.get(relation.getName());
                if (relatedEntity != null) {
                    // update related entity properties if needed
                    if (relation.hasCalcProps()) {
                        updateEntityProps(context, (Entity) relatedEntity, relation);
                    }
                    relatedRepo.save((Entity) relatedEntity);
                    // it the pointer from main entity to related entity is a double binding
                    // (ex: ${entity.image}, update the reference to the related entity
                    ValueBindingExpression entityPropertyExpr = relation.getEntityPropertyExpr();
                    if (!entityPropertyExpr.isReadOnly()) {
                        // remove "entity." prefix
                        String entityProperty = entityPropertyExpr.getBindingProperty().replace(
                                "entity.", "");
                        entity.set(entityProperty, ((Entity) relatedEntity).getId());
                    }
                }
            }
            // in case any relation id has been modified
            repo.save(entity);
        }
    }


    private void updateEntityProps(Context context, Entity relatedEntity, EntityRelation relation) {
        for (CalculatedProperty cp : relation.getCalcProps()) {
            relatedEntity.set(cp.property, JexlUtils.eval(context, cp.expression));
        }
    }

    public void delete(Context context, UIForm form) {
        Entity entity = form.getEntity();
        EditableRepository repo = getEditableRepo(form);
        repo.delete(entity);

        // delete related entities
        if (form.getEntityRelations() != null) {
            for (EntityRelation relation : form.getEntityRelations()) {
                repo = getEditableRepo(relation.getRepo());
                // get related entity
                Entity relatedEntity = (Entity) entity.get(relation.getName());
                repo.delete(relatedEntity);
            }
        }
    }

    public EditableRepository getEditableRepo(UIForm form) {
        Repository repo = form.getRepo();
        if (!(repo instanceof EditableRepository)) {
            throw new IllegalStateException(DevConsole.error(String.format("Cannot perform save " +
                            "operation on [%s] repository related to form [%s], the repository " +
                            "must be editable.",
                    repo.getId(), form.getId())));
        }
        return (EditableRepository) repo;
    }

    public EditableRepository getEditableRepo(Repository repo) {
        if (!(repo instanceof EditableRepository)) {
            throw new IllegalStateException(DevConsole.error(String.format("Cannot perform save " +
                            "operation on [%s] repository, the repository " +
                            "must be editable.",
                    repo.getId())));
        }
        return (EditableRepository) repo;
    }

}
