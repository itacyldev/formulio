package es.jcyl.ita.frmdrd.ui.components.datatable;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;

import androidx.annotation.NonNull;

import es.jcyl.ita.frmdrd.R;
import es.jcyl.ita.frmdrd.view.render.BaseRenderer;
import es.jcyl.ita.frmdrd.view.render.RenderingEnv;

/*
 * Copyright 2020 Gustavo Río Briones (gustavo.rio@itacyl.es), ITACyL (http://www.itacyl.es).
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
 * @author Gustavo Río Briones (gustavo.rio@itacyl.es)
 */

public class UIDatatableRenderer extends BaseRenderer<DatatableLayout, UIDatatable> {

    private final static float ROW_HEIGHT_DP = 38.75f;

    @Override
    protected DatatableLayout createBaseView(RenderingEnv env, UIDatatable component) {
        UIDatatable dtComponent = component;
        DatatableLayout datatableView = (DatatableLayout) View.inflate(env.getViewContext(),
                R.layout.component_datatable_layout, null);
        datatableView.setDatatable(dtComponent);
        datatableView.setTag(getBaseViewTag(component));
        return datatableView;
    }

    @Override
    protected void setupView(RenderingEnv env, DatatableLayout datatableView, UIDatatable component) {
//        component.getRepo().setContext(env.getContext()); // the context must be already set by the repository factory
        LinearLayout tableView = datatableView.findViewById(R.id.list_layout);
        LinearLayout headerLayout = tableView.findViewById(R.id.list_layout_headers);
        datatableView.setHeaderView(headerLayout);

        ListView bodyView = tableView.findViewById(R.id.list_view);
        setLayoutParams(bodyView, env.getViewContext(), component);
        datatableView.setBodyView(bodyView);

        datatableView.load(env);
    }

    /**
     * Sets dimension parameters.
     *
     * @param tableView DataTable container.
     * @param context  Context for resources.
     * @param component
     */
    private void setLayoutParams(@NonNull ListView tableView,
                                 @NonNull Context context, @NonNull UIDatatable component) {
        final int layout_width = ListView.LayoutParams.WRAP_CONTENT;
        final int layout_height;
        if (component.getNumVisibleRows() < 0){
            layout_height = ListView.LayoutParams.MATCH_PARENT;
        } else {
            layout_height = (int)(context.getResources().getDisplayMetrics().density *
                    ROW_HEIGHT_DP *
                    component.getNumVisibleRows());
        }
        tableView.setLayoutParams(new LinearLayout.LayoutParams(layout_width, layout_height));
    }
}
