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

import es.jcyl.ita.formic.core.context.Context;
import es.jcyl.ita.formic.forms.components.form.UIForm;
import es.jcyl.ita.formic.forms.config.DevConsole;
import es.jcyl.ita.formic.repo.EditableRepository;
import es.jcyl.ita.formic.repo.Entity;
import es.jcyl.ita.formic.repo.Repository;

/**
 * Staleless helper class to handle save/delete operations on form entities.
 *
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */

public class FormEntityPersister {

    public void save(UIForm form) {
        Entity entity = form.getEntity();
        EditableRepository repo = getEditableRepo(form);
        repo.save(entity);
    }

    public void delete(UIForm form) {
        Entity entity = form.getEntity();
        EditableRepository repo = getEditableRepo(form);
        repo.delete(entity);
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

}
