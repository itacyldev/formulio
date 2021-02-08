package es.jcyl.ita.formic.forms.components.table;

import android.view.View;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import org.apache.commons.lang3.StringUtils;

import java.util.StringTokenizer;

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

public class UIRowRenderer extends AbstractGroupRenderer<UIRow, Widget<UIRow>> {

    private static final String DELIM = ",";
    public static final int NUM_100 = 100;

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
        Integer[] colspans = null;
        if (StringUtils.isNotBlank(component.getColspans())) {
            colspans = getColspanValues(component);
        }
        // handle cell weigthts
        float[] weigthts = null;
        if (StringUtils.isNotBlank(component.getWeights())) {
            weigthts = getWeigthValues(component, views.length);
        }else{
            weigthts = getEqualWeigthValues(component, views.length);
        }
        int i = 0;

        for (View view : views) {
            if (component.isBorder() && i != views.length - 1) {
                view.setBackground(ContextCompat.getDrawable(root.getContext(), R.drawable.border_cell));
            }
            rowView.addView(view);
            if (colspans != null && i < colspans.length) {
                TableRow.LayoutParams params = (TableRow.LayoutParams) view.getLayoutParams();
                params.span = colspans[i];
            }
            if (weigthts != null && i < weigthts.length) {
                TableRow.LayoutParams lp = new TableRow.LayoutParams();
                lp.weight = 1; //column weight
                lp = new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT, weigthts[i]);
                view.setLayoutParams(lp);
            }
            i++;
        }
    }

    private Integer[] getColspanValues(UIRow component) {
        Integer[] colspans = null;
        try {

            String[] splits = component.getColspans().split(",");
            colspans = new Integer[splits.length];
            int i = 0;
            for (String cs : splits) {
                colspans[i] = Integer.parseInt(cs);
                i++;
            }
        } catch (Exception e) {
            DevConsole.error(String.format("An error occurred while trying ot apply 'colspans' " +
                            "attribute in table [%s], row [%s].", component.getParent().getId(),
                    component.getId()));
            // ignore error and continue with the rendering without colspans
            return null;
        }
        return colspans;
    }

    private float[] getWeigthValues(UIRow component, int length) {
        float[] weigthts = null;
        try {
            StringTokenizer stringTokenizer = new StringTokenizer(component.getWeights(), DELIM);
            int i = 0;
            float total = 0;
            weigthts = new float[length];
            while (stringTokenizer.hasMoreElements() && i<length) {
                String token = stringTokenizer.nextToken();
                float weight = Float.parseFloat(token);
                weigthts[i] = weight;
                total = total + weight;
                i++;
            }

            if (total < 100 && length > i){
                for (int index = i; index< length; index++){
                    weigthts[index] = (NUM_100 - total)/(length - i);
                }
            }

            i = 0;
            for (float w : weigthts) {
                weigthts[i] = w/(total>100?total:100);
                i++;
            }
        } catch (Exception e) {
            DevConsole.error(String.format("An error occurred while trying ot apply 'weigthts' " +
                            "attribute in table [%s], row [%s].", component.getParent().getId(),
                    component.getId()));
            // ignore error and continue with the rendering without colspans
            return getEqualWeigthValues(component, length);
        }
        return weigthts;
    }

    private float[] getEqualWeigthValues(UIRow component, int length) {
        int i=0;
        float[] weigthts = new float[length];
        for (float w : weigthts) {
            weigthts[i] = 1 / (float) length;
            i++;
        }
        return weigthts;
    }
}
