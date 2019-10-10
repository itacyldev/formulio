package es.jcyl.ita.frmdrd.renderer;

import android.app.DatePickerDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import es.jcyl.ita.frmdrd.util.DataUtils;

public class OnChangeFieldInterceptor implements TextWatcher,
        CompoundButton.OnCheckedChangeListener, DatePickerDialog.OnDateSetListener {

    private Button dateButton;


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        String value = s.toString();



    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

    }

    public void setDateButton(Button dateButton) {
        this.dateButton = dateButton;
    }

    @Override
    public void onDateSet(final DatePicker view, final int year,
                          final int monthOfYear, final int dayOfMonth) {
        view.updateDate(year, monthOfYear, dayOfMonth);
        final Calendar c = new GregorianCalendar();
        c.set(year, monthOfYear, dayOfMonth);

        final Date dateValue = c.getTime();
        dateButton.setText(DataUtils.DATE_FORMAT.format(dateValue));
    }
}
