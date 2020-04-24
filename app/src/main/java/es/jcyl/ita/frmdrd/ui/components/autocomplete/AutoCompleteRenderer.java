package es.jcyl.ita.frmdrd.ui.components.autocomplete;
/*
 * Copyright 2020 Gustavo Río (gustavo.rio@itacyl.es), ITACyL (http://www.itacyl.es).
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

import android.view.View;
import android.widget.AdapterView;

import es.jcyl.ita.frmdrd.R;
import es.jcyl.ita.frmdrd.actions.ActionType;
import es.jcyl.ita.frmdrd.actions.UserAction;
import es.jcyl.ita.frmdrd.actions.interceptors.ViewUserActionInterceptor;
import es.jcyl.ita.frmdrd.context.FormContextHelper;
import es.jcyl.ita.frmdrd.context.impl.AndViewContext;
import es.jcyl.ita.frmdrd.ui.components.select.SelectRenderer;
import es.jcyl.ita.frmdrd.view.InputFieldView;
import es.jcyl.ita.frmdrd.view.render.InputRenderer;
import es.jcyl.ita.frmdrd.view.render.RenderingEnv;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 * <p>
 * Creates view elements for autocomplete component
 */
public class AutoCompleteRenderer extends InputRenderer<AutoCompleteView, UIAutoComplete> {

    @Override
    protected int getComponentLayout() {
        return R.layout.component_autocomplete;
    }

    @Override
    protected void composeView(RenderingEnv env, InputFieldView<AutoCompleteView> baseView,
                               UIAutoComplete component) {
        AutoCompleteView input = baseView.getInputView();
        input.initialize(env, component);
        input.load(env);
    }

    @Override
    protected void setMessages(RenderingEnv env, InputFieldView<AutoCompleteView> baseView,
                               UIAutoComplete component) {
        String message = FormContextHelper.getMessage(env.getFormContext(), component.getId());
        if (message != null) {
            baseView.getInputView().setError(message);
        }
    }

}
