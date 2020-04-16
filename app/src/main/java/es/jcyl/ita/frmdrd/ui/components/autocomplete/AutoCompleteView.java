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
import android.util.AttributeSet;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

import es.jcyl.ita.crtrepo.Entity;
import es.jcyl.ita.crtrepo.Repository;
import es.jcyl.ita.crtrepo.context.CompositeContext;
import es.jcyl.ita.crtrepo.query.Filter;
import es.jcyl.ita.frmdrd.R;
import es.jcyl.ita.frmdrd.repo.query.FilterHelper;
import es.jcyl.ita.frmdrd.ui.components.DynamicComponent;
import es.jcyl.ita.frmdrd.view.render.RenderingEnv;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class AutoCompleteView extends androidx.appcompat.widget.AppCompatAutoCompleteTextView
        implements DynamicComponent {
    private UIAutoComplete component;
    private Repository repo;
    private List<Entity> entities = new ArrayList<>();
    private Filter filter;
    private int pageSize;
    private int offset;
    private BaseAdapter adapter;


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
        // set filter to repo using current view data
        this.entities.clear();
        this.pageSize = 100;
        this.offset = 0;
        this.filter = setupFilter(env.getContext());
        // read first page to render data
        loadData();
         adapter = new ListEntityAdapter(this.getContext(), this,
                R.layout.list_item, entities);


    }
    private void loadData() {
        this.filter.setPageSize(this.pageSize);
        this.filter.setOffset(this.offset);
        //this.entities.clear();
        this.entities.addAll(this.repo.find(this.filter));

        //notify that the model changedA
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }

        this.offset += this.pageSize;
    }
    /**
     * Get the definition filter from the dataTable and construct an effective filter using the
     * context information.
     *
     * @param context
     * @return
     */
    private Filter setupFilter(CompositeContext context) {
        Filter defFilter = this.component.getFilter();
        Filter f = FilterHelper.createInstance(repo);
        if (defFilter != null) {
            FilterHelper.evaluateFilter(context, defFilter, f);
        }
        f.setOffset(this.offset);
        f.setPageSize(this.pageSize);
        return f;
    }
    /**********************************/

    public UIAutoComplete getComponent() {
        return component;
    }

    public void setComponent(UIAutoComplete component) {
        this.component = component;
    }

    public Repository getRepo() {
        return repo;
    }

    public void setRepo(Repository repo) {
        this.repo = repo;
    }
}
