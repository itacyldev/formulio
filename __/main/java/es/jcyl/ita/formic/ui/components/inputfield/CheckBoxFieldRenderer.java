package es.jcyl.ita.formic.forms.components.inputfield;

import android.widget.CompoundButton;
import android.widget.Switch;

import es.jcyl.ita.formic.R;
import es.jcyl.ita.formic.forms.actions.ActionType;
import es.jcyl.ita.formic.forms.actions.UserAction;
import es.jcyl.ita.formic.forms.actions.interceptors.ViewUserActionInterceptor;
import es.jcyl.ita.formic.forms.view.widget.InputWidget;
import es.jcyl.ita.formic.forms.view.render.InputTextRenderer;
import es.jcyl.ita.formic.forms.view.render.RenderingEnv;

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

public class CheckBoxFieldRenderer extends InputTextRenderer<UIField, Switch> {

    @Override
    protected void composeInputView(RenderingEnv env, InputWidget<UIField, Switch> widget) {
        Switch inputView = widget.getInputView();
        inputView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton,
                                         boolean value) {
                ViewUserActionInterceptor interceptor = env.getUserActionInterceptor();
                if (interceptor != null) {
                    interceptor.doAction(new UserAction(widget.getComponent(),
                            ActionType.INPUT_CHANGE.name()));
                }
            }
        });
    }

    @Override
    protected int getWidgetLayoutId() {
        return R.layout.widget_checkbox;
    }

    @Override
    protected <T> T handleNullValue(UIField component) {
        return (T) Boolean.FALSE;
    }


}
