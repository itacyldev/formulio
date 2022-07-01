package es.jcyl.ita.formic.forms.view.activities;

import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.HashMap;
import java.util.Map;

import es.jcyl.ita.formic.forms.MainController;
import es.jcyl.ita.formic.forms.R;
import es.jcyl.ita.formic.forms.actions.ActionContext;
import es.jcyl.ita.formic.forms.actions.ActionType;
import es.jcyl.ita.formic.forms.actions.JobActionHandler;
import es.jcyl.ita.formic.forms.actions.UserAction;
import es.jcyl.ita.formic.forms.config.DevConsole;
import es.jcyl.ita.formic.jayjobs.jobs.JobFacade;
import es.jcyl.ita.formic.jayjobs.jobs.config.JobConfigRepo;

public abstract class BaseActivity extends AppCompatActivity  {

    protected SharedPreferences sharedPreferences;
    protected String currentTheme;
    protected int logLevel;
    protected String currentWorkspace;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        currentTheme = sharedPreferences.getString("current_theme", "light");
        currentWorkspace = sharedPreferences.getString("current_workspace", getExternalFilesDir(null).getAbsolutePath() + "/projects");
        logLevel = sharedPreferences.getInt("log_level", Log.DEBUG);
        setTheme();
        setLogLevel();
        doOnCreate();
    }

    protected abstract void doOnCreate();

    protected void setTheme() {
        //currentTheme = sharedPreferences.getString("current_theme", "light");
        if (currentTheme.equals("light")) {
            setTheme(R.style.FormudruidLight_NoActionBar);
        } else {
            setTheme(R.style.FormudruidDark_NoActionBar);
        }
    }

    protected void setLogLevel() {
        DevConsole.setLevel(logLevel);
    }


    @Override
    protected void onResume() {
        super.onResume();
        String theme = sharedPreferences.getString("current_theme", "light");
        if (currentTheme != theme)
            recreate();
    }


    protected void switchTheme() {

        if (currentTheme.equals("light")) {
            setTheme(R.style.FormudruidDark);
            sharedPreferences.edit().putString("current_theme", "dark").apply();
        } else {
            setTheme(R.style.FormudruidLight);
            sharedPreferences.edit().putString("current_theme", "light").apply();
        }

        if (currentTheme.equals("")) {
            sharedPreferences.edit().putString("current_theme", "dark")
                    .apply();
        }
        recreate();

        invalidateOptionsMenu();
    }

    protected void synchronization(){
        MainController mc = MainController.getInstance();

        UserAction userAction = new UserAction(ActionType.JOB);
        Map<String, Object> params = new HashMap<>();
        String JOB_ID = "myMockedJob";
        params.put("jobId", JOB_ID);
        userAction.setParams(params);

        // act - execute action
        JobActionHandler handler = new JobActionHandler(mc, mc.getRouter());
        handler.handle(new ActionContext(mc.getViewController(), this), userAction);


        // create facade and related repositories
        JobFacade facade = new JobFacade();
        JobConfigRepo repo = new JobConfigRepo();
        facade.setJobConfigRepo(repo);

    }


    public void lockOrientation() {
        final int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
            lockOrientationJellyBean();
        } else {
            lockOrientationAllVersions();
        }
    }

    public void unlockOrientation() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
    }

    @Override
    public void setRequestedOrientation(final int requestedOrientation) {
        super.setRequestedOrientation(requestedOrientation);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void lockOrientationJellyBean() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
    }

    private void lockOrientationAllVersions() {
        final int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    protected void setToolbar(String title) {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_navigate_before_white_24dp));
        toolbar.setTitle(title);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                finish();
            }
        });
    }

    public void loadFragment(Fragment fragment) {
        // create a FragmentManager
        FragmentManager fm = getSupportFragmentManager();
        // create a FragmentTransaction to begin the transaction and replace the Fragment
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        // replace the FrameLayout with new Fragment
        fragmentTransaction.replace(R.id.fragment_content_main, fragment);
        fragmentTransaction.commit(); // save the changes
    }

}
