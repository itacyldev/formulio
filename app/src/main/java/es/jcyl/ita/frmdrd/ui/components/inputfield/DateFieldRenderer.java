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

import org.mini2Dx.beanutils.ConvertUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import es.jcyl.ita.frmdrd.R;
import es.jcyl.ita.frmdrd.actions.ActionType;
import es.jcyl.ita.frmdrd.actions.UserAction;
import es.jcyl.ita.frmdrd.actions.interceptors.ViewUserActionInterceptor;
import es.jcyl.ita.frmdrd.ui.components.UIComponent;
import es.jcyl.ita.frmdrd.ui.components.UIField;
import es.jcyl.ita.frmdrd.view.InputFieldView;
import es.jcyl.ita.frmdrd.view.ViewHelper;
import es.jcyl.ita.frmdrd.view.render.RenderingEnv;
import es.jcyl.ita.frmdrd.view.render.FieldRenderer;

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

public class DateFieldRenderer extends FieldRenderer {

    @Override
    protected View createBaseView(Context viewContext, RenderingEnv env, UIField component) {
        LinearLayout baseView = (LinearLayout) View.inflate(viewContext,
                R.layout.tool_alphaedit_date, null);
        return createInputFieldView(viewContext, baseView, component);
    }

    @Override
    protected void setupView(View baseView, RenderingEnv env, UIField component) {

        TextView fieldLabel = ViewHelper.findViewAndSetId(baseView, R.id.field_layout_name,
                TextView.class);
        final Button input = baseView
                .findViewById(R.id.field_layout_value);
        input.setTag(getInputTag(component));
        // get component value and set in view
        String strValue = getValue(component, env, String.class);
        input.setText(strValue);
        input.setEnabled(!component.isReadOnly());
        ((InputFieldView) baseView).setInputView(input);

        Button today = ViewHelper.findViewAndSetId(baseView, R.id.field_layout_today,
                Button.class);
        ImageView resetButton = ViewHelper.findViewAndSetId(baseView, R.id.field_layout_x,
                ImageView.class);
        fieldLabel.setText(component.getLabel());

        final DatePickerDialog.OnDateSetListener listener =
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(final DatePicker view, final int year,
                                          final int monthOfYear, final int dayOfMonth) {
                        view.updateDate(year, monthOfYear, dayOfMonth);
                        final Calendar c = new GregorianCalendar();
                        c.set(year, monthOfYear, dayOfMonth);

                        String strValue = (String) ConvertUtils.convert(c.getTime(), String.class);
                        input.setText(strValue);

                        ViewUserActionInterceptor interceptor = env.getUserActionInterceptor();
                        if (interceptor != null) {
                            interceptor.doAction(new UserAction(component, ActionType.INPUT_CHANGE));
                        }
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
                // set now
                String strValue = (String) ConvertUtils.convert(new Date(), String.class);
                input.setText(strValue);
            }
        });

    }

    @Override
    protected <T> T handleNullValue(Object value) {
        return (T) EMPTY_STRING;
    }
}
