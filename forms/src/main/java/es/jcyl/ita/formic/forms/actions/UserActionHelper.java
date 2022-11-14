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
import org.apache.commons.lang3.ArrayUtils;
import org.mini2Dx.beanutils.ConvertUtils;

import java.io.Serializable;

import es.jcyl.ita.formic.core.context.Context;
import es.jcyl.ita.formic.forms.components.UIComponent;
import es.jcyl.ita.formic.forms.controllers.UIAction;
import es.jcyl.ita.formic.forms.controllers.UIActionGroup;
import es.jcyl.ita.formic.forms.controllers.UIParam;
import es.jcyl.ita.formic.forms.controllers.ViewController;
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
        UserAction action = UserActionHelper.newAction(actionTemplate.getType(), strRoute, component);
        action.setRegisterInHistory(actionTemplate.isRegisterInHistory());

        action.setRefresh(actionTemplate.getRefresh());
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

    public static void evalActionParams(Context context, UIAction uiAction, UserAction action) {
        if (uiAction instanceof UIActionGroup) {
            evalGroupActionParams(context, (UIActionGroup) uiAction, (UserCompositeAction) action);
        } else {
            evalSimpleActionParams(context, uiAction, action);
        }
    }

    private static void evalGroupActionParams(Context context, UIActionGroup uiAction, UserCompositeAction action) {
        UIActionGroup groupAction = (UIActionGroup) uiAction;
        UIAction[] nestedUiActions = groupAction.getActions();
        UserAction[] nestedActions = action.getActions();
        if (groupAction.getActions() != null) {
            for (int i = 0; i < nestedUiActions.length; i++) {
                evalSimpleActionParams(context, nestedUiActions[i], nestedActions[i]);
            }
        }
    }

    private static void evalSimpleActionParams(Context context, UIAction uiAction, UserAction action) {
        if (uiAction.hasParams()) {
            for (UIParam param : uiAction.getParams()) {
                Object value = JexlFormUtils.eval(context, param.getValue());
                action.addParam(param.getName(), value);
            }
        }
    }


    public static UserAction newAction(String actionType) {
        if (actionType.equalsIgnoreCase("composite")) {
            return new UserCompositeAction();
        } else {
            return new UserAction(actionType);
        }
    }

    public static UserAction newAction(ActionType actionType) {
        if (actionType == ActionType.COMPOSITE) {
            return new UserCompositeAction();
        } else {
            return new UserAction(actionType.name());
        }
    }

    public static UserAction newAction(String actionType, String route, ViewController viewController) {
        UserAction action = newAction(actionType);
        action.setRoute(route);
        action.setOrigin(viewController);
        return action;
    }

    public static UserAction newAction(String actionType, String route, UIComponent component) {
        UserAction action = newAction(actionType);
        action.setRoute(route);
        action.setComponent(component);
        return action;
    }

    /****/
    /****/
    private static UserAction newAction(UIAction uiAction) {
        UserAction action = newAction(uiAction.getType());
        copyActionProps(uiAction, action);
        return action;
    }

    public static UserAction newAction(UIAction uiAction, UIComponent component) {
        UserAction action = newAction(uiAction);
        action.setComponent(component);
        if (uiAction instanceof UIActionGroup) {
            buildNestedActions((UIActionGroup) uiAction, (UserCompositeAction) action);
        }
        return action;
    }

    public static UserAction newAction(UIAction uiAction, ViewController origin) {
        UserAction action = newAction(uiAction);
        action.setOrigin(origin);
        if (uiAction instanceof UIActionGroup) {
            buildNestedActions((UIActionGroup) uiAction, (UserCompositeAction) action);
        }
        return action;
    }

    public static void buildNestedActions(UIActionGroup uiAction, UserCompositeAction compositeAction) {
        UIAction[] uiActions = uiAction.getActions();
        if (ArrayUtils.isNotEmpty(uiActions)) {
            UserAction[] actions = new UserAction[uiActions.length];
            for (int i = 0; i < uiActions.length; i++) {
                actions[i] = newAction(uiActions[i]);
                actions[i].setOrigin(compositeAction.getOrigin());
                actions[i].setComponent(compositeAction.getComponent());
            }
            compositeAction.setActions(actions);
        }
    }

    public static UserAction navigate(String formId) {
        UserAction action = new UserAction(ActionType.NAV.name(), formId, null, null);
        return action;
    }

    public static UserAction navigate(String formId, UIComponent component) {
        UserAction action = new UserAction(ActionType.NAV.name(), formId, component, null);
        return action;
    }

    public static UserAction navigate(String formId, ViewController origin) {
        return new UserAction(ActionType.NAV.name(), formId, null, origin);
    }

    public static UserAction back(ViewController origin) {
        return new UserAction(ActionType.BACK.name(), null, null, origin);
    }


    private static void copyActionProps(UIAction origin, UserAction dest) {
        dest.setRoute(origin.getRoute());
        dest.setRegisterInHistory(origin.isRegisterInHistory());
        dest.setRefresh(origin.getRefresh());
        dest.setRestoreView(origin.isRestoreView());
        dest.setPopHistory(origin.getPopHistory());
        dest.setController(origin.getController());
    }


    /**
     * Creates a new action copying all the attributes but the action parameters
     * from the passed UserAction.
     *
     * @param action
     * @return
     */
    public static UserAction clone(UserAction action) {
        UserAction newAction = new UserAction(action.getType());
        newAction.setName(action.getName());
        newAction.setComponent(action.getComponent());
        newAction.setController(action.getController());
        newAction.setRoute(action.getRoute());
        newAction.setRefresh(action.getRefresh());
        newAction.setRestoreView(action.isRestoreView());
        newAction.setRegisterInHistory(action.isRegisterInHistory());
        newAction.setPopHistory(action.getPopHistory());
        newAction.setMessage(action.getMessage());
        newAction.setWidget(action.getWidget());
        return newAction;
    }


}
