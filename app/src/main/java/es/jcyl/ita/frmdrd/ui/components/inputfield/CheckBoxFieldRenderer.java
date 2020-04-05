package es.jcyl.ita.frmdrd.ui.components.inputfield;

import android.content.Context;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import es.jcyl.ita.frmdrd.R;
import es.jcyl.ita.frmdrd.actions.ActionType;
import es.jcyl.ita.frmdrd.actions.UserAction;
import es.jcyl.ita.frmdrd.actions.interceptors.ViewUserActionInterceptor;
import es.jcyl.ita.frmdrd.ui.components.UIComponent;
import es.jcyl.ita.frmdrd.view.InputFieldView;
import es.jcyl.ita.frmdrd.view.ViewHelper;
import es.jcyl.ita.frmdrd.view.render.RenderingEnv;
import es.jcyl.ita.frmdrd.view.render.FieldRenderer;

/*
 * Copyright 2020 Javier Ramos (javier.ramos@itacyl.es), ITACyL (http://www.itacyl.es).
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

/**
 * @author Javier Ramos (javier.ramos@itacyl.es)
 */

public class CheckBoxFieldRenderer extends FieldRenderer {

    @Override
    protected View createBaseView(Context viewContext, RenderingEnv env, UIComponent component) {
        View baseView = ViewHelper.inflate(viewContext,
                R.layout.tool_alphaedit_boolean, View.class);
        return createInputFieldView(viewContext, baseView, component);
    }

    @Override
    protected void setupView(View baseView, RenderingEnv env, UIComponent component) {
        TextView fieldLabel = ViewHelper.findViewAndSetId(baseView, R.id.field_layout_name, TextView.class);
        fieldLabel.setTag("label");
        Switch input = ViewHelper.findViewAndSetId(baseView, R.id.field_layout_value, Switch.class);
        input.setTag(getInputTag(component));

        // get component value and set in view
        Boolean boolValue = getValue(component, env, Boolean.class);
        input.setChecked(boolValue);
        ((InputFieldView) baseView).setInputView(input);

        input.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton,
                                         boolean value) {
                ViewUserActionInterceptor interceptor = env.getUserActionInterceptor();
                if (interceptor != null) {
                    interceptor.doAction(new UserAction(component, ActionType.INPUT_CHANGE));
                }
            }
        });
    }

    @Override
    protected <T> T handleNullValue(Object value) {
        return (T) Boolean.FALSE;
    }

}
