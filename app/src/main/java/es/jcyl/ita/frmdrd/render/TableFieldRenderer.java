package es.jcyl.ita.frmdrd.render;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

import es.jcyl.ita.frmdrd.R;
import es.jcyl.ita.frmdrd.dao.persister.Entity;
import es.jcyl.ita.frmdrd.dao.sources.SampleSourceDescriptor;
import es.jcyl.ita.frmdrd.lifecycle.Lifecycle;
import es.jcyl.ita.frmdrd.ui.form.UIField;
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

public class TableFieldRenderer extends AbstractFieldRenderer {

    private static final int FIELD_LIMIT = 50;

    private List<Entity> entities = new ArrayList<>();

    public TableFieldRenderer(Context context, Lifecycle lifecycle) {
        super(context, lifecycle);
    }

    @Override
    public View render(UIField field) {
        String renderCondition = field.getRenderCondition();

        boolean render = true;
        if (StringUtils.isNotEmpty(renderCondition)) {
            render = this.validateCondition(renderCondition, field.getId());
        }

        LinearLayout linearLayout = (LinearLayout) View.inflate(context,
                R.layout.list_field_layout, null);

        final TextView fieldLabel = linearLayout
                .findViewById(R.id.field_layout_name);
        fieldLabel.setText(field.getLabel());
        fieldLabel.setTag("label");

        LinearLayout tableView = linearLayout.findViewById(R.id.list_layout);

        ListView listView = tableView.findViewById(R.id.list_view);
        LinearLayout headersLayout = tableView.findViewById(R.id.list_layout_headers);

        loadEntities();
        fillHeader(entities.get(0), headersLayout);

        ListEntityAdapter dataAdapter = new ListEntityAdapter(this.context,
                R.layout.list_item, entities, FIELD_LIMIT);
        listView.setAdapter(dataAdapter);


        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(final AbsListView view,
                                             final int scrollState) {
                int threshold = 1;
                int count = listView.getCount();

                if (scrollState == SCROLL_STATE_IDLE) {
                    if (listView.getLastVisiblePosition() >= count - threshold) {
                        loadNextPage();
                    }
                }
            }

            @Override
            public void onScroll(final AbsListView view,
                                 final int firstVisibleItem, final int visibleItemCount,
                                 final int totalItemCount) {
                loadNextPage();
            }
        });

        return linearLayout;
    }

    private void loadEntities() {
        SampleSourceDescriptor source = new SampleSourceDescriptor();
        entities = source.getEntities();
    }

    private View createHeaderView(final ViewGroup parent,
                                  final String columnName) {
        View output = null;

        LayoutInflater inflater = LayoutInflater.from(this.context);
        output = inflater.inflate(R.layout.list_item_header, parent,
                false);

        final TextView fieldName = output
                .findViewById(R.id.list_header_textview);

        fieldName.setText(DataUtils.nullFormat(columnName));
        return output;
    }

    private void fillHeader(final Entity entity, LinearLayout headersLayout) {
        if (entity == null || headersLayout.getChildCount() > 0) {
            return;
        }
        headersLayout.removeAllViews();


        final String[] columns = entity.getMetadata().getColumnNames();
        if (columns != null) {

            for (final String column : columns) {
                if (column != null) {
                    final View dataHeader = createHeaderView(
                            headersLayout, column);
                    headersLayout.addView(dataHeader);
                }
            }

            headersLayout.setVisibility(View.VISIBLE);
        }
    }

    private void loadNextPage() {
        //TODO
    }
}
