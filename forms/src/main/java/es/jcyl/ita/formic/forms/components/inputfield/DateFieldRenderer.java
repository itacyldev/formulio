package es.jcyl.ita.formic.forms.components.inputfield;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;

import org.apache.commons.lang3.StringUtils;
import org.mini2Dx.beanutils.ConvertUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import es.jcyl.ita.formic.forms.R;
import es.jcyl.ita.formic.forms.actions.UserAction;
import es.jcyl.ita.formic.forms.actions.events.Event;
import es.jcyl.ita.formic.forms.actions.events.UserEventInterceptor;
import es.jcyl.ita.formic.forms.components.StyleHolder;
import es.jcyl.ita.formic.forms.view.helpers.ViewHelper;
import es.jcyl.ita.formic.forms.view.render.InputTextRenderer;
import es.jcyl.ita.formic.forms.view.render.RenderingEnv;
import es.jcyl.ita.formic.forms.view.widget.InputWidget;

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

public class DateFieldRenderer extends InputTextRenderer<UIField, Button> {

    @Override
    protected void composeInputView(RenderingEnv env, InputWidget<UIField, Button> widget) {
        StyleHolder<Button> styleHolder = new ButtonStyleHolder(env.getViewContext());

        // configure input view elements
        Button today = ViewHelper.findViewAndSetId(widget, R.id.field_layout_today,
                Button.class);
        styleHolder.applyStyle(today);
        if ((Boolean) ConvertUtils.convert(widget.getComponent().isReadOnly(env.getContext()), Boolean.class) || !widget.getComponent().hasTodayButton()) {
            today.setVisibility(View.GONE);
        }

        ImageView resetButton = ViewHelper.findViewAndSetId(widget, R.id.field_layout_x,
                ImageView.class);
        if ((Boolean) ConvertUtils.convert(widget.getComponent().isReadOnly(env.getContext()), Boolean.class) || !widget.getComponent().hasDeleteButton()) {
            resetButton.setVisibility(View.GONE);
        }

        Button input = widget.getInputView();
        styleHolder.applyStyle(input);

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

                        UserEventInterceptor interceptor = env.getUserActionInterceptor();
                        if (interceptor != null) {
                            interceptor.notify(Event.inputChange(widget));
                        }
                    }
                };

        input.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View arg0) {
                final Calendar c = new GregorianCalendar();
                final Dialog dateDialog = new DatePickerDialog(widget.getContext(),
                        R.style.DialogStyle,
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

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View arg0) {
                input.setText("");
            }
        });

        setVisibiltyResetButtonLayout(StringUtils.isNotBlank(widget.getComponent().getLabel()), resetButton);
    }

    @Override
    protected <T> T handleNullValue(UIField component) {
        return (T) EMPTY_STRING;
    }

    @Override
    protected int getWidgetLayoutId(UIField component) {
        return R.layout.widget_date;
    }
}
