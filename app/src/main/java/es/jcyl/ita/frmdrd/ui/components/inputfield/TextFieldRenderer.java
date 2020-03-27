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
import es.jcyl.ita.frmdrd.interceptors.OnChangeFieldInterceptor;
import es.jcyl.ita.frmdrd.render.BaseRenderer;
import es.jcyl.ita.frmdrd.ui.components.UIComponent;
import es.jcyl.ita.frmdrd.view.ExecEnvironment;

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

public class TextFieldRenderer extends BaseRenderer {

    public TextFieldRenderer() {
    }

    @Override
    protected View createBaseView(Context viewContext, ExecEnvironment env, UIComponent component) {
        LinearLayout linearLayout = (LinearLayout) View.inflate(viewContext,
                R.layout.tool_alphaedit_text, null);
        return linearLayout;
    }

    @Override
    protected void setupView(View baseView, ExecEnvironment env, UIComponent component) {
        final TextView fieldLabel = (TextView) baseView
                .findViewById(R.id.field_layout_name);
        fieldLabel.setText(component.getLabel());
        fieldLabel.setTag("label");
        final EditText input = (EditText) baseView
                .findViewById(R.id.field_layout_value);
        input.setTag(component.getViewId());
        String strValue = convert(getValue(component, env));
        input.setText(strValue);

        final ImageView resetButton = (ImageView) baseView
                .findViewById(R.id.field_layout_x);
        resetButton.setTag("reset");

        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                OnChangeFieldInterceptor interceptor = env.getChangeInterceptor();
                interceptor.onChange(component);
            }
        });
    }

    private String convert(Object value) {
        // TODO: default converters to savely convert from entity/context value to uicomponent.setText(str)
        return (value == null) ? "" : value.toString();
    }

}
