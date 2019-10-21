package es.jcyl.ita.frmdrd.render;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.apache.commons.lang.StringUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import es.jcyl.ita.frmdrd.R;
import es.jcyl.ita.frmdrd.ui.form.UIField;
import es.jcyl.ita.frmdrd.ui.form.UIComponent;
import es.jcyl.ita.frmdrd.util.DataUtils;

public class DateFieldRenderer extends AbstractFieldRenderer {

    public DateFieldRenderer() {
        super();
    }

    @Override
    public void render(Context context, UIComponent field, ViewGroup parent) {
        String renderCondition = field.getRenderCondition();

        boolean render = true;
        if (StringUtils.isNotEmpty(renderCondition)) {
            render = this.validateCondition(renderCondition);
        }

        if (render) {
            LinearLayout linearLayout = (LinearLayout) View.inflate(context,
                    R.layout.tool_alphaedit_date, null);

            final TextView fieldLabel = linearLayout
                    .findViewById(R.id.field_layout_name);
            final Button input = linearLayout
                    .findViewById(R.id.field_layout_value);
            final Button today = linearLayout
                    .findViewById(R.id.field_layout_today);
            final ImageView resetButton = linearLayout
                    .findViewById(R.id.field_layout_x);

            fieldLabel.setText(field.getLabel());

            DatePickerDialog.OnDateSetListener listener =
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(final DatePicker view, final int year,
                                              final int monthOfYear, final int dayOfMonth) {
                            view.updateDate(year, monthOfYear, dayOfMonth);
                            final Calendar c = new GregorianCalendar();
                            c.set(year, monthOfYear, dayOfMonth);

                            final Date dateValue = c.getTime();
                            input.setText(DataUtils.DATE_FORMAT.format(dateValue));

                            onChangeInterceptor.onChange((UIField) field,
                                    dateValue);
                        }
                    };

            input.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View arg0) {
                    final Calendar c = new GregorianCalendar();
                    final Dialog dateDialog = new DatePickerDialog(context,
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

            parent.addView(linearLayout);
        }
    }

    @Override
    public void render(int viewId, UIComponent field) {

    }
}
