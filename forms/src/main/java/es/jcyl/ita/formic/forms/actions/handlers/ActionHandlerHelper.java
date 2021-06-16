package es.jcyl.ita.formic.forms.actions.handlers;
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

import java.util.ArrayList;
import java.util.List;

import es.jcyl.ita.formic.forms.actions.UserAction;
import es.jcyl.ita.formic.forms.actions.UserActionException;
import es.jcyl.ita.formic.forms.components.view.ViewWidget;
import es.jcyl.ita.formic.forms.controllers.widget.WidgetController;

import static es.jcyl.ita.formic.forms.config.DevConsole.error;

/**
 * Action Handler shared methods
 *
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class ActionHandlerHelper {

    public static List<WidgetController> getControllers(ViewWidget rootWidget, UserAction action) {
        List<WidgetController> lst = new ArrayList<>();
        String ctrlIds[] = StringUtils.split(action.getController(), ",");
        String notFoundIds = "";
        for (String id : ctrlIds) {
            WidgetController controller = rootWidget.getWidgetController(id.trim());
            if (controller == null) {
                notFoundIds += id + ", ";
            } else {
                lst.add(controller);
            }
        }
        if (notFoundIds.length() > 0) {
            notFoundIds = notFoundIds.substring(0,notFoundIds.length()-2);
            throw new UserActionException(error(String.format("An attempt to execute save() on " +
                    "WidgetController(s) [%s] was made but it/they cannot be found in current view." +
                    "Check the 'controller' attribute in action [%s].", notFoundIds, action)));
        }
        return lst;
    }
}
