package es.jcyl.ita.frmdrd.forms;
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

import es.jcyl.ita.frmdrd.actions.UserAction;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 * 
 * User actions/buttons to be shown in the FormController interface
 */
public class FCAction {

    private final String route;
    private final String label;
    private final String type;

    public FCAction(String type, String label, String route){
        this.type = type;
        this.label = label;
        this.route = route;
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
}
