package es.jcyl.ita.formic.forms.controllers.operations;
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

import java.util.Map;

import es.jcyl.ita.formic.forms.MainController;
import es.jcyl.ita.formic.forms.components.UIInputComponent;
import es.jcyl.ita.formic.forms.components.form.FormWidget;
import es.jcyl.ita.formic.forms.components.form.UIForm;
import es.jcyl.ita.formic.forms.components.inputfield.UIField;
import es.jcyl.ita.formic.forms.context.impl.ViewContext;
import es.jcyl.ita.formic.forms.controllers.FormController;
import es.jcyl.ita.formic.forms.scripts.ScriptEngine;
import es.jcyl.ita.formic.forms.validation.Validator;
import es.jcyl.ita.formic.forms.validation.ValidatorException;
import es.jcyl.ita.formic.forms.view.helpers.ViewHelper;
import es.jcyl.ita.formic.forms.view.widget.InputWidget;
import es.jcyl.ita.formic.forms.view.render.renderer.WidgetContext;
import es.jcyl.ita.formic.forms.view.widget.WidgetContextHelper;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class FormValidator {

    private final MainController mc;

    public FormValidator(MainController mc) {
        this.mc = mc;
    }

    /**
     * Runs validation on the given element
     *
     * @param inputWidget
     * @return
     */
    public boolean validate(InputWidget inputWidget) {
        WidgetContext widgetContext = inputWidget.getWidgetContext();
        ViewContext viewContext = widgetContext.getViewContext();
        UIField field = (UIField) inputWidget.getComponent();

        // get user input using view context and check all validators.
        String value = viewContext.getString(field.getId());
        boolean valid = true;
        WidgetContextHelper.clearMessage(widgetContext, field.getId());
        for (Validator validator : field.getValidators()) {
            try {
                if (inputWidget.isVisible()) {
                    validator.validate(widgetContext, field, value);
                }
            } catch (ValidatorException e) {
                // get the error and put it in form context
                WidgetContextHelper.setMessage(widgetContext, field.getId(), e.getMessage());
                valid = false;
            }
        }
        // call validation function
        UIForm form = field.getParentForm();
        if (form.getOnValidate() != null) {
            ScriptEngine scriptEngine = mc.getScriptEngine();
            // setup validation context
            scriptEngine.putProperty("view", viewContext);
            scriptEngine.putProperty("entity", widgetContext.getEntity());

            // TODO: we have to pass a combination of globalContext + formContext
            Map result = (Map) scriptEngine.callFunction(form.getOnValidate());
            if (result.containsKey("error")) {
                WidgetContextHelper.setMessage(widgetContext, field.getId(), (String) result.get("message"));
                valid = false;
            }
        }
        return valid;
    }

    /**
     * Runs validation on all the elements of the given form.
     *
     * @param formWidget
     * @return
     */
    public boolean validate(FormWidget formWidget) {
        boolean valid = true;
        WidgetContextHelper.clearMessages(formWidget.getWidgetContext());
        UIForm form = formWidget.getComponent();
        for (UIInputComponent field : form.getFields()) {
            // validate
            InputWidget inputWidget = ViewHelper.findInputWidget(formWidget, field);
            valid &= validate(inputWidget);
        }
        return valid;
    }

}
