package es.jcyl.ita.formic.forms.components.datatable;

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
import java.util.Arrays;
import java.util.List;

import es.jcyl.ita.formic.forms.R;
import es.jcyl.ita.formic.forms.actions.UserAction;
import es.jcyl.ita.formic.forms.actions.interceptors.ViewUserActionInterceptor;
import es.jcyl.ita.formic.forms.components.column.UIColumn;
import es.jcyl.ita.formic.forms.el.JexlUtils;
import es.jcyl.ita.formic.forms.util.DataUtils;
import es.jcyl.ita.formic.repo.Entity;

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

    private final int fieldLimit;
    private static final int RECORDS_CACHE = 20;


    public void setMinColWidths(Integer[] minColWidths) {
        this.minColWidths = minColWidths;
    }

    private Integer[] minColWidths;

    /**
     * Behaviour attributes
     */
    private DatatableWidget dtLayout;


    public ListEntityAdapter(final Context context, DatatableWidget datatableLayout,
                             final int textViewResourceId,
                             final List<Entity> entities) {
        super(context, textViewResourceId, entities);
        this.context = context;
        this.dtLayout = datatableLayout;

        this.fieldLimit = datatableLayout.getComponent().getNumFieldsToShow();
        this.cacheViews = new View[RECORDS_CACHE];
        this.inflater = LayoutInflater.from(getContext());

        this.minColWidths = new Integer[dtLayout.getComponent().getColumns().length];
        Arrays.fill(minColWidths, 0);
    }

    @Override
    public View getView(final int position, View convertView,
                        final ViewGroup parent) {
        View item = this.cacheViews[position % cacheViews.length];
        ViewColumnHolder holder;

        final Entity currentEntity = getItem(position);

        if (item == null) {

            item = inflater.inflate(R.layout.entity_list_item, parent, false);

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

        // Adjust the column width to the content size
        adjustColumnWidth((LinearLayout) item);
        adjustColumnWidth(this.dtLayout.getHeaderView());

        return item;
    }

    /**
     * @param rowLayout
     */
    public void adjustColumnWidth(LinearLayout rowLayout) {

        Integer nChild = rowLayout.getChildCount();
        for (int i = 0; i < nChild; i++) {
            View cellView = rowLayout.getChildAt(i);
            Integer colWidth = 0;
            colWidth = cellView.getMeasuredWidth();
            Integer maxColWidth = minColWidths[i];

            if (maxColWidth > colWidth) {
                cellView.setMinimumWidth(maxColWidth);
            } else {
                minColWidths[i] = colWidth;
            }
        }
    }

    private void setOnClickListener(LinearLayout layout,
                                    Entity currentEntity) {
        layout.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                ViewUserActionInterceptor userActionInterceptor = dtLayout.getRenderingEnv().getUserActionInterceptor();
                // create navigation route using current entity Id as parameter
                if (userActionInterceptor != null && StringUtils.isNoneBlank(dtLayout.getComponent().getRoute())) {
                    UserAction action = UserAction.navigate(context, dtLayout.getComponent(),
                            dtLayout.getComponent().getRoute());
                    action.addParam("entityId", (Serializable) currentEntity.getId());
                    userActionInterceptor.doAction(action);
                }
            }
        });
    }

    private void setViewsLayout(final ViewColumnHolder holder, Entity entity) {
        UIColumn[] columns = dtLayout.getComponent().getColumns();
        Object[] values = JexlUtils.bulkEval(entity, columns);

        for (int i = 0; i < columns.length && i < holder.viewList.size(); i++) {
            String stringValue = (String) ConvertUtils.convert(values[i], String.class);
            TextView textView = (TextView) holder.viewList.get(i);
            textView.setText(DataUtils.nullFormat(stringValue));
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
        UIColumn[] columns = dtLayout.getComponent().getColumns();
        for (UIColumn column : columns) {
            // create context for this entity to evaluate expression
            TextView view = createTextView(parent);
            layout.addView(view);
            holder.viewList.add(view);

            index++;
            if (index >= fieldLimit) {
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
                R.layout.entity_list_textview, parent, false);
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