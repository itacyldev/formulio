package es.jcyl.ita.formic.forms.components.autocomplete;
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
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatAutoCompleteTextView;

import org.apache.commons.lang3.StringUtils;
import org.mini2Dx.beanutils.ConvertUtils;
import org.mini2Dx.collections.CollectionUtils;

import java.util.List;

import es.jcyl.ita.formic.core.context.CompositeContext;
import es.jcyl.ita.formic.forms.R;
import es.jcyl.ita.formic.forms.actions.events.Event;
import es.jcyl.ita.formic.forms.actions.events.UserEventInterceptor;
import es.jcyl.ita.formic.forms.components.option.UIOption;
import es.jcyl.ita.formic.forms.components.option.UIOptionsAdapterHelper;
import es.jcyl.ita.formic.forms.components.select.SelectRenderer;
import es.jcyl.ita.formic.forms.context.ContextUtils;
import es.jcyl.ita.formic.forms.context.impl.AndViewContext;
import es.jcyl.ita.formic.forms.el.JexlFormUtils;
import es.jcyl.ita.formic.forms.view.UserMessagesHelper;
import es.jcyl.ita.formic.forms.view.converters.ViewValueConverterFactory;
import es.jcyl.ita.formic.forms.view.render.renderer.RenderingEnv;
import es.jcyl.ita.formic.forms.view.widget.Widget;
import es.jcyl.ita.formic.repo.Entity;
import es.jcyl.ita.formic.repo.Repository;
import es.jcyl.ita.formic.repo.query.Condition;
import es.jcyl.ita.formic.repo.query.Criteria;
import es.jcyl.ita.formic.repo.query.FilterRepoUtils;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
@SuppressLint("AppCompatCustomView")
public class AutoCompleteView extends AppCompatAutoCompleteTextView {
    // TODO:  umm, extract to external class?
    private static final SelectRenderer.EmptyOption EMPTY_OPTION = new SelectRenderer.EmptyOption(null, null);
    private static final ViewValueConverterFactory convFactory = ViewValueConverterFactory.getInstance();

    private UIAutoComplete component;
    private Object value;
    private boolean selectionInProgress = false;

    private Object initValue;

    public AutoCompleteView(Context context) {
        super(context);
    }

    public AutoCompleteView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AutoCompleteView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void load(RenderingEnv env) {
        if (this.component.isStatic()) {
            return;
        }
        // Create local "this" context for current element and link to the Adapter
        CompositeContext ctx = setupThisContext(env);
        ((EntityListELAdapter) this.getAdapter()).load(ctx, value == null);
    }

    private CompositeContext setupThisContext(RenderingEnv env) {
        AndViewContext thisViewCtx = new AndViewContext(this);
        // the user input will be retrieved as text from the view, value is retrieved as raw text
        thisViewCtx.registerViewElement("value", getId(), convFactory.get("text"), String.class);
        thisViewCtx.setPrefix("this");
        CompositeContext ctx = ContextUtils.combine(env.getWidgetContext(), thisViewCtx);
        return ctx;
    }

    public void initialize(RenderingEnv env, Widget<UIAutoComplete> widget, ImageView arrowDropDown) {
        this.component = widget.getComponent();
        ArrayAdapter adapter;
        if (component.isStatic()) {
            // create adapter using UIOptions
            adapter = UIOptionsAdapterHelper.createAdapterFromOptions(env.getAndroidContext(), component.getOptions(),
                    component.hasNullOption(), android.R.layout.select_dialog_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        } else {
            CompositeContext ctx = setupThisContext(env);
            adapter = new EntityListELAdapter(env, ctx, R.layout.widget_autocomplete_listitem,
                    R.id.autocomplete_item, component);
        }
        this.setAdapter(adapter);

        addClickOptionListener(env, widget);
        addTextChangeListener(env, widget);
        addLostFocusListener(env, widget, arrowDropDown);
    }

    private void executeUserAction(RenderingEnv env, Widget<UIAutoComplete> widget) {
        UserEventInterceptor interceptor = env.getUserActionInterceptor();
        if (interceptor != null) {
            interceptor.notify(Event.inputChange(widget));
        }
    }

    private void addClickOptionListener(RenderingEnv env, Widget<UIAutoComplete> widget) {
        this.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectionInProgress = true;
                setSelection(position);
                selectionInProgress = false;
                if (hasValueChanged()) {
                    initValue = value;
                    executeUserAction(env, widget);
                }
            }
        });
    }

    private void addLostFocusListener(RenderingEnv env, Widget<UIAutoComplete> widget, ImageView arrowDropDown) {
        this.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // if current text doesn't match and option, remove if
                TypedArray ta = env.getAndroidContext().obtainStyledAttributes(new int[]{R.attr.onSurfaceColor, R.attr.primaryColor});
                arrowDropDown.setImageTintList(ta.getColorStateList(v.hasFocus() ? 1 : 0));

                if (v.hasFocus()) {
                    showDropDown();
                } else if (StringUtils.isNotBlank(getText())) {
                    if (value == null) {
                        setText(null);
                    }

                    if (hasValueChanged()) {
                        executeUserAction(env, widget);
                    }
                }
            }
        });
    }

    private void addTextChangeListener(final RenderingEnv env, final Widget widget) {
        Handler handler = new Handler(Looper.getMainLooper() /*UI thread*/);

        this.addTextChangedListener(new TextWatcher() {
            Runnable workRunnable = new Runnable() {
                @Override
                public void run() {
                    boolean found = findCurrentSelection();
                    if (!found) {
                        Context ctx = widget.getContext();
                        UserMessagesHelper.toast(ctx, ctx.getString(R.string.not_found), Toast.LENGTH_LONG);
                    }

                    if (hasValueChanged()) {
                        initValue = value;
                        executeUserAction(env, widget);
                    }
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
                if (selectionInProgress) {
                    return;
                }
                if (!env.isInterceptorDisabled()) {
                    if (env.isInputDelayDisabled()) {
                        boolean found = findCurrentSelection();
                        if (!found) {
                            Context ctx = widget.getContext();
                            UserMessagesHelper.toast(ctx, ctx.getString(R.string.not_found), Toast.LENGTH_LONG);
                        }

                        if (hasValueChanged()) {
                            executeUserAction(env, widget);
                        }
                    } else {
                        handler.postDelayed(workRunnable, env.getInputTypingDelay());
                    }
                }
            }
        });
    }

    /**
     * Checks if the value has changed
     *
     * @return
     */
    private boolean hasValueChanged() {
        if (this.initValue != null && !this.initValue.equals(this.value)
                || this.initValue == null && this.value != null) {
            return true;
        }
        return false;
    }

    /**
     * Uses current TextView value to find the related option in the Option adapter.
     * Returns true if an option fits the user input
     */
    private boolean findCurrentSelection() {
        // get current text
        String textFilter = this.getText().toString();
        ArrayAdapter<UIOption> adapter = (ArrayAdapter<UIOption>) getAdapter();
        int nCounts = adapter.getCount();
        UIOption option;
        for (int i = 0; i < nCounts; i++) {
            option = adapter.getItem(i);
            if (option != null && option.getLabel() != null
                    && option.getLabel().equalsIgnoreCase(textFilter)) {
                this.value = option.getValue();
                return true;
            }
        }
        this.value = null;
        return false;
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
        this.initValue = value;

        if (value == null) {
            this.value = null;
            setText(null);
            return;
        }
        // create filter to get the option from the repo
        Repository repo = this.component.getRepo();
        Condition cond = new Condition(this.component.getOptionValueProperty(),
                this.component.getValueFilteringOperator(), value);
        es.jcyl.ita.formic.repo.query.Filter f = FilterRepoUtils.createInstance(repo);
        f.setExpression(Criteria.single(cond));
        List<Entity> lst = repo.find(f);

        if (CollectionUtils.isEmpty(lst)) {
            // option not found in the repository
            this.value = null;
            this.setText(null);
        } else {
            Entity entity = lst.get(0);
            // calculate the label to show in the input
            Object oLabel = JexlFormUtils.eval(entity, component.getOptionLabelExpression());
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
