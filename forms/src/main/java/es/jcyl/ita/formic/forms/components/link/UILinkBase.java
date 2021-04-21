package es.jcyl.ita.formic.forms.components.link;
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

import es.jcyl.ita.formic.forms.components.UIComponent;

import static es.jcyl.ita.formic.forms.components.link.UILinkBase.TYPE.LINK;


/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 * <p>
 * User navigation component
 */
public class UILinkBase extends UIComponent {

    public enum TYPE {
        LINK, BUTTON
    }

    private TYPE type = LINK;

    private String route;

    @Override
    public String getRendererType() {return type.name().toLowerCase();}

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }
}