package es.jcyl.ita.formic.forms.actions.handlers;
/*
 *
 *  * Copyright 2020 Javier Ramos (javier.ramos@itacyl.es), ITACyL (http://www.itacyl.es).
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      https://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import es.jcyl.ita.formic.forms.MainController;
import es.jcyl.ita.formic.forms.actions.ActionContext;
import es.jcyl.ita.formic.forms.actions.UserAction;
import es.jcyl.ita.formic.forms.actions.UserActionException;
import es.jcyl.ita.formic.forms.actions.handlers.AbstractActionHandler;
import es.jcyl.ita.formic.forms.router.Router;
import es.jcyl.ita.formic.forms.scripts.ScriptEngine;

import static es.jcyl.ita.formic.forms.config.DevConsole.error;

/**
 * Predefined action to execute a js method as result of user interaction with a UIComponent.
 *
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class JsActionHandler extends AbstractActionHandler {

    public JsActionHandler(MainController mc, Router router) {
        super(mc, router);
    }

    public void handle(ActionContext actionContext, UserAction action) {
        if (action.getParams() == null) {
            throwNoParamsException();
        }
        Map<String, Object> params = new LinkedHashMap<>(action.getParams());
        String methodName = (String) params.remove("method");
        if (methodName == null) {
            throwNoParamsException();
        }
        Object[] callParams = params.values().toArray(new Object[params.size()]);
        ScriptEngine scriptEngine = this.mc.getScriptEngine();
        if (scriptEngine.isFunction(methodName)) {
            scriptEngine.callFunction(methodName, callParams);
        } else {// treat as script
            scriptEngine.executeScript(methodName, params);
        }
    }

    private void throwNoParamsException() {
        throw new UserActionException(error("No 'method' parameter found, to call a " +
                "js function from a component, set a parameter <param name=\"method\" " +
                "value=\"yourJsFunctionName\"/>.\n" +
                "You can also use the \"method\" attribute to define a js script:  " +
                "<param name=\"method\" value=\"console.log('hello world!)\"/>/>"));
    }

    @Override
    public String getSuccessMessage(ActionContext actionContext, UserAction action) {
        return null;
    }
}
