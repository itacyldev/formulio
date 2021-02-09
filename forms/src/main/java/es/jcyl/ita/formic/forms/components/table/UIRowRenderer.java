package es.jcyl.ita.formic.forms.components.table;

import android.view.View;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import org.apache.commons.lang3.StringUtils;

import es.jcyl.ita.formic.forms.R;
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

public class UIRowRenderer extends AbstractGroupRenderer<UIRow, Widget<UIRow>> {

    @Override
    protected int getWidgetLayoutId(UIRow component) {
        return R.layout.widget_row;
    }

    @Override
    protected void composeWidget(RenderingEnv env, Widget<UIRow> widget) {
    }

    @Override
    public void addViews(RenderingEnv env, Widget<UIRow> root, View[] views) {
        TableRow rowView = root.findViewById(R.id.row_layout);
        UIRow component = root.getComponent();

        // set label if the attribute is set
        if(StringUtils.isNotBlank(component.getLabel())){
            TextView label = ViewHelper.findViewAndSetId(root, R.id.label_view,
                    TextView.class);
            label.setText(component.getLabel());
            root.removeView(label);
            rowView.addView(label);
        }

        // handle cell colspans
        Integer[] colspans = getColspans(component);
        float[] weigthts = getWeigths(views, component);

        int i = 0;
        for (View view : views) {
            if ((((UITable) component.getParent()).isBorder()) && i != views.length - 1) {
                view.setBackground(ContextCompat.getDrawable(root.getContext(), R.drawable.border_cell));
            }
            rowView.addView(view);

            if (colspans != null && i < colspans.length) {
                TableRow.LayoutParams params = (TableRow.LayoutParams) view.getLayoutParams();
                params.span = colspans[i];
            }

            TableUtils.setLayoutParams(weigthts, i, view);

            i++;
        }
    }

    private Integer[] getColspans(UIRow component) {
        Integer[] colspans = null;
        if (StringUtils.isNotBlank(component.getColspans())) {
            colspans = TableUtils.getColspanValues(component);
        }
        return colspans;
    }

    private float[] getWeigths(View[] views, UIRow component) {
        // handle cell weigthts
        return TableUtils.getWeigths(StringUtils.isNotBlank(component.getWeights())?component.getWeights():((UITable) component.getParent()).getWeights(), views.length, component.getParent().getId(), component.getId());
    }


}
