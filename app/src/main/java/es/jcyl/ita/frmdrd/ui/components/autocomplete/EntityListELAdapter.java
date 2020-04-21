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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.mini2Dx.beanutils.ConvertUtils;
import org.mini2Dx.collections.CollectionUtils;
import org.mini2Dx.collections.ListUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import es.jcyl.ita.crtrepo.Entity;
import es.jcyl.ita.crtrepo.Repository;
import es.jcyl.ita.crtrepo.context.CompositeContext;
import es.jcyl.ita.crtrepo.query.Condition;
import es.jcyl.ita.crtrepo.query.Criteria;
import es.jcyl.ita.frmdrd.el.JexlUtils;
import es.jcyl.ita.frmdrd.el.ValueBindingExpression;
import es.jcyl.ita.frmdrd.repo.query.FilterHelper;
import es.jcyl.ita.frmdrd.ui.components.select.UIOption;
import es.jcyl.ita.frmdrd.view.render.RenderingEnv;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class EntityListELAdapter extends ArrayAdapter<UIOption> {

    private static final String THIS_VALUE = "${this.value}";

    private final List<Entity> dataList;
    private final Context mContext;
    private final int itemLayout;
    private final int itemId;
    private CompositeContext globalContext;
    private final UIAutoComplete component;
    private Filter listFilter = new ListFilter();
    private es.jcyl.ita.crtrepo.query.Filter defFilter;

    public EntityListELAdapter(RenderingEnv env, int resource, int textViewId, UIAutoComplete component) {
        super(env.getViewContext(), resource);
        dataList = new ArrayList<>();
        mContext = env.getViewContext();
        itemLayout = resource;
        this.itemId = textViewId;
        this.component = component;
        defFilter = component.getFilter();
        if (defFilter == null) {
            // create defFilter
            defFilter = createDefaultDefinitionFilter();
        }
    }

    public void load(CompositeContext context) {
        this.globalContext = context;
        dataList.clear();
        es.jcyl.ita.crtrepo.query.Filter filter = setupFilter(context);
        dataList.addAll(component.getRepo().find(filter));
    }

    /**
     * Get the definition filter from the dataTable and construct an effective filter using the
     * context information.
     *
     * @param context
     * @return
     */
    private es.jcyl.ita.crtrepo.query.Filter setupFilter(CompositeContext context) {
        Repository repo = component.getRepo();
        es.jcyl.ita.crtrepo.query.Filter f = FilterHelper.createInstance(repo);

        if (defFilter != null) {
            FilterHelper.evaluateFilter(context, defFilter, f);
        }
        f.setOffset(0);
        f.setPageSize(100);
        return f;
    }

    private es.jcyl.ita.crtrepo.query.Filter createDefaultDefinitionFilter() {
        es.jcyl.ita.crtrepo.query.Filter f = FilterHelper.createInstance(component.getRepo());
        // add default criteria using autocomplete view input
        Criteria criteria = FilterHelper.singleCriteria(component.getLabelFilteringProperty(), THIS_VALUE);
        f.setCriteria(criteria);
        return f;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public UIOption getItem(int position) {
        return new EntityToUIOptionWrapper(dataList.get(position));
    }

    @Override
    public View getView(int position, View view, @NonNull ViewGroup parent) {

        if (view == null) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(itemLayout, parent, false);
        }
        // evaluate expression to set view value
        UIOption option = this.getItem(position);
        TextView textView = view.findViewById(this.itemId);
        textView.setText((String) ConvertUtils.convert(option.getLabel(), String.class));
        return view;
    }

    @Override
    public Filter getFilter() {
        return listFilter;
    }


    public class ListFilter extends Filter {
        private Object lock = new Object();

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            if (constraint != null) {
                load(globalContext);

                FilterResults filterResults = new FilterResults();
                filterResults.values = dataList;
                filterResults.count = dataList.size();
                return filterResults;
            } else {
                return new FilterResults();
            }
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
//            addAll(dataList);
            notifyDataSetChanged();
        }

    }
    
    class EntityToUIOptionWrapper extends UIOption {

        private Entity entity;
        private String cachedValue;
        private String cachedLabel;

        public EntityToUIOptionWrapper(String label, String value) {
            super(label, value);
        }

        public EntityToUIOptionWrapper(Entity entity) {
            super("","");
            this.entity = entity;
        }

        @Override
        public String getValue() {
            if(cachedValue == null){
                Object value = JexlUtils.eval(entity, component.getOptionValueExpression());
                cachedValue = (String) ConvertUtils.convert(value, String.class);
            }
            return cachedValue;
        }

        @Override
        public String getLabel() {
            if(cachedLabel == null){
                Object label = JexlUtils.eval(entity, component.getOptionLabelExpression());
                cachedLabel =(String) ConvertUtils.convert(label, String.class);
            }
            return cachedLabel;
        }
    }

}
