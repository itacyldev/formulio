package es.jcyl.ita.frmdrd.ui.components.autocomplete;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;
import org.mini2Dx.beanutils.ConvertUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import es.jcyl.ita.crtrepo.Entity;
import es.jcyl.ita.frmdrd.R;
import es.jcyl.ita.frmdrd.actions.UserAction;
import es.jcyl.ita.frmdrd.actions.interceptors.ViewUserActionInterceptor;
import es.jcyl.ita.frmdrd.el.JexlUtils;
import es.jcyl.ita.frmdrd.ui.components.column.UIColumn;
import es.jcyl.ita.frmdrd.ui.components.datatable.DatatableLayout;
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
    private final AutoCompleteView view;
    private Context context;

    private LayoutInflater inflater;
    private View[] cacheViews;

    private static final int RECORDS_CACHE = 20;

    /**
     * Behaviour attributes
     */


    public ListEntityAdapter(final Context context, AutoCompleteView view,
                             final int textViewResourceId,
                             final List<Entity> entities) {
        super(context, textViewResourceId, entities);
        this.context = context;
        this.view = view;
        this.cacheViews = new View[RECORDS_CACHE];
        this.inflater = LayoutInflater.from(getContext());
    }

    @Override
    public View getView(final int position, View convertView,
                        final ViewGroup parent) {

        View item = this.cacheViews[position % cacheViews.length];
        ViewColumnHolder holder;

        final Entity currentEntity = getItem(position);

        if (item == null) {

            item = inflater.inflate(R.layout.list_item, parent, false);

            holder = new ViewColumnHolder();
            holder.position = position;
            holder.charged = false;
            holder.layout =
                    (LinearLayout) item.findViewById(R.id.list_item_layout);
            holder.viewList = new ArrayList<>();

            createRowLayout(holder, parent);

            item.setTag(holder);
        } else {
            holder = (ViewColumnHolder) item.getTag();
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
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
            }
        });
    }

    private void setViewsLayout(final ViewColumnHolder holder, Entity entity) {
        for (int i = 0; i < 1 && i < holder.viewList.size(); i++) {
            TextView textView = (TextView) holder.viewList.get(i);
            textView.setText(DataUtils.nullFormat(entity.getId().toString()));
        }
    }

    /**
     * Creates the TextViews needed to
     *
     * @param holder
     * @param parent
     */
    private void createRowLayout(final ViewColumnHolder holder,
                                 final ViewGroup parent) {

        LinearLayout layout = holder.layout;

        int index = 0;
        for (int i=0;i<1; i++) {
            // create context for this entity to evaluate expression
            TextView view = createTextView(parent);
            layout.addView(view);
            holder.viewList.add(view);
            index++;
            if (index >= 3) {
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
            if (item != null) {
                ViewColumnHolder holder = (ViewColumnHolder) item.getTag();
                holder.charged = false;
            }
        }
    }


    class ViewColumnHolder {
        int position;
        boolean charged;
        LinearLayout layout;
        List<View> viewList;
    }

}
