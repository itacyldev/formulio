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
import es.jcyl.ita.formic.forms.router.Router;
import es.jcyl.ita.formic.forms.view.UserMessagesHelper;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public abstract class AbstractActionHandler implements ActionHandler {

    protected final Router router;
    protected final MainController mc;

    public AbstractActionHandler(MainController mc, Router router) {
        this.router = router;
        this.mc = mc;
    }

    public UserAction prepareNavigation(ActionContext actionContext, UserAction action){
        return action;
    }

    public void onError(ActionContext actionContext, UserAction action, Exception e) {
        String msg = getErrorMessage(action, e);
        if (StringUtils.isNotBlank(msg)) {
            UserMessagesHelper.toast(actionContext.getViewContext(), msg);
        }
    }

    /**
     * Default messages
     * @param action
     * @RETURN
     */
    public String getSuccessMessage(ActionContext actionContext, UserAction action) {
        return null;
    }

    protected String getErrorMessage(UserAction action, Exception e) {
        return Config.getInstance().getStringResource(R.string.action_generic_error);
    }
}
