package es.jcyl.ita.frmdrd.ui.components.inputfield;

import android.content.Context;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import es.jcyl.ita.frmdrd.R;
import es.jcyl.ita.frmdrd.interceptors.OnChangeFieldInterceptor;
import es.jcyl.ita.frmdrd.render.BaseRenderer;
import es.jcyl.ita.frmdrd.ui.components.UIComponent;
import es.jcyl.ita.frmdrd.render.ExecEnvironment;

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

public class CheckBoxFieldRenderer extends BaseRenderer {


    @Override
    protected View createBaseView(Context viewContext, ExecEnvironment env, UIComponent component) {
        return View.inflate(viewContext,
                R.layout.tool_alphaedit_boolean, null);
    }

    @Override
    protected void setupView(View baseView, ExecEnvironment env, UIComponent component) {
        final TextView fieldLabel = baseView
                .findViewById(R.id.field_layout_name);
        fieldLabel.setTag("label");
        final Switch input = baseView
                .findViewById(R.id.field_layout_value);
        input.setTag(component.getViewId());
        Boolean convValue = convert(getValue(component, env));
        input.setChecked(convValue);
        fieldLabel.setText(component.getLabel());

        input.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton,
                                         boolean value) {
                OnChangeFieldInterceptor interceptor = env.getChangeInterceptor();
                interceptor.onChange(component);
            }
        });
    }

    protected boolean convert(Object value) {
        // TODO: use converters with java generics (toddfast vs spring vs apache commons)
        return (value == null) ? false : "true".equalsIgnoreCase(value.toString());
    }
}
