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

import es.jcyl.ita.formic.forms.actions.ActionType;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 * <p>
 * Groups a sequence of actions to link them to a component
 */
public class UIActionGroup extends UIAction {

    private UIAction[] actions;

    public UIActionGroup() {
        this.setType(ActionType.COMPOSITE.name());
    }

    public UIAction[] getActions() {
        return actions;
    }

    public void setActions(UIAction[] actions) {
        this.actions = actions;
    }
}
