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

import org.mini2Dx.beanutils.ConvertUtils;

import java.util.ArrayList;
import java.util.List;

import es.jcyl.ita.crtrepo.Entity;
import es.jcyl.ita.crtrepo.Repository;
import es.jcyl.ita.crtrepo.context.CompositeContext;
import es.jcyl.ita.crtrepo.context.impl.BasicContext;
import es.jcyl.ita.crtrepo.query.Criteria;
import es.jcyl.ita.frmdrd.R;
import es.jcyl.ita.frmdrd.context.ContextUtils;
import es.jcyl.ita.frmdrd.el.JexlUtils;
import es.jcyl.ita.frmdrd.repo.query.FilterHelper;
import es.jcyl.ita.frmdrd.view.render.RenderingEnv;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class EntityListELAdapter extends ArrayAdapter {

    private static final String THIS_VALUE = "${this.value}";
    private final List<Entity> dataList;
    private final Context mContext;
    private final int itemLayout;
    private final int itemId;
    private final CompositeContext globalContext;
    private final UIAutoComplete component;
    private Filter listFilter = new ListFilter();
    private es.jcyl.ita.crtrepo.query.Filter defFilter;

    public EntityListELAdapter(RenderingEnv env, int resource, int textViewId, UIAutoComplete component) {
        super(env.getViewContext(), resource);
        dataList = new ArrayList<>();

        mContext = env.getViewContext();
        itemLayout = R.layout.component_autocomplete_listitem;
        this.itemId = R.id.autocomplete_item; //android.R.layout.simple_spinner_dropdown_item
        this.component = component;
//        Context thisViewContext = createViewInputContext();
        this.globalContext = env.getContext(); //ContextUtils.combine(env.getContext(), thisViewContext);
        defFilter = component.getFilter();
        if (defFilter == null) {
            // create defFilter
            defFilter = createDefaultDefinitionFilter();
        }
        loadOptions(null);
    }

    private void loadOptions(CharSequence constraint) {
        dataList.clear();
        es.jcyl.ita.crtrepo.context.CompositeContext  ctx = globalContext;
        if(constraint !=null){
            es.jcyl.ita.crtrepo.context.Context viewCtx = new BasicContext("this");
            viewCtx.put("value", constraint );
            ctx = ContextUtils.combine(ctx, viewCtx);
        }

        es.jcyl.ita.crtrepo.query.Filter filter = setupFilter(ctx);
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
        Criteria criteria = FilterHelper.singleCriteria(component.getOptionFilteringProperty(), THIS_VALUE);
        f.setCriteria(criteria);
        return f;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Entity getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public View getView(int position, View view, @NonNull ViewGroup parent) {

        if (view == null) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(itemLayout, parent, false);
        }
        // evaluate expression to set view value
        Entity entity = getItem(position);
        Object value = JexlUtils.eval(entity, component.getOptionLabelExpression());

        TextView textView = view.findViewById(this.itemId);
        textView.setText((String) ConvertUtils.convert(value, String.class));
        return view;
    }

    @Override
    public Filter getFilter() {
        return listFilter;
    }


    public class ListFilter extends Filter {
        private Object lock = new Object();
//
//        @Override
//        public CharSequence convertResultToString(Object resultValue) {
//            String str = ((People) resultValue).getName();
//            return str;
//        }




        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            if (constraint != null) {
                loadOptions(constraint);


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
            addAll(dataList);
            notifyDataSetChanged();
        }

    }

}
