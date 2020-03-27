package es.jcyl.ita.frmdrd.ui.components.inputfield;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import es.jcyl.ita.frmdrd.R;
import es.jcyl.ita.frmdrd.interceptors.OnChangeFieldInterceptor;
import es.jcyl.ita.frmdrd.render.BaseRenderer;
import es.jcyl.ita.frmdrd.ui.components.UIComponent;
import es.jcyl.ita.frmdrd.util.DataUtils;
import es.jcyl.ita.frmdrd.render.ExecEnvironment;

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

public class DateFieldRenderer extends BaseRenderer {

    @Override
    protected View createBaseView(Context viewContext, ExecEnvironment env, UIComponent component) {
        LinearLayout linearLayout = (LinearLayout) View.inflate(viewContext,
                R.layout.tool_alphaedit_date, null);
        return linearLayout;
    }

    @Override
    protected void setupView(View baseView, ExecEnvironment env, UIComponent component) {
        final TextView fieldLabel = baseView
                .findViewById(R.id.field_layout_name);
        final Button input = baseView
                .findViewById(R.id.field_layout_value);
        final Button today = baseView
                .findViewById(R.id.field_layout_today);
        final ImageView resetButton = baseView
                .findViewById(R.id.field_layout_x);

        fieldLabel.setText(component.getLabel());

        final DatePickerDialog.OnDateSetListener listener =
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(final DatePicker view, final int year,
                                          final int monthOfYear, final int dayOfMonth) {
                        view.updateDate(year, monthOfYear, dayOfMonth);
                        final Calendar c = new GregorianCalendar();
                        c.set(year, monthOfYear, dayOfMonth);

                        final Date dateValue = c.getTime();
                        input.setTag(component.getViewId());
                        String strValue = convert(getValue(component, env));
                        input.setText(strValue);

                        OnChangeFieldInterceptor interceptor = env.getChangeInterceptor();
                        interceptor.onChange(component);
                    }
                };

        input.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View arg0) {
                final Calendar c = new GregorianCalendar();
                final Dialog dateDialog = new DatePickerDialog(baseView.getContext(),
                        listener, c.get(Calendar.YEAR), c
                        .get(Calendar.MONTH), c
                        .get(Calendar.DAY_OF_MONTH));
                dateDialog.show();
            }
        });

        today.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View arg0) {
                final Date dateToday = new Date();
                input.setText("" + DataUtils.DATE_FORMAT.format(dateToday));
            }
        });

    }

    private String convert(Object value) {
        return (value == null) ? "" : DataUtils.DATE_FORMAT.format(value);
    }
}
