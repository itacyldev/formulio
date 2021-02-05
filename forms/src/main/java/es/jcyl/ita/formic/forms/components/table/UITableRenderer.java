package es.jcyl.ita.formic.forms.components.table;

import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import org.apache.commons.lang3.StringUtils;

import es.jcyl.ita.formic.forms.R;
import es.jcyl.ita.formic.forms.config.DevConsole;
import es.jcyl.ita.formic.forms.view.helpers.ViewHelper;
import es.jcyl.ita.formic.forms.view.render.AbstractGroupRenderer;
import es.jcyl.ita.formic.forms.view.render.RenderingEnv;
import es.jcyl.ita.formic.forms.view.widget.Widget;

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
 * @author Gustavo Río Briones (gustavo.rio@itacyl.es)
 */

public class UITableRenderer extends AbstractGroupRenderer<UITable, Widget<UITable>> {


    @Override
    protected void composeWidget(RenderingEnv env, Widget<UITable> widget) {
        UITable component = widget.getComponent();
        // add header views
        TableLayout tableView = widget.findViewById(R.id.table_layout);
        if (StringUtils.isBlank(component.getHeaderText())) {
            // remove default table row
            tableView.removeViewAt(0);
        }else {
            // get header row and configure
            TableRow row = ViewHelper.findViewAndSetId(widget, R.id.header_layout,
                    TableRow.class);

            // create header cells
            String[] splits = component.getHeaderText().split(",");
            for (String h : splits) {
                // inflate header cell from resource
                Widget headerLayout = ViewHelper.inflate(env.getViewContext(),
                        R.layout.widget_table_header, Widget.class);
                TextView headerCell = ViewHelper.findViewAndSetId(headerLayout, R.id.header_view, TextView.class);
                headerLayout.removeView(headerCell);
                headerCell.setText(h);
                row.addView(headerCell, new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT));
//                TableRow.LayoutParams params = (TableRow.LayoutParams) headerCell.getLayoutParams();
//                params.height="mat"

            }
        }

    }

    @Override
    public void addViews(RenderingEnv env, Widget<UITable> widget, View[] views) {
        TableLayout tableView = widget.findViewById(R.id.table_layout);
        UITable component = widget.getComponent();

        View row = null;
        int i = 0;
        for (View view : views) {
            // look for nested row widget
            if (view instanceof Widget) {
                row = ((Widget) view).getChildAt(0);
                ((Widget) view).removeView(row);
            } else {
                row = view;
            }
            if (!(row instanceof TableRow)) {
                DevConsole.warn(String.format("Adding view different that TableRow in table [%s], this will " +
                        "result in unexpected behaviour. All <table/> children must be nested in <row/>" +
                        "elements. File ${file}.", widget.getId()));
            }
            if (component.isBorder()) {
                row.setBackground(ContextCompat.getDrawable(widget.getContext(), R.drawable.border_row));
                if (i == views.length -1) {
                    row.setBackground(ContextCompat.getDrawable(widget.getContext(), R.drawable.border_last_row));
                }
            }
            tableView.addView(row);
            i++;

        }
    }

    @Override
    protected int getWidgetLayoutId(UITable component) {
        return R.layout.widget_table;
    }


}
