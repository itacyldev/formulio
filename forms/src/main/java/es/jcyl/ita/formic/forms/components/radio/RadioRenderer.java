package es.jcyl.ita.formic.forms.components.radio;

import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import es.jcyl.ita.formic.core.context.FormContextHelper;
import es.jcyl.ita.formic.forms.R;
import es.jcyl.ita.formic.forms.actions.ActionType;
import es.jcyl.ita.formic.forms.actions.UserAction;
import es.jcyl.ita.formic.forms.actions.interceptors.ViewUserActionInterceptor;
import es.jcyl.ita.formic.forms.components.UIInputComponent;
import es.jcyl.ita.formic.forms.components.option.UIOption;
import es.jcyl.ita.formic.forms.view.helpers.ViewHelper;
import es.jcyl.ita.formic.forms.view.render.InputRenderer;
import es.jcyl.ita.formic.forms.view.render.RenderingEnv;
import es.jcyl.ita.formic.forms.view.widget.InputWidget;

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

public class RadioRenderer extends InputRenderer<UIRadio, RadioGroup> {
//    private static final EmptyOption EMPTY_OPTION = new EmptyOption(null, null);


    @Override
    protected void composeInputView(RenderingEnv env, InputWidget<UIRadio, RadioGroup> widget) {

        RadioGroup radioGroup = widget.getInputView();
        UIRadio component = widget.getComponent();
        radioGroup.setOrientation(component.getOrientationType());

        // create options
        UIOption[] options = component.getOptions();
        RadioButton button;
        int i = 0;
        for (UIOption option : options) {
            button = new RadioButtonWidget(env.getViewContext(), option);
            button.setText(option.getLabel());
            button.setId(i);
            radioGroup.addView(button);
            i++;
        }
        // set listener to handler option clicks
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                ViewUserActionInterceptor interceptor = env.getUserActionInterceptor();
                if (interceptor != null) {
                    interceptor.doAction(new UserAction(component, ActionType.INPUT_CHANGE.name()));
                }
            }
        });

        ImageView resetButton = ViewHelper.findViewAndSetId(widget, R.id.field_layout_x,
                ImageView.class);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View arg0) {
                // uncheck all options
                for (int index = 0; index < radioGroup.getChildCount(); index++) {
                    RadioButtonWidget option = (RadioButtonWidget) radioGroup.getChildAt(index);
                    option.setChecked(false);
                }
                radioGroup.clearCheck();
            }
        });

    }

    @Override
    protected int getWidgetLayoutId(UIRadio component) {
        return R.layout.widget_radio;
    }

    @Override
    protected void setMessages(RenderingEnv env, InputWidget<UIRadio, RadioGroup> widget) {
        UIInputComponent component = widget.getComponent();
        String message = FormContextHelper.getMessage(env.getFormContext(), component.getId());
        if (message != null) {
            RadioButtonWidget button = (RadioButtonWidget) widget.getInputView().getChildAt(0);
            button.setError(message);
        }
    }

}
