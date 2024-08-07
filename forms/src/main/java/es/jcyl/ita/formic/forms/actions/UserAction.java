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

import java.util.LinkedHashMap;
import java.util.Map;

import es.jcyl.ita.formic.forms.components.UIComponent;
import es.jcyl.ita.formic.forms.controllers.ViewController;
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

    private boolean stopFlowControl = false;

    private ViewController origin;

    UserAction(String actionType) {
        this.type = actionType;
    }

    UserAction(String actionType, String route, UIComponent component, ViewController origin) {
        this.type = actionType;
        this.route = route;
        this.component = component;
        this.origin = origin;
    }

    public void setRefresh(String refresh) {
        this.refresh = refresh;
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
            this.params = new LinkedHashMap<>();
        }
        this.params.put(param, value);
    }

    public UIComponent getComponent() {
        return component;
    }

    public void setComponent(UIComponent component) {
        this.component = component;
    }

    public void setOrigin(ViewController origin) {
        this.origin = origin;
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

    /*********************************/
    /** Default action types **/
    /*********************************/


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

    public boolean isStopFlowControl() {
        return stopFlowControl;
    }

    public void setStopFlowControl(boolean stopFlowControl) {
        this.stopFlowControl = stopFlowControl;
    }
}
