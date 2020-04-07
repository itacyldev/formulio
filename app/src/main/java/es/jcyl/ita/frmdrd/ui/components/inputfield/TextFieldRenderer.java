package es.jcyl.ita.frmdrd.ui.components.inputfield;

import android.content.Context;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import es.jcyl.ita.frmdrd.R;
import es.jcyl.ita.frmdrd.actions.ActionType;
import es.jcyl.ita.frmdrd.actions.UserAction;
import es.jcyl.ita.frmdrd.actions.interceptors.ViewUserActionInterceptor;
import es.jcyl.ita.frmdrd.context.FormContextHelper;
import es.jcyl.ita.frmdrd.context.impl.FormContext;
import es.jcyl.ita.frmdrd.ui.components.UIComponent;
import es.jcyl.ita.frmdrd.ui.components.UIField;
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

public class TextFieldRenderer extends FieldRenderer {


    @Override
    protected View createBaseView(Context viewContext, RenderingEnv env, UIField component) {
        LinearLayout baseView = ViewHelper.inflate(viewContext,
                R.layout.tool_alphaedit_text, LinearLayout.class);
        return createInputFieldView(viewContext, baseView, component);
    }

    @Override
    protected void setupView(View baseView, RenderingEnv env, UIField component) {
        TextView fieldLabel = ViewHelper.findViewAndSetId(baseView, R.id.field_layout_name,
                TextView.class);
        fieldLabel.setText(component.getLabel());
        fieldLabel.setTag("label");
        EditText input = ViewHelper.findViewAndSetId(baseView, R.id.field_layout_value,
                EditText.class);
        input.setTag(getInputTag(component));
        // get component value and set in view
        String strValue = getValue(component, env, String.class);
        input.setText(strValue);
        input.setInputType(component.getInputType());
        setMessages(env.getFormContext(), component, input);
        ((InputFieldView) baseView).setInputView(input);

        ImageView resetButton = ViewHelper.findViewAndSetId(baseView, R.id.field_layout_x,
                ImageView.class);
        resetButton.setTag("reset");

        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                ViewUserActionInterceptor interceptor = env.getUserActionInterceptor();
                if (interceptor != null) {
                    interceptor.doAction(new UserAction(component, ActionType.INPUT_CHANGE));
                }
            }
        });
    }

    private void setMessages(FormContext formContext, UIField component, EditText input) {
        String message = FormContextHelper.getMessage(formContext, component.getId());
        if (message != null) {
            input.setError(message);
        }
    }

    @Override
    protected <T> T handleNullValue(Object value) {
        return (T) EMPTY_STRING;
    }
}
