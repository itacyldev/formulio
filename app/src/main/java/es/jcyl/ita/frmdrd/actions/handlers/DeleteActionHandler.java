package es.jcyl.ita.frmdrd.actions.handlers;
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

import org.apache.commons.lang3.StringUtils;

import es.jcyl.ita.frmdrd.MainController;
import es.jcyl.ita.frmdrd.actions.ActionHandler;
import es.jcyl.ita.frmdrd.actions.UserAction;
import es.jcyl.ita.frmdrd.forms.FormEditController;
import es.jcyl.ita.frmdrd.router.Router;
import es.jcyl.ita.frmdrd.view.UserMessagesHelper;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class DeleteActionHandler extends AbstractActionHandler implements ActionHandler<FormEditController> {

    public DeleteActionHandler(MainController mc, Router router) {
        super(mc, router);
    }

    @Override
    public void handle(FormEditController formController, UserAction action) {
        formController.delete();
        String msg = "Entity successfully deleted.";
        if (StringUtils.isBlank(action.getRoute())) {
            UserMessagesHelper.toast(action.getViewContext(), msg);
        } else {
            Router router = mc.getRouter();
            router.navigate(action.getViewContext(), action.getRoute(), action.getParams(),
                    new String[]{msg});
        }
    }
}