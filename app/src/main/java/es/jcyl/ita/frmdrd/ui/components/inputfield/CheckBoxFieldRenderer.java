package es.jcyl.ita.frmdrd.ui.components.inputfield;

import android.widget.CompoundButton;
import android.widget.Switch;

import es.jcyl.ita.frmdrd.R;
import es.jcyl.ita.frmdrd.actions.ActionType;
import es.jcyl.ita.frmdrd.actions.UserAction;
import es.jcyl.ita.frmdrd.actions.interceptors.ViewUserActionInterceptor;
import es.jcyl.ita.frmdrd.ui.components.UIField;
import es.jcyl.ita.frmdrd.view.InputFieldView;
import es.jcyl.ita.frmdrd.view.render.InputRenderer;
import es.jcyl.ita.frmdrd.view.render.RenderingEnv;

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

public class CheckBoxFieldRenderer extends InputRenderer<Switch, UIField> {

    @Override
    protected void composeView(RenderingEnv env, InputFieldView<Switch> baseView, UIField component) {
        baseView.getInputView().setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton,
                                         boolean value) {
                ViewUserActionInterceptor interceptor = env.getUserActionInterceptor();
                if (interceptor != null) {
                    interceptor.doAction(new UserAction(component, ActionType.INPUT_CHANGE.name()));
                }
            }
        });
    }

    @Override
    protected int getComponentLayout() {
        return R.layout.component_checkbox;
    }

    @Override
    protected void setMessages(RenderingEnv env, InputFieldView<Switch> baseView, UIField component) {

    }

    @Override
    protected <T> T handleNullValue(UIField component) {
        return (T) Boolean.FALSE;
    }

}
