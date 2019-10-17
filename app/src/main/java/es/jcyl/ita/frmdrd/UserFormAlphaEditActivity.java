package es.jcyl.ita.frmdrd;

import android.app.ActionBar;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.ViewPager;
import es.jcyl.ita.frmdrd.ui.form.Form;
import es.jcyl.ita.frmdrd.render.FormRenderer;
import es.jcyl.ita.frmdrd.util.DataUtils;

public class UserFormAlphaEditActivity extends FragmentActivity {
    protected static final Log LOGGER = LogFactory
            .getLog(UserFormAlphaEditActivity.class);

    protected Context context;
    protected Activity parentActivity;

    public static ActionMode actionMode;

    protected ActionBar actionBar;


    protected LinearLayout fieldsLayout;
    protected View separator;
    protected LinearLayout buttonsLayout;
    protected Button picturesButton;
    protected Button deleteButton;
    protected Button acceptButton;
    protected Button cancelButton;
    protected CheckBox deletePicturesCheckbox;

    protected LinearLayout containerLayout;

    protected ViewPager viewPager;


    protected Form form;

    protected String uuid;

    protected ContextThemeWrapper themeWrapper;

    public UserFormAlphaEditActivity() {
    }

    public UserFormAlphaEditActivity(final Context context, final Activity parentActivity) {
        this.context = context;
        this.parentActivity = parentActivity;
        this.actionMode = actionMode;
        this.themeWrapper = new ContextThemeWrapper(context,
                android.R.style.Theme_Holo_Dialog);
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = this.parentActivity;
        this.themeWrapper = new ContextThemeWrapper(context,
                android.R.style.Theme_Holo_Dialog);
        this.form = (Form)this.getIntent().getSerializableExtra("form");

        setContentView(R.layout.tool_alphaedit_finisher_fromxml);

        actionBar = getActionBar();
        if (actionBar != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
            getActionBar().setHomeButtonEnabled(true);
        }
        buttonsLayout = findViewById(R.id.tool_alphaeditxml_buttons);
        acceptButton = findViewById(R.id.tool_alphaeditxml_accept);
        cancelButton = findViewById(R.id.tool_alphaeditxml_cancel);

        if (form != null) {
            setTitle(form.getName());
            prepareForm();
        }
    }


    protected void prepareForm() {
        //containerLayout.setVisibility(View.VISIBLE);

        FormRenderer renderer = new FormRenderer();
        fieldsLayout = (LinearLayout) renderer.render(this, form);
        fieldsLayout.setVisibility(View.VISIBLE);
    }

    protected void prepareButtons() {
        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                close();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                close();
            }
        });
    }



    protected void closeWithMessage(final String error, final int length) {
        Toast.makeText(this, error, length).show();
        finish();
    }


    protected void close() {
        finish();
    }


    /**
     * Executor para eliminar la entidad.
     */


    private class PickDate implements DatePickerDialog.OnDateSetListener {
        private final Button dateButton;

        public PickDate(final Button dateButton) {
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
}