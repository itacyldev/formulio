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

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import java.util.ArrayList;
import java.util.List;

import es.jcyl.ita.crtrepo.Entity;
import es.jcyl.ita.frmdrd.R;
import es.jcyl.ita.frmdrd.actions.ActionType;
import es.jcyl.ita.frmdrd.actions.UserAction;
import es.jcyl.ita.frmdrd.actions.interceptors.ViewUserActionInterceptor;
import es.jcyl.ita.frmdrd.ui.components.select.SelectRenderer;
import es.jcyl.ita.frmdrd.ui.components.select.UIOption;
import es.jcyl.ita.frmdrd.view.InputFieldView;
import es.jcyl.ita.frmdrd.view.render.InputRenderer;
import es.jcyl.ita.frmdrd.view.render.RenderingEnv;
import es.jcyl.ita.frmdrd.view.widget.RepositoryAdapter;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 * <p>
 * Creates view elements for autocomplete component
 */
public class AutoCompleteRenderer extends InputRenderer<AutoCompleteView, UIAutoComplete> {

    private static final SelectRenderer.EmptyOption EMPTY_OPTION = new SelectRenderer.EmptyOption(null, null);

    private List<Entity> entities = new ArrayList<>();


    @Override
    protected int getComponentLayout() {
        return R.layout.component_autocomplete;
    }

//    protected InputFieldView createInputFieldView(Context viewContext, View baseView, UIAutoComplete component) {
//        InputFieldView baseView = super.createInputFieldView(viewContext, baseView, component);
//    }
    @Override
    protected void composeView(RenderingEnv env, InputFieldView<AutoCompleteView> baseView,
                               UIAutoComplete component) {
        AutoCompleteView input = baseView.getInputView();
        ArrayAdapter<UIOption> arrayAdapter;
        if (component.getRepo() != null) {
            arrayAdapter = createDynamicArrayAdapter(env, component, input);
        } else {
            arrayAdapter = createStaticArrayAdapter(env, component, input);
        }

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

        input.load(env);

    }

    private ArrayAdapter createDynamicArrayAdapter(RenderingEnv env, UIAutoComplete component, AutoCompleteTextView input) {
        RepositoryAdapter adapter = new RepositoryAdapter(env.getViewContext(),
                android.R.layout.select_dialog_item, entities, component);
        return adapter;
    }

    private ArrayAdapter createStaticArrayAdapter(RenderingEnv env, UIAutoComplete component, AutoCompleteTextView input) {
        // create items from options
        List<UIOption> items = new ArrayList<UIOption>();
        // empty value option
        items.add(EMPTY_OPTION);
        if (component.getOptions() != null) {
            for (UIOption option : component.getOptions()) {
                items.add(option);
            }
        }
        input.setThreshold(0);
        // setup adapter and event handler
        ArrayAdapter<UIOption> arrayAdapter = new ArrayAdapter<UIOption>(env.getViewContext(),
                android.R.layout.select_dialog_item, items);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        return arrayAdapter;
    }

    @Override
    protected void setMessages(RenderingEnv env, InputFieldView<AutoCompleteView> baseView,
                               UIAutoComplete component) {

    }


}
