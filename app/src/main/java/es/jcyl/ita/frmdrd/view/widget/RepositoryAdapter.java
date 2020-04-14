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
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;

import java.util.List;

import es.jcyl.ita.crtrepo.Entity;
import es.jcyl.ita.crtrepo.Repository;
import es.jcyl.ita.crtrepo.query.Filter;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class RepositoryAdapter extends ArrayAdapter<Entity> {

    private Filter filter;
    private Repository repo;
    private List<Entity> store;
    private RepoListFilter listFilter;
    private es.jcyl.ita.crtrepo.context.Context globalContext;

    public RepositoryAdapter(@NonNull Context context, int resource, @NonNull List<Entity> store,
                             Repository repo, Filter filter, RepoListFilter listFilter) {
        super(context, resource, store);
        this.repo = repo;
        this.filter = filter;
        this.store = store;
        this.listFilter = listFilter;
    }
    public void setGlobalContext(es.jcyl.ita.crtrepo.context.Context context){
        this.globalContext = context;
    }

    @NonNull
    @Override
    public android.widget.Filter getFilter() {
        return listFilter;
    }

    public class RepoListFilter extends android.widget.Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            return null;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {

        }
    }
}
