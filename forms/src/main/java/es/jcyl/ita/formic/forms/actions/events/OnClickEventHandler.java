package es.jcyl.ita.formic.forms.actions.events;
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

import es.jcyl.ita.formic.forms.MainController;
import es.jcyl.ita.formic.forms.actions.ActionController;
import es.jcyl.ita.formic.forms.actions.UserAction;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class OnClickEventHandler
        implements EventHandler {

    private final MainController mc;
    private final ActionController ac;

    public OnClickEventHandler(MainController mc, ActionController actionController) {
        this.mc = mc;
        this.ac = actionController;
    }

    @Override
    public void handle(Event event) {
        // if the component has defined an UserAction for this event, use it
        UserAction action = event.getHandler();
        if (action != null) {
            if (event.getSource() != null) {
                action.addParam("parentContext", event.getSource().getRootWidget().getContext());
            }
            ac.execAction(action);
            if (action.isViewChangeAction()) {
                // if the action forces a transition to another view we don't need to update
                // view values and re-render components
                return;
            }
        }
    }
}
