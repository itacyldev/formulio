package es.jcyl.ita.frmdrd.ui.components.select;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import es.jcyl.ita.frmdrd.R;
import es.jcyl.ita.frmdrd.actions.ActionType;
import es.jcyl.ita.frmdrd.actions.UserAction;
import es.jcyl.ita.frmdrd.actions.interceptors.ViewUserActionInterceptor;
import es.jcyl.ita.frmdrd.ui.components.option.UIOption;
import es.jcyl.ita.frmdrd.ui.components.option.UIOptionsAdapterHelper;
import es.jcyl.ita.frmdrd.view.InputFieldView;
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

public class SelectRenderer extends InputRenderer<Spinner, UISelect> {
    private static final EmptyOption EMPTY_OPTION = new EmptyOption(null, null);

    @Override
    protected int getComponentLayoutId() {
        return R.layout.component_select;
    }

    @Override
    protected void composeView(RenderingEnv env, InputFieldView<Spinner> baseView, UISelect component) {
        Spinner input = baseView.getInputView();

        // setup adapter and event handler
        ArrayAdapter<UIOption> arrayAdapter = UIOptionsAdapterHelper.createAdapterFromOptions(env.getViewContext(),
                component.getOptions(), component.hasNullOption(), android.R.layout.simple_spinner_item);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        input.setAdapter(arrayAdapter);
        input.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // notify action
                ViewUserActionInterceptor interceptor = env.getUserActionInterceptor();
                if (interceptor != null) {
                    interceptor.doAction(new UserAction(component, ActionType.INPUT_CHANGE.name()));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    @Override
    protected void setMessages(RenderingEnv env, InputFieldView<Spinner> baseView, UISelect component) {

    }

    public static class EmptyOption extends UIOption {
        public EmptyOption(String label, String value) {
            super(label, value);
        }

        @Override
        public String toString() {
            return " ";
        }
    }

}
