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
import es.jcyl.ita.formic.forms.R;
import es.jcyl.ita.formic.forms.actions.ActionController;
import es.jcyl.ita.formic.forms.actions.UserAction;
import es.jcyl.ita.formic.forms.config.Config;
import es.jcyl.ita.formic.forms.view.UserMessagesHelper;
import es.jcyl.ita.formic.forms.view.widget.WidgetContextHolder;
import es.jcyl.ita.formic.forms.controllers.operations.WidgetValidator;
import es.jcyl.ita.formic.forms.controllers.widget.WidgetController;
import es.jcyl.ita.formic.forms.view.widget.ControllableWidget;
import es.jcyl.ita.formic.forms.view.widget.InputWidget;
import es.jcyl.ita.formic.forms.view.widget.Widget;

import static es.jcyl.ita.formic.forms.config.DevConsole.error;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class OnInputChangeEventHandler
        implements EventHandler {

    private final MainController mc;
    private final ActionController ac;

    public OnInputChangeEventHandler(MainController mc, ActionController actionController) {
        this.mc = mc;
        this.ac = actionController;
    }

    @Override
    public void handle(Event event) {
        try {
            Widget widget = event.getSource();
            // if the component has defined an UserAction for this event, use it
            UserAction action = event.getHandler();
            if (action != null) {
                ac.doUserAction(action);
                if (action.isViewChangeAction()) {
                    // if the action forces a transition to another view we don't need to update
                    // view values and re-render components
                    return;
                }
            }
            if (!(widget instanceof InputWidget)) {
                return;
            }
            InputWidget fieldView = (InputWidget) widget;
            WidgetContextHolder holder = widget.getWidgetContext().getHolder();
            WidgetValidator validator = null;
            if (holder instanceof ControllableWidget) {
                WidgetController controller = ((ControllableWidget) holder).getController();
                validator = controller.getValidator();
            }
            boolean valid = true;
            if (validator != null) {
                valid = validator.validate(fieldView);
            }

            // save view state
            Object state = fieldView.getValue();
            if (!valid) {
                // update the view to show messages
                InputWidget newFieldView = (InputWidget) mc.updateView(fieldView, false);
                mc.getRenderingEnv().disableInterceptors();
                // set values in new View
                try {
                    newFieldView.setValue(state);
                    newFieldView.setFocus(true);
                } finally {
                    mc.getRenderingEnv().enableInterceptors();
                }
            } else {
                // render depending objects
                mc.updateDependants(widget);
            }
        } catch (Exception e) {
            String msg = Config.getInstance().getStringResource(R.string.action_generic_error);
            error(msg, e);
            mc.renderBack();
            // show error message
            UserMessagesHelper.toast(mc.getRenderingEnv().getAndroidContext(), msg);
        }
    }

}
