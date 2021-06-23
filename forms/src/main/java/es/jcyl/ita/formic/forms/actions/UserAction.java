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

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

import es.jcyl.ita.formic.forms.components.UIComponent;
import es.jcyl.ita.formic.forms.controllers.ViewController;
import es.jcyl.ita.formic.forms.controllers.UIAction;
import es.jcyl.ita.formic.forms.view.widget.Widget;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class UserAction {

    private String type;
    private String name;
    private String controller;
    private String route;
    private String refresh;
    private boolean restoreView = false;
    private boolean registerInHistory = true;
    private int popHistory = 0;
    private String message;
    private Map<String, Object> params;
    private UIComponent component;
    private Widget widget;

    private ViewController origin;

    public UserAction(ActionType actionType) {
        this(actionType.name(), null, null, null);
    }

    public UserAction(String actionType, UIComponent component) {
        this(actionType, null, component, null);
    }

    public UserAction(String actionType, String route, ViewController origin) {
        this(actionType, route, null, origin);
    }

    public UserAction(UIAction action, UIComponent component) {
        this(action.getType(), action.getRoute(), component);
        this.setRegisterInHistory(action.isRegisterInHistory());
        this.setRefresh(action.getRefresh());
        this.setRestoreView(action.isRestoreView());
        this.setPopHistory(action.getPopHistory());
        this.setController(action.getController());
    }

    public UserAction(UIAction action, ViewController origin) {
        this(action.getType(), action.getRoute(), null, origin);
        this.setRegisterInHistory(action.isRegisterInHistory());
        this.setRefresh(action.getRefresh());
        this.setRestoreView(action.isRestoreView());
        this.setPopHistory(action.getPopHistory());
        this.setController(action.getController());
    }

    public void setRefresh(String refresh) {
        this.refresh = refresh;
    }

    public UserAction(String actionType, String route, UIComponent component) {
        this(actionType, route, component, component.getRoot() == null ? null : component.getRoot().getFormController());
    }

    private UserAction(String actionType, String route, UIComponent component, ViewController origin) {
        this.type = actionType;
        this.route = route;
        this.component = component;
        this.origin = origin;
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public String getRefresh() {
        return refresh;
    }

    public boolean isRegisterInHistory() {
        return registerInHistory;
    }

    public void setRegisterInHistory(boolean registerInHistory) {
        this.registerInHistory = registerInHistory;
    }

    public int getPopHistory() {
        return popHistory;
    }

    public void setPopHistory(int popHistory) {
        this.popHistory = popHistory;
    }

    public boolean isRestoreView() {
        return restoreView;
    }

    public void setRestoreView(boolean restoreView) {
        this.restoreView = restoreView;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

    public void addParam(String param, Object value) {
        if (this.params == null) {
            this.params = new HashMap<>();
        }
        this.params.put(param, value);
    }

    public UIComponent getComponent() {
        return component;
    }

    public void setComponent(UIComponent component) {
        this.component = component;
    }

    public ViewController getOrigin() {
        return origin;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Indicates if current action is going to provoke a change in the view due to a refresh or
     * because it makes the action controller to perform a navigation.
     *
     * @return
     */
    public boolean isViewChangeAction() {
        return StringUtils.isNotBlank(this.refresh) || StringUtils.isNotBlank(this.route);
    }

    public String getController() {
        return controller;
    }

    public void setController(String controller) {
        this.controller = controller;
    }

    /**
     * Creates a new action copying all the attributes but the action parameters
     * from the passed UserAction.
     *
     * @param action
     * @return
     */
    public static UserAction clone(UserAction action) {
        UserAction newAction = new UserAction(action.getType(), action.getComponent());
        newAction.name = action.name;
        newAction.controller = action.controller;
        newAction.route = action.route;
        newAction.refresh = action.refresh;
        newAction.restoreView = action.restoreView;
        newAction.registerInHistory = action.registerInHistory;
        newAction.popHistory = action.popHistory;
        newAction.message = action.message;
        newAction.widget = action.widget;
        return newAction;
    }

    /*********************************/
    /** Default action types **/
    /*********************************/
    public static UserAction navigate(String formId) {
        UserAction action = new UserAction(ActionType.NAV.name(), formId, null, null);
        return action;
    }

    public static UserAction navigate(String formId, UIComponent component) {
        UserAction action = new UserAction(ActionType.NAV.name(), formId, component);
        return action;
    }

    public static UserAction navigate(String formId, ViewController origin) {
        UserAction action = new UserAction(ActionType.NAV.name(), formId, origin);
        return action;
    }

    public static UserAction back(ViewController origin) {
        UserAction action = new UserAction(ActionType.BACK.name(), null, origin);
        return action;
    }

    public Widget getWidget() {
        return widget;
    }

    public void setWidget(Widget widget) {
        this.widget = widget;
    }


    @Override
    public String toString() {
        return "UserAction{" +
                "type='" + type + '\'' +
                ", route='" + route + '\'' +
                ", component='" + component + '\'' +
                ", params=" + params +
                '}';
    }

    /**
     * Indicates if current action has set the value of attribute refresh
     *
     * @return
     */
    public boolean isRefreshSet() {
        return StringUtils.isNotBlank(this.refresh);
    }
}
