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

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import java.util.ArrayList;
import java.util.List;

import es.jcyl.ita.crtrepo.context.CompositeContext;
import es.jcyl.ita.frmdrd.R;
import es.jcyl.ita.frmdrd.actions.ActionType;
import es.jcyl.ita.frmdrd.actions.UserAction;
import es.jcyl.ita.frmdrd.actions.interceptors.ViewUserActionInterceptor;
import es.jcyl.ita.frmdrd.context.ContextUtils;
import es.jcyl.ita.frmdrd.context.impl.AndViewContext;
import es.jcyl.ita.frmdrd.ui.components.DynamicComponent;
import es.jcyl.ita.frmdrd.ui.components.UIComponent;
import es.jcyl.ita.frmdrd.ui.components.select.UIOption;
import es.jcyl.ita.frmdrd.view.render.RenderingEnv;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
@SuppressLint("AppCompatCustomView")
public class AutoCompleteView extends AutoCompleteTextView
        implements DynamicComponent {
    private static final EmptyOption EMPTY_OPTION = new EmptyOption(null, null);

    private UIAutoComplete component;
    private int selection = -1;

    public AutoCompleteView(Context context) {
        super(context);
    }

    public AutoCompleteView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AutoCompleteView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void load(RenderingEnv env) {
        if (this.component.isStatic()) {
            return;
        }
        // Create local "this" context for current element and link to the Adapter
        CompositeContext ctx = setupThisContext(env);
        ((EntityListELAdapter) this.getAdapter()).load(ctx);
    }

    private CompositeContext setupThisContext(RenderingEnv env) {
        AndViewContext thisViewCtx = new AndViewContext(this);
        // the user input will be retrieved as text from the view
        thisViewCtx.registerViewElement("value", getId(), component.getConverter(), String.class);
        thisViewCtx.setPrefix("this");
        CompositeContext ctx = ContextUtils.combine(env.getContext(), thisViewCtx);
        return ctx;
    }

    public void initialize(RenderingEnv env, UIAutoComplete component) {
        this.component = component;
        ArrayAdapter adapter;
        if (component.isStatic()) {
            // create adapter using UIOptions
            adapter = createStaticArrayAdapter(env, component);
        } else {
            adapter = new EntityListELAdapter(env, R.layout.component_autocomplete_listitem,
                    R.id.autocomplete_item, component);
        }
        this.setAdapter(adapter);

        addClickOptionListener(env, component);
        addTextChangeListener(env, component);
    }

    private void executeUserAction(RenderingEnv env, UIComponent component) {
        ViewUserActionInterceptor interceptor = env.getUserActionInterceptor();
        if (interceptor != null) {
            interceptor.doAction(new UserAction(component, ActionType.INPUT_CHANGE.name()));
        }
    }

    private void addClickOptionListener(RenderingEnv env, UIAutoComplete component) {
        this.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                setSelection(position);
                executeUserAction(env, component);
            }
        });
    }

    private void addTextChangeListener(RenderingEnv env, UIAutoComplete component) {
        Handler handler = new Handler(Looper.getMainLooper() /*UI thread*/);

        this.addTextChangedListener(new TextWatcher() {
            Runnable workRunnable = new Runnable() {
                @Override
                public void run() {
                    executeUserAction(env, component);
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
                        executeUserAction(env, component);
                    } else {
                        handler.postDelayed(workRunnable, env.getInputTypingDelay());
                    }
                }
            }
        });
    }


    private ArrayAdapter createStaticArrayAdapter(RenderingEnv env, UIAutoComplete component) {
        // create items from options
        List<UIOption> items = new ArrayList<UIOption>();
        // empty value option
        items.add(EMPTY_OPTION);
        if (component.getOptions() != null) {
            for (UIOption option : component.getOptions()) {
                items.add(option);
            }
        }
//        input.setThreshold(0);
        // setup adapter and event handler
        ArrayAdapter<UIOption> arrayAdapter = new ArrayAdapter<UIOption>(env.getViewContext(),
                android.R.layout.select_dialog_item, items);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        return arrayAdapter;
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

    public void setSelection(int optionIdx) {
        this.selection = optionIdx;
        if (optionIdx == -1) {
            this.setText(null);
        } else {
            UIOption[] options = component.getOptions();
            this.setText(options[optionIdx].getLabel());
        }
    }

    public int getSelection() {
        return selection;
    }

    public String getValue() {
        if (!component.isForceSelection()) {
            return this.getText().toString();
        }
        if (selection == -1) {
            return null;
        } else {
            return this.component.getOptions()[this.selection].getValue();
        }
    }

    public UIOption[] getOptions() {
        return component.getOptions();
    }
}
