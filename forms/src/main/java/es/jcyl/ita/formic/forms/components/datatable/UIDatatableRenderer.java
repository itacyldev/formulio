package es.jcyl.ita.formic.forms.components.datatable;

import android.content.Context;
import android.widget.LinearLayout;
import android.widget.ListView;

import androidx.annotation.NonNull;

import es.jcyl.ita.formic.forms.R;
import es.jcyl.ita.formic.forms.view.render.AbstractGroupRenderer;
import es.jcyl.ita.formic.forms.view.render.renderer.RenderingEnv;

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

public class UIDatatableRenderer extends AbstractGroupRenderer<UIDatatable, DatatableWidget> {

    private final static float ROW_HEIGHT_DP = 38.75f;

    @Override
    protected int getWidgetLayoutId(UIDatatable datatable) {
        return R.layout.widget_datatable;
    }

    @Override
    protected void composeWidget(RenderingEnv env, DatatableWidget widget) {
        // Do nothing
    }

    @Override
    protected DatatableWidget createWidget(RenderingEnv env, UIDatatable component) {
        DatatableWidget widget = super.createWidget(env, component);
        widget.setTag(getWidgetViewTag(component));
        return widget;
    }

    @Override
    protected void setupWidget(RenderingEnv env, DatatableWidget widget) {
        super.setupWidget(env, widget);

        LinearLayout tableView = widget.findViewById(R.id.list_layout);
        LinearLayout headerLayout = tableView.findViewById(R.id.list_layout_headers);
        widget.setHeaderView(headerLayout);

        ListView bodyView = tableView.findViewById(R.id.list_view);
        setLayoutParams(bodyView, env.getAndroidContext(), widget.getComponent());
        widget.setBodyView(bodyView);

        widget.load(env);
    }

    /**
     * Configure datatable layout setting the dimension parameters.
     * @param tableView
     * @param context: android context
     * @param component: ui component
     */
    private void setLayoutParams(@NonNull ListView tableView,
                                 @NonNull Context context, @NonNull UIDatatable component) {
        final int layout_width = ListView.LayoutParams.MATCH_PARENT;
        final int layout_height;
        if (component.getNumVisibleRows() < 0) {
            layout_height = ListView.LayoutParams.MATCH_PARENT;
        } else {
            layout_height = (int) (context.getResources().getDisplayMetrics().density *
                    ROW_HEIGHT_DP *
                    component.getNumVisibleRows());
        }
        tableView.setLayoutParams(new LinearLayout.LayoutParams(layout_width, layout_height));
    }

}
