package es.jcyl.ita.frmdrd;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.ContextThemeWrapper;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import androidx.fragment.app.FragmentActivity;
import es.jcyl.ita.frmdrd.context.impl.BasicContext;
import es.jcyl.ita.frmdrd.lifecycle.Lifecycle;

public class UserFormAlphaEditActivity extends FragmentActivity {
    protected static final Log LOGGER = LogFactory
            .getLog(UserFormAlphaEditActivity.class);

    Lifecycle lifecycle;

    protected Context context;
    protected Activity parentActivity;

    public static ActionMode actionMode;

    protected LinearLayout fieldsLayout;

    protected String formId;

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
        setContentView(R.layout.tool_alphaedit_finisher_fromxml);
        this.context = this.parentActivity;
        this.themeWrapper = new ContextThemeWrapper(context,
                android.R.style.Theme_Holo_Dialog);

        this.formId = this.getIntent().getStringExtra("form");

        if (StringUtils.isNotEmpty(formId)) {
            lifecycle = new Lifecycle(formId);
            BasicContext lifecycleContext = new BasicContext("lifecycle");
            lifecycleContext.put("activity", this);
            lifecycle.doExecute(lifecycleContext);
        }

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

}