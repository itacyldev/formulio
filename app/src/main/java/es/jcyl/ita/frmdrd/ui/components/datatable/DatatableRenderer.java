package es.jcyl.ita.frmdrd.ui.components.datatable;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;

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

public class DatatableRenderer extends BaseRenderer<DatatableLayout, UIDatatable> {

    @Override
    protected DatatableLayout createBaseView(RenderingEnv env, UIDatatable component) {
        UIDatatable dtComponent = (UIDatatable) component;
        DatatableLayout datatableView = (DatatableLayout) View.inflate(env.getViewContext(),
                R.layout.component_datatable_layout, null);
        datatableView.setDatatable(dtComponent);
        datatableView.setTag(getBaseViewTag(component));
        return datatableView;
    }

    @Override
    protected void setupView(RenderingEnv env, DatatableLayout datatableView, UIDatatable component) {
        component.getRepo().setContext(env.getContext());

        LinearLayout tableView = datatableView.findViewById(R.id.list_layout);

        LinearLayout headerLayout = tableView.findViewById(R.id.list_layout_headers);
        datatableView.setHeaderView(headerLayout);

        ListView bodyView = tableView.findViewById(R.id.list_view);
        datatableView.setBodyView(bodyView);

        datatableView.load(env);
    }
}
