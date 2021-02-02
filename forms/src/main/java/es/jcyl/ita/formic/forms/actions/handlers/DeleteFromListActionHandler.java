package es.jcyl.ita.formic.forms.actions.handlers;
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

import java.util.List;

import es.jcyl.ita.formic.forms.MainController;
import es.jcyl.ita.formic.forms.actions.ActionHandler;
import es.jcyl.ita.formic.forms.actions.UserAction;
import es.jcyl.ita.formic.forms.controllers.FormListController;
import es.jcyl.ita.formic.forms.router.Router;
import es.jcyl.ita.formic.forms.view.UserMessagesHelper;
import es.jcyl.ita.formic.repo.Entity;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class DeleteFromListActionHandler extends AbstractActionHandler implements ActionHandler<FormListController> {

    public DeleteFromListActionHandler(MainController mc, Router router) {
        super(mc, router);
    }

    @Override
    public void handle(FormListController formController, UserAction action) {
        List<Entity> selectedEntities = formController.getEntitySelector().getSelectedEntities();
        if (selectedEntities != null) {
            for (Entity entity : selectedEntities) {
                formController.getEditableRepo().delete(entity);
            }
        }
        UserMessagesHelper.toast(action.getViewContext(), "Entity successfully deleted.");
    }
}
