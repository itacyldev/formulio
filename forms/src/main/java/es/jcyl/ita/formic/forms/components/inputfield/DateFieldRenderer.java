package es.jcyl.ita.formic.forms.components.inputfield;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TimePicker;

import org.apache.commons.lang3.StringUtils;
import org.mini2Dx.beanutils.ConvertUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import es.jcyl.ita.formic.forms.R;
import es.jcyl.ita.formic.forms.actions.events.Event;
import es.jcyl.ita.formic.forms.actions.events.UserEventInterceptor;
import es.jcyl.ita.formic.forms.components.StyleHolder;
import es.jcyl.ita.formic.forms.converters.CustomDateConverter;
import es.jcyl.ita.formic.forms.view.helpers.ViewHelper;
import es.jcyl.ita.formic.forms.view.render.InputTextRenderer;
import es.jcyl.ita.formic.forms.view.render.renderer.RenderingEnv;
import es.jcyl.ita.formic.forms.view.widget.InputWidget;

import static es.jcyl.ita.formic.forms.components.inputfield.UIField.TYPE.DATE;

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
        StyleHolder<Button> styleHolder = new ButtonStyleHolder(env.getAndroidContext());

        // configure input view elements
        Button today = ViewHelper.findViewAndSetId(widget, R.id.field_layout_today,
                Button.class);
        styleHolder.applyStyle(today);
        if ((Boolean) ConvertUtils.convert(widget.getComponent().isReadonly(env.getWidgetContext()), Boolean.class) || !widget.getComponent().hasTodayButton()) {
            today.setVisibility(View.GONE);
        }
        if (widget.getComponent().getType().equals(UIField.TYPE.DATETIME.name())) {
            today.setText(R.string.now);
        }

        Button input = widget.getInputView();
        styleHolder.applyStyle(input);

        final Calendar c = new GregorianCalendar();

        final DatePickerDialog.OnDateSetListener listener =
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(final DatePicker view, final int year,
                                          final int monthOfYear, final int dayOfMonth) {
                        view.updateDate(year, monthOfYear, dayOfMonth);
                        //final Calendar c = new GregorianCalendar();
                        c.set(year, monthOfYear, dayOfMonth);

                        if (widget.getComponent().getType().equals(UIField.TYPE.DATETIME.name())) {
                            int hour = c.get(Calendar.HOUR);
                            int minute = c.get(Calendar.MINUTE);
                            TimePickerDialog timePickerDialog = new TimePickerDialog(widget.getContext(), R.style.DialogStyle, new TimePickerDialog.OnTimeSetListener() {
                                @Override
                                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                    c.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                    c.set(Calendar.MINUTE, minute);
                                    //String strValue = (String) ConvertUtils.convert(c.getTime(), String.class);
                                    //input.setText(formatDate(strValue, widget.getComponent().getDatetimePattern(), widget));
                                    String strValue = (String) ConvertUtils.convert(new SimpleDateFormat(widget.getComponent().getDatetimePattern()).format(c.getTime()), String.class);
                                    input.setText(strValue);
                                }
                            }, hour, minute, false);
                            timePickerDialog.show();
                        }


                        //String strValue = (String) ConvertUtils.convert(c.getTime(), String.class);
                        //input.setText(formatDate(strValue, widget.getComponent().getType().equals(DATE.name()) ? widget.getComponent().getDatePattern() : widget.getComponent().getDatetimePattern(), widget));
                        String strValue = (String) ConvertUtils.convert(new SimpleDateFormat(widget.getComponent().getType().equals(DATE.name()) ? widget.getComponent().getDatePattern() : widget.getComponent().getDatetimePattern()).format(c.getTime()), String.class);
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
                Calendar c = new GregorianCalendar();
                if (input.getText() != null && StringUtils.isNotEmpty(input.getText())) {
                    CustomDateConverter cdc = new CustomDateConverter();
                    cdc.setPatterns(new String[]{widget.getComponent().getType().equals(DATE.name())?widget.getComponent().getDatePattern():widget.getComponent().getDatetimePattern()});
                    ConvertUtils.register(cdc, Calendar.class);

                    c = (Calendar) ConvertUtils.convert(input.getText(), Calendar.class);
                }

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
                //String strValue = (String) ConvertUtils.convert(new java.util.Date(), String.class);
                //input.setText(formatDate(strValue, widget.getComponent().getType().equals(DATE.name()) ? widget.getComponent().getDatePattern() : widget.getComponent().getDatetimePattern(), widget));
                String strValue = (String) ConvertUtils.convert(new SimpleDateFormat(widget.getComponent().getType().equals(DATE.name())?widget.getComponent().getDatePattern():widget.getComponent().getDatetimePattern()).format(new java.util.Date()), String.class);
                input.setText(strValue);
            }
        });

        setOnClickListenerResetButton(widget.getComponent(), input);
    }

    private void setOnClickListenerResetButton(UIField component, Button input) {
        ImageView resetButton = component.getResetButton();
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View arg0) {
                input.setText("");
            }
        });

    }

    protected void setValueInView(RenderingEnv env, InputWidget<UIField, Button> widget) {
        String value = getComponentValue(env, widget.getComponent(), String.class);
        widget.getConverter().setPattern(widget.getComponent().getPattern());
        widget.getConverter().setType(widget.getComponent().getType());
        widget.getConverter().setViewValue(widget.getInputView(), value);
        //widget.getConverter().setViewValue(widget.getInputView(), StringUtils.isNotEmpty(value) ? formatDate(value, null, widget) : value);
    }

    /*private String formatDate(String value, String pattern, InputWidget<UIField, Button> widget) {
        String formattedDate = "";
        Date date;

        if (StringUtils.isEmpty(pattern)) {
            pattern = widget.getComponent().getPattern();
            if (StringUtils.isEmpty(pattern)) {
                pattern = widget.getComponent().getType().equals(DATE.name()) ? widget.getComponent().getDatePattern() : widget.getComponent().getDatetimePattern();
            }
        }


        if (pattern.equals("unixepoch_s")){
            date = new Date(Long.parseLong(value.concat("000")));
        }else if (pattern.equals("unixepoch_m")){
            date = new Date(Long.parseLong(value));
        }else{
            date = (Date) ConvertUtils.convert(value, Date.class);
        }

        try {
            formattedDate = new SimpleDateFormat(widget.getComponent().getType().equals(DATE.name())?widget.getComponent().getDatePattern():widget.getComponent().getDatetimePattern()).format(date);
        } catch (Exception e) {
            DevConsole.error(String.format("An error occurred while trying to format the date [%s].", value));
            return value;
        }

        return formattedDate;
    }*/

    @Override
    protected <T> T handleNullValue(UIField component) {
        return (T) EMPTY_STRING;
    }

    @Override
    protected int getWidgetLayoutId(UIField component) {
        return R.layout.widget_date;
    }
}
