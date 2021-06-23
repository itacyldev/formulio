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

import java.util.List;

import es.jcyl.ita.formic.forms.components.UIInputComponent;
import es.jcyl.ita.formic.forms.view.widget.WidgetContextHolder;
import es.jcyl.ita.formic.forms.context.impl.ViewContext;
import es.jcyl.ita.formic.forms.validation.Validator;
import es.jcyl.ita.formic.forms.validation.ValidatorException;
import es.jcyl.ita.formic.forms.view.render.renderer.RenderingEnv;
import es.jcyl.ita.formic.forms.view.render.renderer.WidgetContext;
import es.jcyl.ita.formic.forms.view.widget.InputWidget;
import es.jcyl.ita.formic.forms.view.widget.StatefulWidget;
import es.jcyl.ita.formic.forms.view.widget.WidgetContextHelper;

/**
 * Stateless class that implements WidgetContext validation
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class BaseWidgetValidator implements WidgetValidator {

    protected final RenderingEnv env;

    public BaseWidgetValidator(RenderingEnv env){
        this.env = env;
    }

    /**
     * Uses WidgetContext to retrieve InputWidgets, validate the user input and set error
     * messages in WidgetContext
     *
     * @param ctxHolder
     * @return
     */
    @Override
    public final boolean validate(WidgetContextHolder ctxHolder) {
        WidgetContext widgetContext = ctxHolder.getWidgetContext();
        WidgetContextHelper.clearMessages(widgetContext);

        boolean valid = true;
        List<StatefulWidget> statefulWidgets = widgetContext.getStatefulWidgets();
        for (StatefulWidget w : statefulWidgets) {
            if (w instanceof InputWidget) {
                // validate input
                valid &= validate((InputWidget) w);
            }
        }
        valid &= customValidate(ctxHolder);
        return valid;
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
        UIInputComponent field = (UIInputComponent) inputWidget.getComponent();

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
        return valid;
    }
    /**
     * Extension point for subclases
     * @param ctxHolder
     * @return
     */
    protected boolean customValidate(WidgetContextHolder ctxHolder) {
        return true;
    }
}
