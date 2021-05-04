package es.jcyl.ita.formic.forms.controllers;
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

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 * <p>
 * Allowed user actions/buttons to be shown in the FormController interface
 */
public class UIAction {

    private String id;
    private String label;
    private String type;
    private String route;
    private boolean registerInHistory = true;
    private boolean forceRefresh = false;
    private UIParam[] params;
    private String message;

    public UIAction() {
    }

    public UIAction(String type, String label, String route) {
        this.type = type;
        this.label = label;
        this.route = route;
    }

    public UIAction(UIAction action) {
        this(action.type, action.label, action.route);
        this.forceRefresh = action.isForceRefresh();
        this.registerInHistory = action.isRegisterInHistory();
        this.message = action.getMessage();
        // clone parameters
        if (action.getParams() != null) {
            int i = 0;
            this.params = new UIParam[action.getParams().length];
            for (UIParam param : action.getParams()) {
                this.params[i] = new UIParam(param);
                i++;
            }
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRoute() {
        return route;
    }

    public String getLabel() {
        return label;
    }

    public String getType() {
        return type;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public boolean isRegisterInHistory() {
        return registerInHistory;
    }

    public void setRegisterInHistory(boolean registerInHistory) {
        this.registerInHistory = registerInHistory;
    }

    public UIParam[] getParams() {
        return params;
    }

    public void setParams(UIParam[] params) {
        this.params = params;
    }

    public boolean hasParams() {
        return this.params != null && this.params.length > 0;
    }

    public boolean isForceRefresh() {
        return forceRefresh;
    }

    public void setForceRefresh(boolean forceRefresh) {
        this.forceRefresh = forceRefresh;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
