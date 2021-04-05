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

import org.apache.commons.lang3.StringUtils;

import es.jcyl.ita.formic.forms.MainController;
import es.jcyl.ita.formic.forms.actions.ActionContext;
import es.jcyl.ita.formic.forms.actions.ActionHandler;
import es.jcyl.ita.formic.forms.actions.UserAction;
import es.jcyl.ita.formic.forms.actions.UserActionException;
import es.jcyl.ita.formic.forms.controllers.FormController;
import es.jcyl.ita.formic.forms.controllers.FormException;
import es.jcyl.ita.formic.forms.router.Router;
import es.jcyl.ita.formic.forms.view.UserMessagesHelper;

import static es.jcyl.ita.formic.forms.config.DevConsole.error;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class NavigateActionHandler extends AbstractActionHandler implements ActionHandler {

    public NavigateActionHandler(MainController mc, Router router) {
        super(mc, router);
    }

    /**
     * Handles user navigations request accessing the router.
     * The method has the synchronized modifier to avoid simultaneous requests.
     *
     * @param action
     */
    @Override
    public synchronized void handle(ActionContext actionContext, UserAction action) {
        // create route from action params
        String formId = action.getRoute();
        if (StringUtils.isBlank(formId)) {
            throw new UserActionException(error(String.format("A navigation action was called from the " +
                    "form [%s], but the parameter 'route' was empty, make sure " +
                    "the attribute of the component is properly set and references a valid " +
                    "formId.", actionContext.getFc().getId())));
        }
        try {
            router.navigate(actionContext, action);
        } catch (FormException e) {
            UserMessagesHelper.toast(actionContext.getViewContext(), e.getLocalizedMessage());
        }
    }
}
