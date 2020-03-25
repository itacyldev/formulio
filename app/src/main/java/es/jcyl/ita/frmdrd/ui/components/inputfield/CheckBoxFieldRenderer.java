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

public class CheckBoxFieldRenderer extends BaseRenderer {

    public CheckBoxFieldRenderer() {
    }

//    public CheckBoxFieldRenderer(Context context, Lifecycle lifecycle) {
//        super(context, lifecycle);
//    }
//
//    @Override
//    public View render(final UIField field) {
//        String renderCondition = field.getRenderCondition();
//
//        boolean render = true;
//        if (StringUtils.isNotEmpty(renderCondition)) {
//            render = this.validateCondition(renderCondition, field.getId());
//        }
//
//
//        LinearLayout linearLayout = null;
//
//        linearLayout = (LinearLayout) View.inflate(context,
//                R.layout.tool_alphaedit_boolean, null);
//
//        final TextView fieldLabel = linearLayout
//                .findViewById(R.id.field_layout_name);
//        fieldLabel.setTag("label");
//        final Switch input = linearLayout
//                .findViewById(R.id.field_layout_value);
//        input.setTag("input");
//
//
//        fieldLabel.setText(field.getLabel());
//
//        input.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton,
//                                         boolean value) {
//                onChangeInterceptor.onChange(field.getId());
//            }
//        });
//
//        linearLayout.setVisibility(render ? View.VISIBLE : View.INVISIBLE);
//
//        bindField(field, input);
//
//        return linearLayout;
//    }


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
        input.setTag("input");

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
}
