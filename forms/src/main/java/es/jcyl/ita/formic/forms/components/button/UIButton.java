package es.jcyl.ita.formic.forms.components.button;
/*
 * Copyright 2020 Rosa María Muñiz (mungarro@itacyl.es), ITACyL (http://www.itacyl.es).
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

import es.jcyl.ita.formic.forms.components.UIInputComponent;
import es.jcyl.ita.formic.forms.components.link.UIParam;

/**
 * @author Rosa María Muñiz (mungarro@itacyl.es)
        */
public class UIButton extends UIInputComponent {

    private static final String BUTTON = "button";
    private String route;
    private UIParam[] params;

    @Override
    public String getRendererType() {
        return BUTTON;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
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

}
