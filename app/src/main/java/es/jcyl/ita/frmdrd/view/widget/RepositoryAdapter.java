package es.jcyl.ita.frmdrd.view.widget;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import es.jcyl.ita.crtrepo.Entity;
import es.jcyl.ita.crtrepo.Repository;
import es.jcyl.ita.crtrepo.query.Filter;
import es.jcyl.ita.frmdrd.R;
import es.jcyl.ita.frmdrd.repo.query.FilterHelper;
import es.jcyl.ita.frmdrd.ui.components.autocomplete.UIAutoComplete;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class RepositoryAdapter extends ArrayAdapter<Entity> {

    private final int layoutResource;
    private Filter defFilter;
    private Repository repo;
    private List<Entity> store;
    private RepoListFilter listFilter;
    private es.jcyl.ita.crtrepo.context.Context globalContext;

    public RepositoryAdapter(@NonNull Context context, int resource, @NonNull List<Entity> store,
                             UIAutoComplete component) {
        super(context, resource, store);
        this.repo = component.getRepo();
        this.defFilter = component.getFilter();
        this.store = store;
        this.layoutResource = resource;
        this.listFilter = new RepoListFilter();
    }

    private Filter setupFilter(es.jcyl.ita.crtrepo.context.Context context) {
        Filter f = FilterHelper.createInstance(repo);
        if (defFilter != null) {
            FilterHelper.evaluateFilter(context, defFilter, f);
        }
        f.setOffset(0);
        f.setPageSize(200);
        return f;
    }

    @Override
    public int getCount() {
        return store.size();
    }

    @Override
    public Entity getItem(int position) {
        return store.get(position);
    }

    @Override
    public View getView(int position, View view, @NonNull ViewGroup parent) {

        if (view == null) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(layoutResource, parent, false);
        }
//
//        Entity entity = getItem(position);
//        TextView strName = view.findViewById(R.id.item_label);
//        strName.setText(entity.getId().toString());
//
//        TextView couponCount = view.findViewById(R.id.item_value);
//        couponCount.setText(entity.getId().toString());

        return view;
    }

    @NonNull
    @Override
    public android.widget.Filter getFilter() {
        return listFilter;
    }

    public class RepoListFilter extends android.widget.Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            // combine global context with current entity value

            Filter filter = setupFilter(globalContext);
            List<Entity> entities = repo.find(filter);
            results.values = entities;
            results.count = (int) repo.count(filter);

            return results;

        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if (results.values != null) {
                store = (ArrayList<Entity>) results.values;
            } else {
                store = null;
            }
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }
    }
}
