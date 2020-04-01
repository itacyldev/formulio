package es.jcyl.ita.frmdrd.ui.components.datatable;

import android.content.Context;
import android.provider.Contacts;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.mini2Dx.beanutils.ConvertUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import es.jcyl.ita.crtrepo.Entity;
import es.jcyl.ita.crtrepo.meta.PropertyType;
import es.jcyl.ita.frmdrd.MainController;
import es.jcyl.ita.frmdrd.R;
import es.jcyl.ita.frmdrd.actions.ActionType;
import es.jcyl.ita.frmdrd.actions.UserAction;
import es.jcyl.ita.frmdrd.actions.interceptors.ViewUserActionInterceptor;
import es.jcyl.ita.frmdrd.util.DataUtils;

/*
 * Copyright 2020 Javier Ramos (javier.ramos@itacyl.es), ITACyL (http://www.itacyl.es).
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

/**
 * @author Javier Ramos (javier.ramos@itacyl.es)
 */

public class ListEntityAdapter extends ArrayAdapter<Entity> {
    private Context context;

    private LayoutInflater inflater;
    private View[] cacheViews;

    private final int FIELD_LIMIT;
    private static final int RECORDS_CACHE = 20;

    /**
     * Behaviour attributes
     */
    private UIDatatable dataTable;
    private ViewUserActionInterceptor userActionInterceptor;

    public ListEntityAdapter(final Context context, UIDatatable dataTable,
                             final int textViewResourceId,
                             final List<Entity> entities, int field_limit) {
        super(context, textViewResourceId, entities);
        this.context = context;
        this.dataTable = dataTable;
        this.FIELD_LIMIT = field_limit;
        this.cacheViews = new View[RECORDS_CACHE];
        this.inflater = LayoutInflater.from(getContext());
    }

    @Override
    public View getView(final int position, View convertView,
                        final ViewGroup parent) {

        View item = this.cacheViews[position % cacheViews.length];
        ViewHolder holder;

        final Entity currentEntity = getItem(position);

        if (item == null) {

            item = inflater.inflate(R.layout.list_item, parent, false);

            holder = new ViewHolder();
            holder.position = position;
            holder.charged = false;
            holder.layout =
                    (LinearLayout) item.findViewById(R.id.list_item_layout);
            holder.viewList = new ArrayList<>();

            createViewsLayout(holder, parent, currentEntity);

            item.setTag(holder);
        } else {
            holder = (ViewHolder) item.getTag();
        }

        if (!holder.charged || holder.position != position) {
            setViewsLayout(holder, currentEntity);
            setOnClickListener(holder.layout, currentEntity);
            holder.position = position;
            holder.charged = true;
            cacheViews[position % cacheViews.length] = item;
        }

        return item;
    }

    private void setOnClickListener(LinearLayout layout,
                                    Entity currentEntity) {
        layout.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (userActionInterceptor != null) {
                    UserAction action = new UserAction(context, dataTable, ActionType.NAVIGATE);
                    action.addParam("route", dataTable.getNavigateTo());
                    action.addParam("entityId", (Serializable) currentEntity.getId());
                    userActionInterceptor.doAction(action);
                }
            }
        });
    }


    private void setViewsLayout(final ViewHolder holder, Entity entity) {
        Object value;
        int index = 0;
        for (PropertyType p : entity.getMetadata().getProperties()) {
            value = entity.get(p.getName());
            String stringValue = (String) ConvertUtils.convert(value, String.class);
            TextView textView = (TextView) holder.viewList.get(index);
            textView.setText(DataUtils.nullFormat(stringValue));
            index++;
        }
    }

    private void createViewsLayout(final ViewHolder holder,
                                   final ViewGroup parent, Entity entity) {

        LinearLayout layout = holder.layout;

        Object value;
        int index = 0;
        for (PropertyType p : entity.getMetadata().getProperties()) {
            value = entity.get(p.getName());
            View view = createTextView(parent);
            layout.addView(view);
            holder.viewList.add(view);

            index++;
            if (index >= FIELD_LIMIT) {
                final TextView overflowTextView = createTextView(parent);
                layout.addView(overflowTextView);
                holder.viewList.add(view);
                break;
            }
        }

    }

    private TextView createTextView(final ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        TextView output = (TextView) inflater.inflate(
                R.layout.list_item_textview, parent, false);
        return output;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        for (View item : cacheViews) {
            ViewHolder holder = (ViewHolder) item.getTag();
            holder.charged = false;
        }
    }


    public void setUserActionInterceptor(ViewUserActionInterceptor userActionInterceptor) {
        this.userActionInterceptor = userActionInterceptor;
    }

    static class ViewHolder {
        int position;
        boolean charged;
        LinearLayout layout;
        List<View> viewList;
    }

}
