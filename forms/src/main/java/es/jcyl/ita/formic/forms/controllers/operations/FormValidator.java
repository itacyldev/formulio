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

import es.jcyl.ita.formic.forms.components.form.FormWidget;
import es.jcyl.ita.formic.forms.components.form.UIForm;
import es.jcyl.ita.formic.forms.view.widget.WidgetContextHolder;
import es.jcyl.ita.formic.forms.scripts.ScriptEngine;
import es.jcyl.ita.formic.forms.view.render.renderer.RenderingEnv;
import es.jcyl.ita.formic.forms.view.render.renderer.WidgetContext;
import es.jcyl.ita.formic.forms.view.render.renderer.MessageHelper;

/**
 * Form validator that adds js validation functionality
 *
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class FormValidator extends BaseWidgetValidator {

    public FormValidator(RenderingEnv env) {
        super(env);
    }

    @Override
    protected boolean customValidate(WidgetContextHolder ctxHolder) {
        FormWidget widget = (FormWidget) ctxHolder.getWidget();
        WidgetContext widgetContext = ctxHolder.getWidgetContext();
        UIForm form = widget.getComponent();

        boolean valid = true;
        // call validation function
        if (form != null && form.getOnValidate() != null) {
            ScriptEngine scriptEngine = env.getScriptEngine();
            // setup validation context
            scriptEngine.putProperty("view", widgetContext.getViewContext());
            scriptEngine.putProperty("entity", widgetContext.getEntity());

            Map result = (Map) scriptEngine.callFunction(form.getOnValidate());
            if (result.containsKey("error")) {
                MessageHelper.setMessage(widgetContext, form.getId(), (String) result.get("message"));
                valid = false;
            }
        }
        return valid;
    }
}
