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
import es.jcyl.ita.formic.forms.components.form.UIForm;
import es.jcyl.ita.formic.forms.context.FormContextHelper;
import es.jcyl.ita.formic.forms.context.impl.ComponentContext;
import es.jcyl.ita.formic.forms.context.impl.ViewContext;
import es.jcyl.ita.formic.forms.controllers.FormController;
import es.jcyl.ita.formic.forms.scripts.ScriptEngine;
import es.jcyl.ita.formic.forms.validation.Validator;
import es.jcyl.ita.formic.forms.validation.ValidatorException;
import es.jcyl.ita.formic.forms.view.widget.InputWidget;

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
     * @param field
     * @return
     */
    public boolean validate(UIForm form, UIInputComponent field) {
        ComponentContext context = form.getContext();
        ViewContext viewContext = context.getViewContext();

        // get user input using view context and check all validators.
        String value = viewContext.getString(field.getId());
        boolean valid = true;
        for (Validator validator : field.getValidators()) {
            try {
                if (isVisible(context, field)) {
                    validator.validate(context, field, value);
                }
            } catch (ValidatorException e) {
                // get the error and put it in form context
                FormContextHelper.setMessage(context, field.getId(), e.getMessage());
                valid = false;
            }
        }
        // call validation function
        if (form.getOnValidate() != null) {
            ScriptEngine srcEngine = mc.getScriptEngine();
            FormController fc = form.getRoot().getFormController();

            // TODO: we have to pass a combination of globalContext + formContext
            Map result = (Map) srcEngine.callFunction(fc.getId(), form.getOnValidate());
            if (result.containsKey("error")) {
                FormContextHelper.setMessage(context, form.getId(), (String) result.get("message"));
                valid = false;
            }
        }
        return valid;
    }

    public boolean validate(UIForm form) {
        boolean valid = true;
        form.getContext().clearMessages();
        for (UIInputComponent field : form.getFields()) {
            // validate
            valid &= validate(form, field);
        }
        return valid;
    }


    public boolean isVisible(ComponentContext context, UIInputComponent field) {
        ViewContext viewContext = context.getViewContext();
        // TODO: replace with field.isRendered(context);
        InputWidget fieldView = viewContext.findInputFieldViewById(field.getId());
        return fieldView.isVisible();
    }
}
