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
import android.util.AttributeSet;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import java.util.ArrayList;
import java.util.List;

import es.jcyl.ita.crtrepo.Entity;
import es.jcyl.ita.crtrepo.Repository;
import es.jcyl.ita.crtrepo.context.CompositeContext;
import es.jcyl.ita.crtrepo.query.Filter;
import es.jcyl.ita.frmdrd.R;
import es.jcyl.ita.frmdrd.repo.query.FilterHelper;
import es.jcyl.ita.frmdrd.ui.components.DynamicComponent;
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
    private List<Entity> entities = new ArrayList<>();
    private Filter filter;
    private int pageSize = 100;
    private int offset = 0;


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
        // set filter to repo using current view data

        this.filter = setupFilter(env.getContext());
        // read first page to render data
        loadData();
    }

    private void loadData() {
        Repository repo = this.component.getRepo();
        this.filter.setPageSize(this.pageSize);
        this.filter.setOffset(this.offset);
        this.entities.clear();
        this.entities.addAll(repo.find(this.filter));

        //notify that the model changedA
//        if (this.getAdapter() != null) {
//            ((ArrayAdapter) getAdapter()).notifyDataSetChanged();
//        }

    }

    /**
     * Get the definition filter from the dataTable and construct an effective filter using the
     * context information.
     *
     * @param context
     * @return
     */
    private Filter setupFilter(CompositeContext context) {
        Repository repo = this.component.getRepo();
        Filter defFilter = this.component.getFilter();
        Filter f = FilterHelper.createInstance(repo);
        if (defFilter != null) {
            FilterHelper.evaluateFilter(context, defFilter, f);
        }
        f.setOffset(this.offset);
        f.setPageSize(this.pageSize);
        return f;
    }

    public void initialize(RenderingEnv env, UIAutoComplete component) {
        this.component = component;
        ArrayAdapter adapter;
        if (component.isStatic()) {
            // create adapter using UIOptions
            adapter = createStaticArrayAdapter(env, component);
        } else {
            this.offset = 0;
            this.pageSize = 100;
            // get options querying the repository
            this.entities = new ArrayList<>();
            adapter = new EntityListELAdapter(env, R.layout.component_autocomplete_listitem,
                    R.id.autocomplete_item, component);
//            adapter = new ListEntityAdapter(this.getContext(), this,
//                    R.layout.list_item, entities);
        }

        this.setAdapter(adapter);
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
}
