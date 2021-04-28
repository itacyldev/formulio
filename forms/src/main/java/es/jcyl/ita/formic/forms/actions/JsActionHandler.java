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

import java.util.Map;

import es.jcyl.ita.formic.forms.MainController;
import es.jcyl.ita.formic.forms.actions.handlers.EntityChangeAction;
import es.jcyl.ita.formic.forms.router.Router;
import es.jcyl.ita.formic.forms.scripts.ScriptEngine;

import static es.jcyl.ita.formic.forms.config.DevConsole.error;

/**
 * Predefined action to execute a js method as result of user interaction with a UIComponent.
 *
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class JsActionHandler extends EntityChangeAction {

    private static final Object[] EMPTY_PARAMS = new Object[]{};

    public JsActionHandler(MainController mc, Router router) {
        super(mc, router);
    }

    @Override
    protected void doAction(ActionContext actionContext, UserAction action) {
        Map<String, Object> params = action.getParams();
        String methodName = null;
        Object[] callParams = EMPTY_PARAMS;
        if (params.size() > 1) {
            // has additional parameters
            callParams = new Object[params.size() - 1];
        }
        int i = 0;
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            if (entry.getKey().equalsIgnoreCase("method")) {
                methodName = (String) entry.getValue();
            } else {
                callParams[i] = entry.getValue();
                i++;
            }
        }
        if (methodName == null) {
            throw new UserActionException(error("No methodName parameter found, to call a " +
                    "js function from a component, set a parameter <param name='method' " +
                    "value='yourJsFunctionName'/>"));
        }
        ScriptEngine scriptEngine = this.mc.getScriptEngine();
        scriptEngine.callFunction(methodName, callParams);
    }

    @Override
    protected String getSuccessMessage(UserAction action) {
        return action.getMessage();
    }

}
