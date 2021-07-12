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
import es.jcyl.ita.formic.forms.R;
import es.jcyl.ita.formic.forms.actions.ActionContext;
import es.jcyl.ita.formic.forms.actions.ActionHandler;
import es.jcyl.ita.formic.forms.actions.UserAction;
import es.jcyl.ita.formic.forms.config.Config;
import es.jcyl.ita.formic.forms.controllers.ViewController;
import es.jcyl.ita.formic.forms.router.Router;
import es.jcyl.ita.formic.forms.validation.ValidatorException;
import es.jcyl.ita.formic.forms.view.UserMessagesHelper;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public abstract class EntityChangeAction<F extends ViewController> extends AbstractActionHandler
        implements ActionHandler {

    public EntityChangeAction(MainController mc, Router router) {
        super(mc, router);
    }

    @Override
    public void handle(ActionContext actionContext, UserAction action) {
        // save view state for each WidgetContextHolder (forms, list-items, ..)
        mc.saveViewState();
        try {
            doAction(actionContext, action);
            String msg = getSuccessMessage(action);
            // resolve after-action navigation
        } catch (Exception e) {
            // re-render form content
            onError(actionContext, action, e);
            if (!(e instanceof ValidatorException)) {
                throw e;
            }
        }
    }
    public void onError(ActionContext actionContext, UserAction action, Exception e) {
        mc.renderBack();
        mc.restoreViewState();
        String msg = getErrorMessage(action, e);
        if (StringUtils.isNotBlank(msg)) {
            UserMessagesHelper.toast(actionContext.getViewContext(), msg);
        }
    }

    /* Subclasses extension points */
    protected abstract void doAction(ActionContext actionContext, UserAction action);

    protected String getSuccessMessage(UserAction action) {
        return Config.getInstance().getStringResource(R.string.action_generic_success);
    }

    protected String getErrorMessage(UserAction action, Exception e) {
        return Config.getInstance().getStringResource(R.string.action_generic_error);
    }

}
