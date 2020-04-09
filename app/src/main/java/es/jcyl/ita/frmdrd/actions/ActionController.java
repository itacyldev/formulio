package es.jcyl.ita.frmdrd.actions;
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

import es.jcyl.ita.frmdrd.MainController;
import es.jcyl.ita.frmdrd.actions.handlers.BackPressedActionHandler;
import es.jcyl.ita.frmdrd.actions.handlers.DeleteActionHandler;
import es.jcyl.ita.frmdrd.actions.handlers.DeleteFromListActionHandler;
import es.jcyl.ita.frmdrd.actions.handlers.InputChangeActionHandler;
import es.jcyl.ita.frmdrd.actions.handlers.NavigateActionHandler;
import es.jcyl.ita.frmdrd.actions.handlers.SaveActionHandler;
import es.jcyl.ita.frmdrd.router.Router;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class ActionController {

    private static final Map<String, ActionHandler> actionMap = new HashMap<>();
    private final MainController mc;
    private final Router router;

    public ActionController(MainController mc, Router router) {
        this.mc = mc;
        this.router = router;
        // default actions
        register(ActionType.SAVE, new SaveActionHandler(mc, router));
        register(ActionType.INPUT_CHANGE, new InputChangeActionHandler(mc, router));
        BackPressedActionHandler bch = new BackPressedActionHandler(mc, router);
        register(ActionType.BACK, bch);
        register(ActionType.CANCEL, bch);
        register(ActionType.NAVIGATE, new NavigateActionHandler(mc, router));
        register(ActionType.DELETE, new DeleteActionHandler(mc, router));
        register(ActionType.DELETE_LIST, new DeleteFromListActionHandler(mc, router));
    }

    public void register(ActionType type, ActionHandler handler) {
        register(type.name(), handler);
    }

    public void register(String type, ActionHandler handler) {
        actionMap.put(type.toLowerCase(), handler);
    }

    public void doUserAction(UserAction action) {
        // TODO: log user interactions
        ActionHandler handler = actionMap.get(action.getType().toLowerCase());
        handler.handle(mc.getFormController(), action);
        // TODO: catch errors, log, toast for user with meaningful information
    }

}
