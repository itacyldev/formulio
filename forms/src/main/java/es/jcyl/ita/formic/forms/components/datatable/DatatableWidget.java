package es.jcyl.ita.formic.forms.components.datatable;
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

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
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

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import es.jcyl.ita.formic.core.context.CompositeContext;
import es.jcyl.ita.formic.forms.R;
import es.jcyl.ita.formic.forms.components.DynamicComponent;
import es.jcyl.ita.formic.forms.components.EntityListProvider;
import es.jcyl.ita.formic.forms.components.column.UIColumn;
import es.jcyl.ita.formic.forms.components.column.UIColumnFilter;
import es.jcyl.ita.formic.forms.context.ContextUtils;
import es.jcyl.ita.formic.forms.context.impl.AndViewContext;
import es.jcyl.ita.formic.forms.el.ValueBindingExpression;
import es.jcyl.ita.formic.forms.el.ValueExpressionFactory;
import es.jcyl.ita.formic.forms.repo.query.ConditionBinding;
import es.jcyl.ita.formic.forms.repo.query.FilterHelper;
import es.jcyl.ita.formic.forms.util.DataUtils;
import es.jcyl.ita.formic.forms.view.converters.TextViewConverter;
import es.jcyl.ita.formic.forms.view.render.renderer.RenderingEnv;
import es.jcyl.ita.formic.forms.view.selection.EntitySelector;
import es.jcyl.ita.formic.forms.view.selection.SelectionManager;
import es.jcyl.ita.formic.forms.view.widget.StatefulWidget;
import es.jcyl.ita.formic.forms.view.widget.Widget;
import es.jcyl.ita.formic.repo.Entity;
import es.jcyl.ita.formic.repo.Repository;
import es.jcyl.ita.formic.repo.query.Criteria;
import es.jcyl.ita.formic.repo.query.Expression;
import es.jcyl.ita.formic.repo.query.Filter;
import es.jcyl.ita.formic.repo.query.FilterRepoUtils;
import es.jcyl.ita.formic.repo.query.Sort;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */

public class DatatableWidget extends Widget<UIDatatable>
        implements DynamicComponent, EntityListProvider, EntitySelector, StatefulWidget {

    private final String HEADER_FILTER_SUFIX = "_header_filter";
    private final String HEADER_ORDER_SUFIX = "header_order";

    private int offset = 0;
    private int pageSize = 20;
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
    private SelectionManager selectionMgr;

    public DatatableWidget(Context context) {
        super(context);
    }

    public DatatableWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DatatableWidget(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setup(RenderingEnv env) {
        super.setup(env);
        this.repo = component.getRepo();
    }

    @SuppressLint("ResourceAsColor")
    public void setBodyView(ListView bodyView) {
        this.bodyView = bodyView;

        ListEntityAdapter dataAdapter = new ListEntityAdapter(this.getContext(), this,
                R.layout.entity_list_item, entities);
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
        fillHeader();

        // set filter to repo using current view data
        this.offset = 0;
        this.entities.clear();

        CompositeContext ctx = setupThisContext(env);
        this.filter = setupFilter(ctx, this.getComponent().getFilter());
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
        Filter f = FilterRepoUtils.createInstance(repo);
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
        addNoResults();

        this.offset += this.pageSize;
    }

    private void addNoResults() {
        TextView list_no_results = this.findViewById(R.id.list_no_results);
        if (this.entities.size() == 0) {
            list_no_results.setVisibility(VISIBLE);
        } else {
            list_no_results.setVisibility(GONE);
        }
    }

    private View createHeaderView(final Context viewContext, final ViewGroup parent, final UIColumn column) {
        View output = null;

        String columnName = column.getHeaderText();

        LayoutInflater inflater = LayoutInflater.from(viewContext);
        output = inflater.inflate(R.layout.entity_list_header, parent,
                false);

        final TextView fieldNameView = output
                .findViewById(R.id.list_header_textview);
        fieldNameView.setText(DataUtils.nullFormat(StringUtils.isNotBlank(columnName) ? StringUtils.capitalize(columnName) : columnName));

        final ImageView searchView = output
                .findViewById(R.id.list_header_img);

        addHeaderFilterLayout(column, output, fieldNameView, searchView);

        if (!column.isFiltering()) {
            searchView.setVisibility(INVISIBLE);
        }

        return output;
    }

    private void addHeaderFilterLayout(final UIColumn column, View headerLayout, View fieldNameView, ImageView searchView) {
        final LinearLayout filterLayout = headerLayout
                .findViewById(R.id.list_header_filter_layout);

        final EditText filterText = headerLayout
                .findViewById(R.id.list_header_filter_text);
        String tag = column.getId() + HEADER_FILTER_SUFIX;
        filterText.setTag(tag);
        addHeaderToCtx(column.getId(), tag);

        final ImageView filterOrder = headerLayout.findViewById(R.id.list_header_filter_order);
        filterOrder.setTag(column.getId() + HEADER_ORDER_SUFIX);


        fieldNameView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                setFilterVisibility(filterLayout, filterText);

            }
        });

        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                setFilterVisibility(filterLayout, filterText);

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
                    orderImage = getOrderIcon(Sort.SortType.ASC);
                } else {
                    Sort.SortType type = sort.getType();
                    if (type.equals(Sort.SortType.DESC)) {
                        sort = createHeaderSort(column, Sort.SortType.ASC);
                        orderImage = getOrderIcon(Sort.SortType.ASC);
                    } else {
                        sort = createHeaderSort(column, Sort.SortType.DESC);
                        orderImage = getOrderIcon(Sort.SortType.DESC);
                    }
                }
                filterOrder.setImageDrawable(orderImage);

                //sets a noorder image for the rest of the columns
                disableOrderImages(column.getId());

                updateFilter();
            }
        });
    }

    private void setFilterVisibility(LinearLayout filterLayout, EditText filterText) {
        if (filterLayout.getVisibility() == View.VISIBLE) {
            setHeaderFilterVisibility(View.GONE);
            resetFilter();
        } else {
            setHeaderFilterVisibility(View.VISIBLE);
            filterText.requestFocus();
        }
    }

    /**
     * Gets the icon depending on app's theme
     *
     * @param type
     * @return
     */
    private Drawable getOrderIcon(Sort.SortType type) {
        Drawable drawableFromTheme;
        int[] attrs;
        if (Sort.SortType.ASC.equals(type)) {
            attrs = new int[]{R.attr.iconOrderAsc};
        } else if (Sort.SortType.DESC.equals(type)) {
            attrs = new int[]{R.attr.iconOrderDesc};
        } else {
            attrs = new int[]{R.attr.iconNoorder};
        }
        TypedArray ta = getContext().obtainStyledAttributes(attrs);
        drawableFromTheme = ta.getDrawable(0);

        return drawableFromTheme;
    }

    /**
     *
     */
    private void resetFilter() {
        sort = null;
        for (UIColumn column : this.getComponent().getColumns()) {
            if (column.isFiltering()) {
                EditText filterText = this.findViewWithTag(column.getId() + HEADER_FILTER_SUFIX);
                if (StringUtils.isNotEmpty(filterText.getText().toString())) {
                    filterText.setText("");
                }
            }
        }
        disableOrderImages(null);
    }

    /**
     * Sets a no order image for columns other than the parameter
     *
     * @param columnid
     */
    private void disableOrderImages(String columnid) {
        Drawable orderImage = ContextCompat
                .getDrawable(getContext(), R.drawable.
                        order_arrow_noorder);

        for (UIColumn column : this.getComponent().getColumns()) {
            if (!column.getId().equals(columnid)) {
                ImageView orderImageView = this.findViewWithTag(column.getId() + HEADER_ORDER_SUFIX);
                if (orderImageView != null) {
                    orderImageView.setImageDrawable(orderImage);
                }
            }
        }
    }

    /**
     * Sets the visibility of header filters
     *
     * @param visibility
     */
    private void setHeaderFilterVisibility(int visibility) {
        for (int i = 0; i < this.headerView.getChildCount(); i++) {
            View header_item = this.headerView.getChildAt(i);
            View header_filter = header_item.findViewById(R.id.list_header_filter_layout);
            header_filter.setVisibility(visibility);

            setFilterSearchVisibility(header_item, this.getComponent().getColumn(i));
            setFilterOrderVisibility(header_item, this.getComponent().getColumn(i));

        }
    }

    private void setFilterSearchVisibility(View header_item, UIColumn column) {
        if (!column.isFiltering()) {
            ImageView filterSearch = header_item.findViewById(R.id.list_header_filter_search);
            filterSearch.setVisibility(INVISIBLE);
            EditText filterText = header_item.findViewById(R.id.list_header_filter_text);
            filterText.setVisibility(INVISIBLE);
        }
    }
    private void setFilterOrderVisibility(View header_item, UIColumn column) {
        if (!column.isOrdering()) {
            ImageView filterOrder = header_item.findViewById(R.id.list_header_filter_order);
            filterOrder.setVisibility(INVISIBLE);
        }
    }

    /**
     * Updates the filter with the content of the headers of each column of the table
     */
    private void updateFilter() {
        this.entities.clear();
        this.offset = 0;

        ConditionBinding[] conditions = new ConditionBinding[this.getComponent().getColumns().length];
        int i = 0;
        for (UIColumn c : this.getComponent().getColumns()) {
            if (c.isFiltering()) {
                String headerTextValue = thisViewCtx.getString(c.getId());
                if (StringUtils.isNotEmpty(headerTextValue)) {
                    conditions[i] = createHeaderCondition(c);
                    i++;
                }
            }
        }

        if (this.filter == null) {
            filter = FilterRepoUtils.createInstance(this.repo);
        }
        if (conditions.length > 0) {
            Criteria criteria = Criteria.and(conditions);
            if (this.getComponent().getFilter() != null && this.getComponent().getFilter().getExpression() != null) {
                List<Expression> expressions = new ArrayList<>(Arrays.asList(criteria.getChildren()));
                expressions.add(this.getComponent().getFilter().getExpression());
                criteria.setChildren(expressions.toArray(new Expression[expressions.size()]));
            }
            filter.setExpression(criteria);
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
        UIColumnFilter headerFilter = column.getHeaderFilter();
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
        CompositeContext ctx = ContextUtils.combine(env.getWidgetContext(), thisViewCtx);
        return ctx;
    }

    private void fillHeader() {
        headerView.removeAllViews();

        int i = 0;
        for (UIColumn c : this.getComponent().getColumns()) {
            final View dataHeader = createHeaderView(getContext(),
                    headerView, c);
            headerView.addView(dataHeader);
            i++;
        }

        headerView.setVisibility(View.VISIBLE);
    }

    public RenderingEnv getRenderingEnv() {
        return renderingEnv;
    }

    public LinearLayout getHeaderView() {
        return this.headerView;
    }

    @Override
    public void setEntities(List<Entity> entities) {
    }

    @Override
    public List<Entity> getEntities() {
        return this.entities;
    }

    @Override
    public void setSelectionManager(SelectionManager manager) {
        this.selectionMgr = manager;
    }

    @Override
    public void setState(Object value) {
        System.out.println("Restore: " + value);
    }

    @Override
    public Object getState() {
        String value = RandomStringUtils.random(3);
        System.out.println("Save datatable state: " + value);
        return value;
    }

    @Override
    public boolean allowsPartialRestore() {
        return this.component.getAllowsPartialRestore();
    }
}
