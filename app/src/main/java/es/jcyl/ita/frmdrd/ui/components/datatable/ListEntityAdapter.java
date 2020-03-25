package es.jcyl.ita.frmdrd.ui.components.datatable;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.jcyl.ita.crtrepo.Entity;
import es.jcyl.ita.crtrepo.meta.PropertyType;
import es.jcyl.ita.frmdrd.NavigationManager;
import es.jcyl.ita.frmdrd.R;
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

    public ListEntityAdapter(final Context context,
                             final int textViewResourceId,
                             final List<Entity> entities, int field_limit) {
        super(context, textViewResourceId, entities);
        this.context = context;
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
                NavigationManager navigationManager = new NavigationManager();
                Map<String, Serializable> params = new HashMap<>();
                params.put("entityId", "xxxx"); // id of the selected record
//                params.put("entity", currentEntity);
                //navigationManager.navigate(context, "form2", params);
            }
        });
    }


    private void setViewsLayout(final ViewHolder holder, Entity entity) {
        Object value;
        int index = 0;
        for (PropertyType p : entity.getMetadata().getProperties()) {
            value = entity.get(p.getName());
            TextView textView = (TextView) holder.viewList.get(index);
            textView.setText(DataUtils.nullFormat(value.toString()));
            index++;
        }
//
//        for (int index = 0; index < numberOfColumns; index++) {
//
//            String fieldName = columnNames[index].toLowerCase();
//
//            TextView textView = (TextView) holder.viewList.get(index);
//
//            Object value = entity.getProperties().get(fieldName);
//            textView.setText(DataUtils.nullFormat(value.toString()));
//        }
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
    public void notifyDataSetChanged(){
        super.notifyDataSetChanged();
        for(View item:cacheViews){
            ViewHolder holder = (ViewHolder) item.getTag();
            holder.charged = false;
        }
    }

    static class ViewHolder {
        int position;
        boolean charged;
        LinearLayout layout;
        List<View> viewList;
    }
}
