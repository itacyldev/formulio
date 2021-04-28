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

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import es.jcyl.ita.formic.forms.components.UIComponent;
import es.jcyl.ita.formic.forms.controllers.FormController;
import es.jcyl.ita.formic.forms.controllers.UIAction;
import es.jcyl.ita.formic.forms.view.widget.Widget;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class UserAction {

    private String type;
    private String name;
    private String route;
    private boolean forceRefresh = false;
    private boolean registerInHistory = true;
    private String message;
    private Map<String, Object> params;
    private UIComponent component;
    private Widget widget;

    private FormController origin;

    public UserAction(ActionType actionType) {
        this(actionType.name(), null, null, null);
    }

    public UserAction(String actionType, UIComponent component) {
        this(actionType, null, component, null);
    }

    public UserAction(String actionType, String route, FormController origin) {
        this(actionType, route, null, origin);
    }

    public UserAction(UIAction action, UIComponent component) {
        this(action.getType(), action.getRoute(), component);
        this.setRegisterInHistory(action.isRegisterInHistory());
        this.setForceRefresh(action.isForceRefresh());
    }

    public UserAction(String actionType, String route, UIComponent component) {
        this(actionType, route, component, component.getRoot() == null ? null : component.getRoot().getFormController());
    }

    private UserAction(String actionType, String route, UIComponent component, FormController origin) {
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

    public boolean isForceRefresh() {
        return forceRefresh;
    }

    public void setForceRefresh(boolean forceRefresh) {
        this.forceRefresh = forceRefresh;
    }

    public boolean isRegisterInHistory() {
        return registerInHistory;
    }

    public void setRegisterInHistory(boolean registerInHistory) {
        this.registerInHistory = registerInHistory;
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

    public FormController getOrigin() {
        return origin;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Indicates if current action is going to provoke a change in the view due to a forceRefresh or
     * because it makes the action controller to perform a navigation.
     *
     * @return
     */
    public boolean isViewChangeAction() {
        return this.forceRefresh || StringUtils.isNotBlank(this.route);
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

    public static UserAction navigate(String formId, FormController origin) {
        UserAction action = new UserAction(ActionType.NAV.name(), formId, origin);
        return action;
    }

    public static UserAction back(FormController origin) {
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

}
