package es.jcyl.ita.formic.forms.components.datatable;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import org.apache.commons.jexl3.JexlContext;
import org.apache.commons.jexl3.JxltEngine;
import org.apache.commons.lang3.StringUtils;
import org.mini2Dx.beanutils.ConvertUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import es.jcyl.ita.formic.forms.R;
import es.jcyl.ita.formic.forms.actions.UserAction;
import es.jcyl.ita.formic.forms.actions.UserActionHelper;
import es.jcyl.ita.formic.forms.actions.events.Event;
import es.jcyl.ita.formic.forms.actions.events.UserEventInterceptor;
import es.jcyl.ita.formic.forms.components.UIComponent;
import es.jcyl.ita.formic.forms.components.column.UIColumn;
import es.jcyl.ita.formic.forms.context.impl.EntityContext;
import es.jcyl.ita.formic.forms.controllers.UIAction;
import es.jcyl.ita.formic.forms.controllers.UIParam;
import es.jcyl.ita.formic.forms.el.JexlFormUtils;
import es.jcyl.ita.formic.forms.util.DataUtils;
import es.jcyl.ita.formic.forms.view.render.renderer.WidgetContext;
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

        //adjustColumnWidth((LinearLayout) item);
        //adjustColumnWidth(this.dtLayout.getHeaderView());

        //if there is no route in the table
        if (this.dtLayout.getComponent().getRoute() == null) {
            item.setBackground(ContextCompat.getDrawable(context, R.drawable.unselectablebuttonbackground));
        }

        if (this.dtLayout.getEntities().size() == position + 1){
            for(int i = 0; i < this.dtLayout.getEntities().size(); i++){
                getMinColumnWidth((LinearLayout) cacheViews[i]);
            }
            getMinColumnWidth(this.dtLayout.getHeaderView());
            for(int i = 0; i < this.dtLayout.getEntities().size(); i++){
                adjustColumnWidth((LinearLayout) cacheViews[i]);
                adjustColumnWidth(this.dtLayout.getHeaderView());
                item.requestLayout();
            }

        }

        setAlternateRowColor(position, item);

        return item;
    }

    private void setAlternateRowColor(int position, View item) {
        if (position % 2 == 1) {
            TypedArray ta = context.obtainStyledAttributes(new int[]{R.attr.onSurface5Color});
            item.setBackgroundColor(ta.getColor(0, Color.GRAY));
            //item.setBackgroundColor(R.attr.onSurface5Color);
        }
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
            } /*else {
                    minColWidths[i] = colWidth;
                }*/
        }
    }

    public void getMinColumnWidth(LinearLayout rowLayout) {

        Integer nChild = rowLayout.getChildCount();
        for (int i = 0; i < nChild; i++) {
            View cellView = rowLayout.getChildAt(i);

            Integer colWidth = 0;
            cellView.measure(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            colWidth = cellView.getMeasuredWidth();

            Integer maxColWidth = minColWidths[i];

            if (maxColWidth <= colWidth) {
                minColWidths[i] = colWidth;
            }
        }
    }

    private void setOnClickListener(LinearLayout layout,
                                    Entity currentEntity) {
        layout.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                UserEventInterceptor interceptor = dtLayout.getRenderingEnv().getUserActionInterceptor();
                // create navigation route using current entity Id as parameter
                if (interceptor != null && StringUtils.isNotBlank(dtLayout.getComponent().getRoute())) {
                    UIAction uiAction = dtLayout.getComponent().getAction();
                    if (uiAction == null) {
                        return;
                    }
                    WidgetContext widgetContext = dtLayout.getWidgetContext();
                    widgetContext.addContext(new EntityContext(currentEntity));

                    UserAction action = UserActionHelper.evaluate(uiAction, widgetContext, dtLayout.getComponent());
                    Map<String, Object> params = action.getParams();
                    if (params == null || !params.containsKey("entityId")) {
                        // add current entityId as default param if it's not already set
                        action.addParam("entityId", (Serializable) currentEntity.getId());
                    }
                    Event event = new Event(Event.EventType.CLICK, null, action);
                    interceptor.notify(event);
                }
            }
        });
    }

    public static UserAction evaluate(UIAction actionTemplate, es.jcyl.ita.formic.core.context.Context context, UIComponent component) {
        String strRoute = "";
        if (actionTemplate.getRoute() != null) {
            JxltEngine.Expression e = JexlFormUtils.createExpression(actionTemplate.getRoute());
            Object route = e.evaluate((JexlContext) context);
            strRoute = (String) ConvertUtils.convert(route, String.class);
        }
        UserAction action = new UserAction(actionTemplate.getType(), strRoute, component);
        action.setRegisterInHistory(actionTemplate.isRegisterInHistory());

        action.setRefresh(actionTemplate.getRefresh());
        if (actionTemplate.hasParams()) {
            for (UIParam param : actionTemplate.getParams()) {
                Object value = JexlFormUtils.eval(context, param.getValue());
                if (value != null) {
                    action.addParam(param.getName(), (Serializable) value);
                }
            }
        }
        return action;
    }

    private void setViewsLayout(final ViewColumnHolder holder, Entity entity) {
        UIColumn[] columns = dtLayout.getComponent().getColumns();
        Object[] values = JexlFormUtils.bulkEval(entity, columns);

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
