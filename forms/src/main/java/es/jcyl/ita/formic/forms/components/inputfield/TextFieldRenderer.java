package es.jcyl.ita.formic.forms.components.inputfield;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;

import org.mini2Dx.beanutils.ConvertUtils;

import es.jcyl.ita.formic.forms.R;
import es.jcyl.ita.formic.forms.actions.events.Event;
import es.jcyl.ita.formic.forms.actions.events.UserEventInterceptor;
import es.jcyl.ita.formic.forms.view.helpers.ViewHelper;
import es.jcyl.ita.formic.forms.view.render.InputTextRenderer;
import es.jcyl.ita.formic.forms.view.render.renderer.RenderingEnv;
import es.jcyl.ita.formic.forms.view.widget.InputWidget;
import es.jcyl.ita.formic.forms.view.widget.Widget;

import static android.view.View.inflate;
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
        // set event
        addTextChangeListener(env, inputView, widget);

        TextInputLayout textInputLayout = (TextInputLayout) ViewHelper.findViewAndSetId(widget, R.id.text_input_layout);

        removeUnderline(env, component, textInputLayout);

        setOnClickListenerResetButton(component, inputView);

    }

    protected void setOnClickListenerResetButton(UIField component, EditText inputView) {
        ImageView resetButton = component.getResetButton();
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View arg0) {
                inputView.setText(null);
            }
        });
    }

    protected void setLabel(EditText view, TextInputLayout labelView, UIField component) {
        labelView.setTag("label_" + component.getId());
        String labelComponent = (component.isMandatory()) ?
                component.getLabel() + " *"
                : component.getLabel();

        view.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    labelView.setHintTextAppearance(R.style.HintTextAppearance);
                } else {
                    labelView.setHintTextAppearance(R.style.TextInputLabel_label);
                }
            }
        });

        labelView.setHint(labelComponent);
        labelView.setHintTextAppearance(R.style.TextInputLabel_label);
    }

    protected void setClearButton(RenderingEnv env, EditText view, TextInputLayout textInputLayout, UIField component) {
        // set clear button
        if (!(Boolean) ConvertUtils.convert(component.isReadonly(env.getWidgetContext()), Boolean.class)) {
            textInputLayout.setEndIconActivated(true);
            textInputLayout.setEndIconMode(END_ICON_CLEAR_TEXT);
            TypedArray ta = env.getAndroidContext().obtainStyledAttributes(new int[]{R.attr.onSurfaceColor});
            textInputLayout.setEndIconTintList(ta.getColorStateList(0));
            textInputLayout.setEndIconOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    view.setText(null);
                }
            });
        }
    }

   /*protected void setInfoButton(RenderingEnv env, TextInputLayout textInputLayout, UIField component) {
        if (component.getHint() != null) {
            textInputLayout.setEndIconDrawable(R.drawable.ic_tool_info);
            TypedArray ta = env.getAndroidContext().obtainStyledAttributes(new int[]{R.attr.onSurfaceColor});
            textInputLayout.setEndIconTintList(ta.getColorStateList(0));
            textInputLayout.setEndIconOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    createInfoDialog(env, component);
                }
            });
        }
    }*/

    protected void createInfoDialog(RenderingEnv env, UIField component) {
        AlertDialog.Builder builder = new AlertDialog.Builder(env.getAndroidContext(), R.style.DialogStyle);
        final View view = inflate(env.getAndroidContext(), R.layout.info_dialog, null);
        TextView titleView = view.findViewById(R.id.info);
        titleView.setText(component.getHint(env.getWidgetContext()));
        builder.setCustomTitle(view)
                .setPositiveButton("OK", null);
        Dialog dialog = builder.create();
        dialog.show();
    }

    protected void adjustBounds() {

    }

    private void executeUserAction(RenderingEnv env, Widget widget) {
        UserEventInterceptor interceptor = env.getUserActionInterceptor();
        if (interceptor != null) {
            interceptor.notify(Event.inputChange(widget));
        }
    }

    protected void addTextChangeListener(RenderingEnv env, EditText view, Widget widget) {
        Handler handler = new Handler(Looper.getMainLooper() /*UI thread*/);

        view.addTextChangedListener(new TextWatcher() {
            Runnable workRunnable = new Runnable() {
                @Override
                public void run() {
                    executeUserAction(env, widget);
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
                        executeUserAction(env, widget);
                    } else {
                        handler.postDelayed(workRunnable, env.getInputTypingDelay());
                    }
                }
            }
        });
    }

    protected void removeUnderline(RenderingEnv env, UIField component, TextInputLayout textInputLayout) {
        if ((Boolean) ConvertUtils.convert(component.isReadonly(env.getWidgetContext()), Boolean.class)) {
            textInputLayout.setBoxBackgroundMode(TextInputLayout.BOX_BACKGROUND_NONE);
        }
    }

}
