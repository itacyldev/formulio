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
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import es.jcyl.ita.crtrepo.Entity;
import es.jcyl.ita.crtrepo.Repository;
import es.jcyl.ita.crtrepo.context.CompositeContext;
import es.jcyl.ita.crtrepo.meta.EntityMeta;
import es.jcyl.ita.crtrepo.query.Filter;
import es.jcyl.ita.frmdrd.R;
import es.jcyl.ita.frmdrd.repo.query.CriteriaVisitor;
import es.jcyl.ita.frmdrd.repo.query.FilterHelper;
import es.jcyl.ita.frmdrd.ui.components.DynamicComponent;
import es.jcyl.ita.frmdrd.ui.components.column.UIColumn;
import es.jcyl.ita.frmdrd.util.DataUtils;
import es.jcyl.ita.frmdrd.view.render.RenderingEnv;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */

public class DatatableLayout extends LinearLayout implements DynamicComponent {
    private int offset = 0;
    private int pageSize = 20;
    private UIDatatable dataTable;
    private EntityMeta meta;
    private Repository repo;
    private RenderingEnv renderingEnv;
    private List<Entity> entities = new ArrayList<>();
    // view sorting and filtering criteria
    private Filter filter;

    private static CriteriaVisitor criteriaVisitor = new CriteriaVisitor();

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

    private void loadNextPage() {
        this.filter.setPageSize(this.pageSize);
        this.filter.setOffset(this.offset);
        //this.entities.clear();
        this.entities.addAll(this.repo.find(this.filter));

        //notify that the model changed
        ListEntityAdapter adapter = (ListEntityAdapter) bodyView.getAdapter();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }

        this.offset += this.pageSize;
    }

    private View createHeaderView(final Context viewContext, final ViewGroup parent,
                                  final String columnName) {
        View output = null;

        LayoutInflater inflater = LayoutInflater.from(viewContext);
        output = inflater.inflate(R.layout.list_item_header, parent,
                false);
        final TextView fieldName = output
                .findViewById(R.id.list_header_textview);

        fieldName.setText(DataUtils.nullFormat(columnName));
        return output;
    }

    public void setBodyView(ListView bodyView) {
        this.bodyView = bodyView;
    }

    public void setHeaderView(LinearLayout headerView) {
        this.headerView = headerView;
    }


    public void load(RenderingEnv environment) {
        this.renderingEnv = environment;
        fillHeader(this.getContext(), this.headerView);

        // set filter to repo using current view data
        this.filter = setupFilter(environment.getContext());
        // read first page to render data
        loadNextPage();
        ListEntityAdapter dataAdapter = new ListEntityAdapter(this.getContext(), this,
                R.layout.list_item, entities);


        bodyView.setAdapter(dataAdapter);
        bodyView.setOnScrollListener(new AbsListView.OnScrollListener() {
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

    private Filter setupFilter(CompositeContext context) {
        Filter f = FilterHelper.createInstance(repo);

        FilterHelper.evaluateFilter(context, this.filter, f);
        f.setOffset(this.offset);
        f.setPageSize(this.pageSize);
        return f;
    }

    private void fillHeader(Context viewContext, LinearLayout headersLayout) {
        headersLayout.removeAllViews();
        for (UIColumn c : this.getDatatable().getColumns()) {
            final View dataHeader = createHeaderView(viewContext,
                    headersLayout, c.getHeaderText());
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
        this.meta = this.repo.getMeta();
    }

    public RenderingEnv getRenderingEnv() {
        return renderingEnv;
    }


}
