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
import es.jcyl.ita.formic.forms.components.UIComponent;
import es.jcyl.ita.formic.forms.components.UIInputComponent;
import es.jcyl.ita.formic.forms.components.form.UIForm;
import es.jcyl.ita.formic.forms.context.impl.ComponentContext;
import es.jcyl.ita.formic.forms.context.impl.ViewContext;
import es.jcyl.ita.formic.forms.controllers.operations.FormValidator;
import es.jcyl.ita.formic.forms.view.widget.InputWidget;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class OnInputChangeEventHandler
        implements EventHandler {

    private final MainController mc;
    private final ActionController ac;
    private final FormValidator formValidator;

    public OnInputChangeEventHandler(MainController mc, ActionController actionController) {
        this.mc = mc;
        this.ac = actionController;
        this.formValidator = new FormValidator(mc);
    }

    @Override
    public void handle(Event event) {
        UIComponent component = event.getSource().getComponent();

        // if the component has defined an UserAction for this event, use it
        UserAction action = event.getHandler();
        if(action != null){
            ac.doUserAction(action);
            if(action.isViewChangeAction()){
                // if the action forces a transition to another view we don't need to update
                // view values and re-render components
                return;
            }
        }
        if (!(component instanceof UIInputComponent)) {
            return;// nothing to do, no input element
        }
        ComponentContext cContext = component.getParentContext();
        ViewContext viewContext = cContext.getViewContext();
        InputWidget fieldView = viewContext.findInputFieldViewById(component.getId());
        if(fieldView == null){
            return; // no input field related to current component
        }

        // TODO: FORMIC-224 Refactorizar FormControllers, eliminar diferencias entre Edit y list
        // save view state
        Object state = fieldView.getValue();
        boolean valid = formValidator.validate(component.getParentForm(), (UIInputComponent) component);
        if (!valid) {
            // update the view to show messages
            mc.updateView(component, false);
            // find the new View and restore state
            fieldView = viewContext.findInputFieldViewById(component.getId());
            mc.getRenderingEnv().disableInterceptors();
            try {
                fieldView.setValue(state);
                // restore focus on the current view element
                fieldView.setFocus(true);
            } finally {
                mc.getRenderingEnv().enableInterceptors();
            }
        } else {
            // render depending objects
            mc.updateDependants(component);
        }
    }

}
