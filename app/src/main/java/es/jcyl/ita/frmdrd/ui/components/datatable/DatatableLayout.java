package es.jcyl.ita.frmdrd.ui.components.datatable;
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
 * without warranties or conditions of any kind, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

import es.jcyl.ita.crtrepo.Entity;
import es.jcyl.ita.crtrepo.Repository;
import es.jcyl.ita.crtrepo.context.CompositeContext;
import es.jcyl.ita.crtrepo.db.SQLQueryFilter;
import es.jcyl.ita.crtrepo.query.Criteria;
import es.jcyl.ita.crtrepo.query.Filter;
import es.jcyl.ita.crtrepo.query.Sort;
import es.jcyl.ita.frmdrd.R;
import es.jcyl.ita.frmdrd.context.ContextUtils;
import es.jcyl.ita.frmdrd.context.impl.AndViewContext;
import es.jcyl.ita.frmdrd.el.ValueBindingExpression;
import es.jcyl.ita.frmdrd.el.ValueExpressionFactory;
import es.jcyl.ita.frmdrd.repo.query.ConditionBinding;
import es.jcyl.ita.frmdrd.repo.query.FilterHelper;
import es.jcyl.ita.frmdrd.ui.components.DynamicComponent;
import es.jcyl.ita.frmdrd.ui.components.EntitySelector;
import es.jcyl.ita.frmdrd.ui.components.column.UIColumn;
import es.jcyl.ita.frmdrd.ui.components.column.UIFilter;
import es.jcyl.ita.frmdrd.util.DataUtils;
import es.jcyl.ita.frmdrd.view.converters.TextViewConverter;
import es.jcyl.ita.frmdrd.view.render.RenderingEnv;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */

public class DatatableLayout extends LinearLayout implements DynamicComponent, EntitySelector {
    private final String HEADER_SUFIX = "_header";

    private int offset = 0;
    private int pageSize = 20;
    private UIDatatable dataTable;
    private Repository repo;
    private RenderingEnv renderingEnv;
    private List<Entity> entities = new ArrayList<>();

    // view sorting and filtering criteria
    private Filter filter;
    private Sort sort;

    private AndViewContext thisViewCtx = new AndViewContext(this);

    // inner view elements
    private LinearLayout headerView;
    private ListView bodyView;

    public DatatableLayout(Context context) {
        super(context);
    }

    public DatatableLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DatatableLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    public void setBodyView(ListView bodyView) {
        this.bodyView = bodyView;

        ListEntityAdapter dataAdapter = new ListEntityAdapter(this.getContext(), this,
                R.layout.list_item, entities);
        this.bodyView.setAdapter(dataAdapter);

        this.bodyView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(final AbsListView view,
                                             final int scrollState) {
                int threshold = 1;
                int count = bodyView.getCount();

                if (scrollState == SCROLL_STATE_IDLE) {
                    if (bodyView.getLastVisiblePosition() >= count - threshold) {
                        loadNextPage();
                    }
                }
            }

            @Override
            public void onScroll(final AbsListView view,
                                 final int firstVisibleItem, final int visibleItemCount,
                                 final int totalItemCount) {
            }
        });
    }

    public void setHeaderView(LinearLayout headerView) {
        this.headerView = headerView;
    }

    /*************************************/
    /** Dynamic component interface **/
    /*************************************/

    public void load(RenderingEnv env) {
        this.renderingEnv = env;
        fillHeader(this.getContext(), this.headerView);

        // set filter to repo using current view data
        this.offset = 0;
        this.entities.clear();

        CompositeContext ctx = setupThisContext(env);
        this.filter = setupFilter(ctx, this.getDatatable().getFilter());
        // read first page to render data
        loadNextPage();
    }


    /**
     * Get the definition filter from the dataTable and construct an effective filter using the
     * context information.
     *
     * @param context
     * @return
     */
    private Filter setupFilter(CompositeContext context, Filter defFilter) {
        Filter f = FilterHelper.createInstance(repo);
        if (defFilter != null) {
            FilterHelper.evaluateFilter(context, defFilter, f);
        }
        f.setOffset(this.offset);
        f.setPageSize(this.pageSize);
        return f;
    }

    private void loadNextPage() {
        this.filter.setPageSize(this.pageSize);
        this.filter.setOffset(this.offset);
        //this.entities.clear();
        addData();

        this.offset += this.pageSize;
    }

    private void reloadData() {
        this.entities.clear();
        addData();
    }

    private void addData() {
        this.entities.addAll(this.repo.find(this.filter));

        //notify that the model changedA
        ListEntityAdapter adapter = (ListEntityAdapter) bodyView.getAdapter();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    private View createHeaderView(final Context viewContext, final ViewGroup parent, final UIColumn column) {
        View output = null;

        String columnName = column.getHeaderText();

        LayoutInflater inflater = LayoutInflater.from(viewContext);
        output = inflater.inflate(R.layout.list_item_header, parent,
                false);
        final TextView fieldNameView = output
                .findViewById(R.id.list_header_textview);
        fieldNameView.setText(DataUtils.nullFormat(columnName));

        if (column.isFiltering()) {
            addHeaderFilterLayout(column, output, fieldNameView);
        }

        return output;
    }

    private void addHeaderFilterLayout(final UIColumn column, View headerLayout, View fieldNameView) {
        final LinearLayout filterLayout = headerLayout
                .findViewById(R.id.list_header_filter_layout);

        final EditText filterText = headerLayout
                .findViewById(R.id.list_header_filter_text);
        String tag = column.getId() + HEADER_SUFIX;
        filterText.setTag(tag);
        addHeaderToCtx(column.getId(), tag);

        final ImageView filterOrder = headerLayout.findViewById(R.id.list_header_filter_order);


        fieldNameView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (filterLayout.getVisibility() == View.VISIBLE) {
                    setHeaderFilterVisibility(View.GONE);
                    //resetFilter();
                } else {
                    setHeaderFilterVisibility(View.VISIBLE);
                }

            }
        });

        filterText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                updateFilter();
            }
        });

        filterOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                String columnOrderProperty = column.getHeaderFilter().getOrderProperty();

                Drawable orderImage = null;
                if (sort == null || !columnOrderProperty.equals(sort.getProperty())) {
                    sort = createHeaderSort(column, Sort.SortType.ASC);
                    orderImage = ContextCompat
                            .getDrawable(getContext(), R.drawable.
                                    order_arrow_asc);

                    //TODO change all other images to sort disabled

                } else {
                    Sort.SortType type = sort.getType();
                    if (type.equals(Sort.SortType.DESC)) {
                        sort = createHeaderSort(column, Sort.SortType.ASC);
                        orderImage = ContextCompat
                                .getDrawable(getContext(), R.drawable.
                                        order_arrow_asc);
                    } else {
                        sort = createHeaderSort(column, Sort.SortType.DESC);
                        orderImage = ContextCompat
                                .getDrawable(getContext(), R.drawable.
                                        order_arrow_desc);
                    }
                }

                filterOrder.setImageDrawable(orderImage);
                updateFilter();
            }
        });
    }

    private void setHeaderFilterVisibility(int visibility) {
        for (int i = 0; i < this.headerView.getChildCount(); i++) {
            View header_item = this.headerView.getChildAt(i);
            View header_filter = header_item.findViewById(R.id.list_header_filter_layout);
            header_filter.setVisibility(visibility);
        }
    }

    /**
     * Updates the filter with the content of the headers of each column of the table
     */
    private void updateFilter() {
        ConditionBinding[] conditions = new ConditionBinding[this.getDatatable().getColumns().length];
        int i = 0;
        for (UIColumn c : this.getDatatable().getColumns()) {
            String headerTextValue = thisViewCtx.getString(c.getId());
            if (StringUtils.isNotEmpty(headerTextValue)) {
                conditions[i] = createHeaderCondition(c);
                i++;
            }
        }

        Filter filter = new SQLQueryFilter();
        if (conditions.length > 0) {
            Criteria criteria = Criteria.and(conditions);
            filter.setCriteria(criteria);
        }

        if (sort != null) {
            Sort[] sorts = {sort};
            filter.setSorting(sorts);
        }


        CompositeContext ctx = setupThisContext(this.renderingEnv);
        Filter headerFilter = setupFilter(ctx, filter);

        this.filter = headerFilter;
        this.offset = 0;

        reloadData();
    }


    /**
     * @param column
     * @return
     */
    private ConditionBinding createHeaderCondition(UIColumn column) {
        UIFilter headerFilter = column.getHeaderFilter();
        ValueBindingExpression valueExpression = headerFilter.getFilterValueExpression();
        if (valueExpression == null) {
            ValueExpressionFactory exprFactory = ValueExpressionFactory.getInstance();
            valueExpression = exprFactory.create("${this." + headerFilter.getFilterProperty() + "}");
        }

        ConditionBinding condition = new ConditionBinding(headerFilter.getFilterProperty(), headerFilter.getMatchingOperator(), null);
        condition.setBindingExpression(valueExpression);
        return condition;
    }

    /**
     * @param column
     * @param type
     * @return
     */
    private Sort createHeaderSort(UIColumn column, Sort.SortType type) {
        String orderProperty = column.getHeaderFilter().getOrderProperty();

        Sort sort = new Sort(orderProperty, type);
        return sort;
    }

    private void addHeaderToCtx(String columnId, String tag) {
        thisViewCtx.registerViewElement(columnId, tag, new TextViewConverter(), String.class);
    }

    private CompositeContext setupThisContext(RenderingEnv env) {
        thisViewCtx.setPrefix("this");
        CompositeContext ctx = ContextUtils.combine(env.getContext(), thisViewCtx);
        return ctx;
    }

    private void fillHeader(Context viewContext, LinearLayout headersLayout) {
        headersLayout.removeAllViews();

        for (UIColumn c : this.getDatatable().getColumns()) {
            final View dataHeader = createHeaderView(viewContext,
                    headersLayout, c);
            headersLayout.addView(dataHeader);
        }

        headersLayout.setVisibility(View.VISIBLE);
    }

    public UIDatatable getDatatable() {
        return dataTable;
    }

    public void setDatatable(UIDatatable dataTable) {
        this.dataTable = dataTable;
        this.repo = dataTable.getRepo();
    }

    public RenderingEnv getRenderingEnv() {
        return renderingEnv;
    }

    /*************************************/
    /** Entity Selector interface **/
    /*************************************/
    @Override
    public List<Entity> getSelectedEntities() {
        return null;
    }
}
