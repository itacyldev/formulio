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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.HeaderViewListAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

import es.jcyl.ita.crtrepo.Entity;
import es.jcyl.ita.crtrepo.Repository;
import es.jcyl.ita.crtrepo.context.CompositeContext;
import es.jcyl.ita.crtrepo.context.impl.BasicContext;
import es.jcyl.ita.crtrepo.context.impl.OrderedCompositeContext;
import es.jcyl.ita.crtrepo.query.Filter;
import es.jcyl.ita.frmdrd.R;
import es.jcyl.ita.frmdrd.repo.query.FilterHelper;
import es.jcyl.ita.frmdrd.ui.components.DynamicComponent;
import es.jcyl.ita.frmdrd.ui.components.EntitySelector;
import es.jcyl.ita.frmdrd.ui.components.column.UIColumn;
import es.jcyl.ita.frmdrd.util.DataUtils;
import es.jcyl.ita.frmdrd.view.render.RenderingEnv;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */

public class DatatableLayout extends LinearLayout implements DynamicComponent, EntitySelector {
    private int offset = 0;
    private int pageSize = 20;
    private UIDatatable dataTable;
    private Repository repo;
    private RenderingEnv renderingEnv;
    private List<Entity> entities = new ArrayList<>();
    // view sorting and filtering criteria
    private Filter filter;

    private BasicContext headerContext = new BasicContext("this");

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

        //filterContext.addContext(headerContext);

    }

    /*************************************/
    /** Dynamic component interface **/
    /*************************************/

    public void load(RenderingEnv environment) {
        this.renderingEnv = environment;
        fillHeader(this.getContext(), this.headerView);

        // set filter to repo using current view data
        this.offset = 0;
        this.entities.clear();
        this.filter = setupFilter(environment.getContext());
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
    private Filter setupFilter(CompositeContext context) {
        Filter defFilter = this.getDatatable().getFilter();
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
        this.entities.addAll(this.repo.find(this.filter));

        //notify that the model changedA
        ListEntityAdapter adapter = (ListEntityAdapter) bodyView.getAdapter();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }

        this.offset += this.pageSize;
    }

    private View createHeaderView(final Context viewContext, final ViewGroup parent,
                                  final String columnName, final String columnid) {
        View output = null;

        LayoutInflater inflater = LayoutInflater.from(viewContext);
        output = inflater.inflate(R.layout.list_item_header, parent,
                false);
        final TextView fieldName = output
                .findViewById(R.id.list_header_textview);

        fieldName.setText(DataUtils.nullFormat(columnName));

        final LinearLayout filterLayout = (LinearLayout) output
                .findViewById(R.id.list_header_filter_layout);

        final EditText filterText = (EditText) output
                .findViewById(R.id.list_header_filter_text);


        fieldName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                if (filterLayout.getVisibility() == View.VISIBLE) {
                    filterLayout.setVisibility(View.GONE);
                } else {
                    filterLayout.setVisibility(View.VISIBLE);
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
                updateHeaderContext(columnid, filterText.getText().toString());
            }
        });

        return output;
    }

    private void updateHeaderContext(String columnId, String text) {
        if (StringUtils.isNotEmpty(text)) {
            headerContext.put(columnId, text);
        } else {
            headerContext.remove(columnId);
        }

        CompositeContext filterContext = new OrderedCompositeContext();
        filterContext.addContext(headerContext);
        setupFilter(filterContext);
    }


    private void fillHeader(Context viewContext, LinearLayout headersLayout) {
        headersLayout.removeAllViews();

        HeaderViewListAdapter adapter = (HeaderViewListAdapter) bodyView.getAdapter();

        for (UIColumn c : this.getDatatable().getColumns()) {
            final View dataHeader = createHeaderView(viewContext,
                    headersLayout, c.getHeaderText(), c.getId());
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
