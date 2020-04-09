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

import android.content.Context;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import es.jcyl.ita.frmdrd.ui.components.UIComponent;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class UserAction {

    private android.content.Context viewContext;
    private UIComponent component;
    private String type;
    private String name;
    private String route;
    private String origin;
    private Map<String, Serializable> params;

    public UserAction(UIComponent component, String actionType) {
        this.component = component;
        this.type = actionType;
    }

    public UserAction(android.content.Context context, String actionType) {
        this.viewContext = context;
        this.type = actionType;
    }

    public UserAction(android.content.Context context, UIComponent component, String actionType, String route) {
        this.viewContext = context;
        this.component = component;
        this.type = actionType;
        this.route = route;
    }

    public void addParam(String param, Serializable value) {
        if (this.params == null) {
            this.params = new HashMap<>();
        }
        this.params.put(param, value);
    }

    public UIComponent getComponent() {
        return component;
    }

    public String getType() {
        return type;
    }

    public Context getViewContext() {
        return this.viewContext;
    }

    public void setParams(Map<String, Serializable> params) {
        this.params = params;
    }

    public Map<String, Serializable> getParams() {
        return params;
    }

    public String getOrigin() {
        return origin;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getRoute() {
        return route;
    }

    /*********************************/
    /** Default action types **/
    /*********************************/
    public static UserAction navigate(Context context, UIComponent component, String formId) {
        UserAction action = new UserAction(context, component, ActionType.NAVIGATE.name(), formId);
        action.name = "Navigate";
        return action;
    }

    public static UserAction back(Context context) {
        UserAction action = new UserAction(context, null, ActionType.BACK.name(), null);
        return action;
    }

    public static UserAction delete(Context context, UIComponent component, String formId) {
        UserAction action = new UserAction(context, component, ActionType.DELETE.name(), formId);
        action.name = "Delete";
        return action;
    }

    public static UserAction delete(Context context, UIComponent component, String formId, String route) {
        UserAction action = new UserAction(context, component, ActionType.DELETE.name(), formId);
        action.name = "Delete";
        action.route = route;
        return action;
    }

    public static UserAction save(Context context, UIComponent component, String formId) {
        UserAction action = new UserAction(context, component, ActionType.SAVE.name(), formId);
        action.name = "Save";
        return action;
    }

    public static UserAction save(Context context, UIComponent component, String formId, String route) {
        UserAction action = new UserAction(context, component, ActionType.SAVE.name(), formId);
        action.route = route;
        action.name = "Save";
        return action;
    }

    public static UserAction cancel(Context context, UIComponent component, String formId, String route) {
        UserAction action = new UserAction(context, component, ActionType.SAVE.name(), formId);
        action.route = route;
        action.name = "Cancel";
        return action;
    }

}
