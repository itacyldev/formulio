package es.jcyl.ita.formic.forms.components.table;

import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import org.apache.commons.lang3.StringUtils;

import es.jcyl.ita.formic.forms.R;
import es.jcyl.ita.formic.forms.components.util.ComponentUtils;
import es.jcyl.ita.formic.forms.config.DevConsole;
import es.jcyl.ita.formic.forms.view.helpers.ViewHelper;
import es.jcyl.ita.formic.forms.view.render.AbstractGroupRenderer;
import es.jcyl.ita.formic.forms.view.render.renderer.RenderingEnv;
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

    public static final int NUM_100 = 100;


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

            // handle cell weigthts
            float[] weigthts = ComponentUtils.getWeigths(component.getWeights(), splits.length, component.getId(), null);

            int i=0;
            for (String h : splits) {
                // inflate header cell from resource
                Widget headerLayout = ViewHelper.inflate(env.getAndroidContext(),
                        R.layout.widget_table_header, Widget.class);
                TextView headerCell = ViewHelper.findViewAndSetId(headerLayout, R.id.header_view, TextView.class);
                headerLayout.removeView(headerCell);
                headerCell.setText(h);
                row.addView(headerCell, new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT));

                boolean isLastCell= i != splits.length - 1;

                setBorderRow(widget, component, row, false);

                setBorderCell(widget, component, headerCell, isLastCell);

                TableUtils.setLayoutParams(weigthts, i, headerCell);
                i++;
            }
        }
    }

    @Override
    public void addViews(RenderingEnv env, Widget<UITable> widget, Widget[] views) {
        TableLayout tableView = widget.findViewById(R.id.table_layout);
        UITable component = widget.getComponent();

        View row = null;
        int i = 0;

        boolean hasChildren = false;
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
            boolean isLastRow = i == views.length -1;
            setBorderRow(widget, component, row, isLastRow);
            if (((TableRow) row).getChildCount() > 0){
                hasChildren = true;
            }
            tableView.addView(row);
            i++;
        }

        if (!hasChildren){
            tableView.setStretchAllColumns(false);
            DevConsole.warn(String.format("\n" +
                    "Tablerow [%s] does not contain any views to add", widget.getId()));
        }
    }

    private void setBorderRow(Widget<UITable> widget, UITable component, View row, boolean isLastRow) {
        if (component.isBorder()) {
            row.setBackground(ContextCompat.getDrawable(widget.getContext(), R.drawable.border_row));
            if (isLastRow) {
                row.setBackground(ContextCompat.getDrawable(widget.getContext(), R.drawable.border_last_row));
            }
        }
    }

    private void setBorderCell(Widget<UITable> widget, UITable component, TextView headerCell, boolean isLastCell) {
        if (component.isBorder() && isLastCell) {
            headerCell.setBackground(ContextCompat.getDrawable(widget.getContext(), R.drawable.border_cell));
        }
    }

    @Override
    protected int getWidgetLayoutId(UITable component) {
        return R.layout.widget_table;
    }

}
