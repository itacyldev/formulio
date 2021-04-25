package es.jcyl.ita.formic.forms.actions;
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

import java.util.HashMap;
import java.util.Map;

import es.jcyl.ita.formic.forms.MainController;
import es.jcyl.ita.formic.forms.R;
import es.jcyl.ita.formic.forms.actions.handlers.BackPressedActionHandler;
import es.jcyl.ita.formic.forms.actions.handlers.CreateEntityActionHandler;
import es.jcyl.ita.formic.forms.actions.handlers.DeleteActionHandler;
import es.jcyl.ita.formic.forms.actions.handlers.DeleteFromListActionHandler;
import es.jcyl.ita.formic.forms.actions.handlers.NavigateActionHandler;
import es.jcyl.ita.formic.forms.actions.handlers.SaveActionHandler;
import es.jcyl.ita.formic.forms.config.Config;
import es.jcyl.ita.formic.forms.config.DevConsole;
import es.jcyl.ita.formic.forms.router.Router;
import es.jcyl.ita.formic.forms.view.UserMessagesHelper;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class ActionController {

    private final Map<String, ActionHandler> actionMap = new HashMap<>();
    private final MainController mc;
    private final Router router;

    public ActionController(MainController mc, Router router) {
        this.mc = mc;
        this.router = router;
        // default actions
        register(ActionType.SAVE, new SaveActionHandler(mc, router));
        BackPressedActionHandler bch = new BackPressedActionHandler(mc, router);
        register(ActionType.BACK, bch);
        register(ActionType.CANCEL, bch);
        register(ActionType.NAV, new NavigateActionHandler(mc, router));
        register(ActionType.DELETE, new DeleteActionHandler(mc, router));
        register(ActionType.DELETE_LIST, new DeleteFromListActionHandler(mc, router));
        register(ActionType.CREATE, new CreateEntityActionHandler(mc, router));
        register(ActionType.JS, new JsActionHandler(mc, router));
    }

    public void register(ActionType type, ActionHandler handler) {
        register(type.name(), handler);
    }

    public void register(String type, ActionHandler handler) {
        actionMap.put(type.toLowerCase(), handler);
    }

    public synchronized void doUserAction(UserAction action) {
        if (action.getOrigin() != null && action.getOrigin() != mc.getFormController()) {
            // Make sure the formController referred by the action is the current form controller,
            // in other case dismiss action to prevent executing delayed actions
            return;
        }

        try {
            // create context for action execution
            ActionContext actionContext = new ActionContext(mc.getFormController(),
                    mc.getRenderingEnv().getAndroidContext());
            ActionHandler handler = actionMap.get(action.getType().toLowerCase());

            if (DevConsole.isDebugEnabled()) {
                DevConsole.debug(String.format("Executing action %s with ActionHandler: %s.",
                        action, handler));
            }
            handler.handle(actionContext, action);
        } catch (Exception e) {
            String msg = Config.getInstance().getStringResource(R.string.action_generic_error);
            DevConsole.error(msg, e);
            UserMessagesHelper.toast(mc.getRenderingEnv().getAndroidContext(), msg);
        }
    }

    public MainController getMc() {
        return mc;
    }
}
