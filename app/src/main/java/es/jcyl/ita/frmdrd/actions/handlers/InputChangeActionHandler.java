package es.jcyl.ita.frmdrd.actions.handlers;
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

import es.jcyl.ita.frmdrd.MainController;
import es.jcyl.ita.frmdrd.actions.ActionHandler;
import es.jcyl.ita.frmdrd.actions.UserAction;
import es.jcyl.ita.frmdrd.context.impl.FormViewContext;
import es.jcyl.ita.frmdrd.ui.components.UIComponent;
import es.jcyl.ita.frmdrd.ui.components.form.UIForm;
import es.jcyl.ita.frmdrd.ui.components.inputfield.UIField;
import es.jcyl.ita.frmdrd.view.InputFieldView;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class InputChangeActionHandler implements ActionHandler {

    @Override
    public void handle(UserAction action) {
        MainController mc = MainController.getInstance();

        UIComponent component = action.getComponent();
        if (!(component instanceof UIField)) {
            return;// nothing to do, no input element
        }
        UIForm form = component.getParentForm();
        FormViewContext viewContext = form.getContext().getViewContext();
        InputFieldView fieldView = viewContext.findInputFieldViewById(component.getId());

        // save view state
        String state = fieldView.getValueString();
        boolean valid = mc.getFormController().validate((UIField) component);
        if (!valid) {
            // update the view to show messages
            mc.updateView(component);
            // find the new View and restore state
            fieldView = viewContext.findInputFieldViewById(component.getId());
            mc.getExecEnvironment().disableInterceptors();
            fieldView.setValueString(state);
            // restore focus on the current view element
            fieldView.setFocus(true);
            mc.getExecEnvironment().enableInterceptors();
        }
    }

}