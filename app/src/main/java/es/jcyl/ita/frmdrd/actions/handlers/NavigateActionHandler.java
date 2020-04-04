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
import es.jcyl.ita.frmdrd.actions.UserActionException;
import es.jcyl.ita.frmdrd.router.Router;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class NavigateActionHandler implements ActionHandler {

    /**
     * Handles user navigations request accessing the router.
     * The method has the synchronized modifier to avoid simultaneous requests.
     * @param action
     */
    @Override
    public synchronized void handle(UserAction action) {
        MainController mc = MainController.getInstance();
        // create route from action params
        String route = (String) action.getParams().get("route");
        if (StringUtils.isBlank(route)) {
            throw new UserActionException(String.format("A navigation action was called from the " +
                            "component [%s] in form [%s], but the parameter 'route' was empty, make sure " +
                            "the attribute of the component is properly set.", action.getComponent().getId(),
                    action.getComponent().getParentForm()));
        }
        route = route.toLowerCase();
        // check the format of the route
        String parts[] = route.split("#");
        if (parts.length != 2) {
            throw new UserActionException(String.format("Invalid route format, the route has to " +
                            "follow the expression 'formId#edit' or 'formId#list', but this was found:[%s]."
                    , route));
        }
        String navType = parts[1];
        String formId = parts[0];
        if (!navType.equals("list") && !navType.equals("edit")) {
            throw new UserActionException(String.format("Invalid route format, the route has to " +
                            "follow the expression 'formId#edit' or 'formId#list', but this was found:[%s]."
                    , route));
        }
        Router router = mc.getRouter();
        if (navType.equals("list")) {
            router.navigateList(action.getViewContext(), formId, action.getParams());
        } else {
            router.navigateEdit(action.getViewContext(), formId, action.getParams());
        }
    }
}
