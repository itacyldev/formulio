package es.jcyl.ita.formic.forms.components.inputfield;

import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import com.google.android.material.textfield.TextInputLayout;

import es.jcyl.ita.formic.forms.R;
import es.jcyl.ita.formic.forms.actions.ActionType;
import es.jcyl.ita.formic.forms.actions.UserAction;
import es.jcyl.ita.formic.forms.actions.interceptors.ViewUserActionInterceptor;
import es.jcyl.ita.formic.forms.components.UIComponent;
import es.jcyl.ita.formic.forms.view.helpers.ViewHelper;
import es.jcyl.ita.formic.forms.view.render.InputTextRenderer;
import es.jcyl.ita.formic.forms.view.render.RenderingEnv;
import es.jcyl.ita.formic.forms.view.widget.InputWidget;

import static com.google.android.material.textfield.TextInputLayout.END_ICON_CLEAR_TEXT;

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

public class TextFieldRenderer extends InputTextRenderer<UIField, EditText> {

    @Override
    protected int getWidgetLayoutId(UIField component) {
        return R.layout.widget_textfield;
    }

    @Override
    protected void composeInputView(RenderingEnv env, InputWidget<UIField, EditText> widget) {
        // configure input view elements
        UIField component = widget.getComponent();
        EditText inputView = widget.getInputView();
        if (component.getInputType() != null) {
            inputView.setInputType(component.getInputType());
        }

        TextInputLayout textInputLayout = (TextInputLayout) ViewHelper.findViewAndSetId(widget, R.id.text_input_layout);
        // set floating label
        setLabel(inputView, textInputLayout, component);

        // set event
        addTextChangeListener(env, inputView, component);

        // set clear button
        setClearButton(env, inputView, textInputLayout);
    }

    protected void setLabel(EditText view, TextInputLayout labelView, UIField component) {
        labelView.setTag("label_" + component.getId());
        String labelComponent = (component.isMandatory()) ?
                component.getLabel() + " *"
                : component.getLabel();

        String hintComponent = component.getHint()!=null?component.getHint(): labelComponent;

        view.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    labelView.setHint(hintComponent);
                    labelView.setHintTextAppearance(R.style.HintTextAppearance);
                } else {
                    labelView.setHint(labelComponent);
                    labelView.setHintTextAppearance(R.style.TextInputLabel_label);
                }
            }
        });

        labelView.setHint(labelComponent);
        labelView.setHintTextAppearance(R.style.TextInputLabel_label);
    }

    protected void setClearButton(RenderingEnv env, EditText view, TextInputLayout textInputLayout){
        // set clear button
        textInputLayout.setEndIconActivated(true);
        textInputLayout.setEndIconMode(END_ICON_CLEAR_TEXT);
        TypedArray ta = env.getViewContext().obtainStyledAttributes(new int[]{R.attr.onSurfaceColor});
        textInputLayout.setEndIconTintList(ta.getColorStateList(0));
        textInputLayout.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view.setText(null);
            }
        });
    }

    private void executeUserAction(RenderingEnv env, UIComponent component) {
        ViewUserActionInterceptor interceptor = env.getUserActionInterceptor();
        if (interceptor != null) {
            interceptor.doAction(new UserAction(component, ActionType.INPUT_CHANGE.name()));
        }
    }

    protected void addTextChangeListener(RenderingEnv env, EditText view, UIField component) {
        Handler handler = new Handler(Looper.getMainLooper() /*UI thread*/);

        view.addTextChangedListener(new TextWatcher() {
            Runnable workRunnable = new Runnable() {
                @Override
                public void run() {
                    executeUserAction(env, component);
                }
            };

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!env.isInputDelayDisabled()) {
                    handler.removeCallbacks(workRunnable);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!env.isInterceptorDisabled()) {
                    if (env.isInputDelayDisabled()) {
                        executeUserAction(env, component);
                    } else {
                        handler.postDelayed(workRunnable, env.getInputTypingDelay());
                    }
                }
            }
        });
    }

}
