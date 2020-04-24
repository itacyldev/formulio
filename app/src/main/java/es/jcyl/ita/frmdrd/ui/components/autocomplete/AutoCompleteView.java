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

import org.mini2Dx.beanutils.ConvertUtils;
import org.mini2Dx.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

import es.jcyl.ita.crtrepo.Entity;
import es.jcyl.ita.crtrepo.Repository;
import es.jcyl.ita.crtrepo.context.CompositeContext;
import es.jcyl.ita.crtrepo.query.Condition;
import es.jcyl.ita.crtrepo.query.Criteria;
import es.jcyl.ita.frmdrd.R;
import es.jcyl.ita.frmdrd.actions.ActionType;
import es.jcyl.ita.frmdrd.actions.UserAction;
import es.jcyl.ita.frmdrd.actions.interceptors.ViewUserActionInterceptor;
import es.jcyl.ita.frmdrd.context.ContextUtils;
import es.jcyl.ita.frmdrd.context.impl.AndViewContext;
import es.jcyl.ita.frmdrd.el.JexlUtils;
import es.jcyl.ita.frmdrd.repo.query.FilterHelper;
import es.jcyl.ita.frmdrd.ui.components.DynamicComponent;
import es.jcyl.ita.frmdrd.ui.components.UIComponent;
import es.jcyl.ita.frmdrd.ui.components.select.SelectRenderer;
import es.jcyl.ita.frmdrd.ui.components.option.UIOption;
import es.jcyl.ita.frmdrd.view.converters.ViewValueConverterFactory;
import es.jcyl.ita.frmdrd.view.render.RenderingEnv;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
@SuppressLint("AppCompatCustomView")
public class AutoCompleteView extends AutoCompleteTextView
        implements DynamicComponent {
    // TODO:  umm, extract to external class?
    private static final SelectRenderer.EmptyOption EMPTY_OPTION = new SelectRenderer.EmptyOption(null, null);
    private static final ViewValueConverterFactory convFactory = ViewValueConverterFactory.getInstance();

    private UIAutoComplete component;
    private Object value;

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
        // the user input will be retrieved as text from the view, value is retrieved as raw text
        thisViewCtx.registerViewElement("value", getId(), convFactory.get("text"), String.class);
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

    public boolean hasNullOption() {
        return this.component.isHasNullOption();
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
                        findCurrentSelection();
                        executeUserAction(env, component);
                    } else {
                        handler.postDelayed(workRunnable, env.getInputTypingDelay());
                    }
                }
            }
        });
    }

    /**
     * Uses current TextView value to find the related option in the Option adapter
     */
    private void findCurrentSelection() {
        // get current text
        String textFilter = this.getText().toString();
        ArrayAdapter<UIOption> adapter = (ArrayAdapter<UIOption>) getAdapter();
        int nCounts = adapter.getCount();
        UIOption option;
        for (int i = 0; i < nCounts; i++) {
            option = adapter.getItem(i);
            if (option.getLabel().equalsIgnoreCase(textFilter)) {
                this.value = option.getValue();
                break;
            }
        }
    }


    private ArrayAdapter createStaticArrayAdapter(RenderingEnv env, UIAutoComplete component) {
        // create items from options
        List<UIOption> items = new ArrayList<UIOption>();
        // empty value option
        if (hasNullOption()) {
            items.add(EMPTY_OPTION);
        }
        if (component.getOptions() != null) {
            for (UIOption option : component.getOptions()) {
                items.add(option);
            }
        }
        this.setThreshold(component.getInputThreshold());
        // setup adapter and event handler
        ArrayAdapter<UIOption> arrayAdapter = new ArrayAdapter<UIOption>(env.getViewContext(),
                android.R.layout.select_dialog_item, items);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        return arrayAdapter;
    }


    public void setSelection(int position) {
        if (position == -1) {
            this.value = null;
            this.setText(null);
        } else {
            UIOption option = (UIOption) this.getAdapter().getItem(position);
            this.value = option.getValue();
            this.setText(option.getLabel());
        }
    }

    public Object getValue() {
        if (!component.isForceSelection()) {
            return this.getText().toString();
        }
        return this.value;
    }

    public void setValue(Object value) {
        if (value == null) {
            this.value = null;
            setText(null);
            return;
        }
        // create filter to get the option from the repo
        Repository repo = this.component.getRepo();
        Condition cond = new Condition(this.component.getValueFilteringProperty(),
                this.component.getValueFilteringOperator(), value);
        es.jcyl.ita.crtrepo.query.Filter f = FilterHelper.createInstance(repo);
        f.setCriteria(Criteria.single(cond));
        List<Entity> lst = repo.find(f);

        if (CollectionUtils.isEmpty(lst)) {
            // option not found in the repository
            this.value = null;
            this.setText(null);
        } else {
            Entity entity = lst.get(0);
            // calculate the label to show in the input
            Object oLabel = JexlUtils.eval(entity, component.getOptionLabelExpression());
            if (oLabel != null) {
                this.value = value;
                String label = (String) ConvertUtils.convert(oLabel, String.class);
                this.setText(label);
            }
        }
    }

    public UIOption[] getOptions() {
        return component.getOptions();
    }


}
