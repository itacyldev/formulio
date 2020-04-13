package es.jcyl.ita.frmdrd.ui.components.inputfield;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.LinearLayout;

import es.jcyl.ita.frmdrd.R;
import es.jcyl.ita.frmdrd.actions.ActionType;
import es.jcyl.ita.frmdrd.actions.UserAction;
import es.jcyl.ita.frmdrd.actions.interceptors.ViewUserActionInterceptor;
import es.jcyl.ita.frmdrd.context.FormContextHelper;
import es.jcyl.ita.frmdrd.view.InputFieldView;
import es.jcyl.ita.frmdrd.view.ViewHelper;
import es.jcyl.ita.frmdrd.view.render.InputRenderer;
import es.jcyl.ita.frmdrd.view.render.RenderingEnv;

/*
 * Copyright 2020 Gustavo Río Briones (gustavo.rio@itacyl.es), ITACyL (http://www.itacyl.es).
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
 * @author Gustavo Río Briones (gustavo.rio@itacyl.es)
 */

public class TextFieldRenderer extends InputRenderer<EditText, UIField> {

    @Override
    protected InputFieldView createBaseView(RenderingEnv env, UIField component) {
        LinearLayout baseView = ViewHelper.inflate(env.getViewContext(),
                R.layout.component_textfield, LinearLayout.class);
        return createInputFieldView(env.getViewContext(), baseView, component);
    }

    @Override
    protected int getComponentLayout() {
        return R.layout.component_textfield;
    }

    @Override
    protected void setMessages(RenderingEnv env, InputFieldView<EditText> baseView, UIField component) {
        String message = FormContextHelper.getMessage(env.getFormContext(), component.getId());
        if (message != null) {
            baseView.getInputView().setError(message);
        }
    }

    @Override
    protected void composeView(RenderingEnv env, InputFieldView<EditText> baseView, UIField component) {
        // configure input view elements
        baseView.getInputView().setInputType(component.getInputType());
        // set event
        baseView.getInputView().addTextChangedListener(new TextWatcher() {
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
                    interceptor.doAction(new UserAction(component, ActionType.INPUT_CHANGE.name()));
                }
            }
        });
    }

    @Override
    protected <T> T handleNullValue(UIField component) {
        return (T) EMPTY_STRING;
    }

}
