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

import org.apache.commons.jexl3.JexlContext;
import org.apache.commons.jexl3.JxltEngine;
import org.mini2Dx.beanutils.ConvertUtils;

import java.io.Serializable;

import es.jcyl.ita.formic.core.context.Context;
import es.jcyl.ita.formic.forms.components.UIComponent;
import es.jcyl.ita.formic.forms.controllers.UIAction;
import es.jcyl.ita.formic.forms.controllers.UIParam;
import es.jcyl.ita.formic.forms.el.JexlFormUtils;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class UserActionHelper {

    /**
     * Creates an UserAction from the FCAction template
     *
     * @param actionTemplate
     * @param context:       Context used to evaluate template's binding expressions
     * @return
     */
    public static UserAction evaluate(UIAction actionTemplate, Context context, UIComponent component) {
        String strRoute = "";
        if (actionTemplate.getRoute() != null) {
            JxltEngine.Expression e = JexlFormUtils.createExpression(actionTemplate.getRoute());
            Object route = e.evaluate((JexlContext) context);
            strRoute = (String) ConvertUtils.convert(route, String.class);
        }
        UserAction action = new UserAction(actionTemplate.getType(), strRoute, component);
        action.setRegisterInHistory(actionTemplate.isRegisterInHistory());

        action.setForceRefresh(actionTemplate.isForceRefresh());
        if (actionTemplate.hasParams()) {
            for (UIParam param : actionTemplate.getParams()) {
                Object value = JexlFormUtils.eval(context, param.getValue());
                if (value != null) {
                    action.addParam(param.getName(), (Serializable) value);
                }
            }
        }
        return action;
    }

}
