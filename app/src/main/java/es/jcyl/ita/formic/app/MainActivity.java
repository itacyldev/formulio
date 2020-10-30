package es.jcyl.ita.formic.app;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.mini2Dx.collections.CollectionUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import es.jcyl.ita.formic.R;
import es.jcyl.ita.formic.app.dev.DevConsoleActivity;
import es.jcyl.ita.formic.app.projects.ProjectListFragment;
import es.jcyl.ita.formic.forms.MainController;
import es.jcyl.ita.formic.forms.actions.UserAction;
import es.jcyl.ita.formic.forms.config.Config;
import es.jcyl.ita.formic.forms.config.DevConsole;
import es.jcyl.ita.formic.forms.controllers.FormController;
import es.jcyl.ita.formic.forms.project.Project;
import es.jcyl.ita.formic.forms.project.ProjectRepository;
import es.jcyl.ita.formic.forms.view.activities.BaseActivity;
import es.jcyl.ita.formic.forms.view.activities.FormListFragment;

import static es.jcyl.ita.formic.forms.config.DevConsole.warn;

public class MainActivity extends BaseActivity implements FormListFragment.OnListFragmentInteractionListener {

    protected SharedPreferences settings;

    private static final int PERMISSION_REQUEST = 1234;

    @Override
    protected void doOnCreate() {
        getApplication().getFilesDir();
        setContentView(R.layout.activity_main);
        checkPermissions();
        checkDeviceFeatures();
    }

    private void checkDeviceFeatures() {
        Context context = getApplicationContext();
        /** Check if this device has a camera */
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            // TODO: complete
            DevConsole.error("Camera not available in this device.");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.action_dark_theme);
        if (currentTheme.equals("dark")) {
            item.setTitle(R.string.action_light_theme);
        } else {
            item.setTitle(R.string.action_dark_theme);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        if (id == R.id.action_dark_theme) {
            switchTheme();
            return true;
        }

        if (id == R.id.action_dev_console) {
            openDevConsole();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void openDevConsole() {
        Intent intent = new Intent(this, DevConsoleActivity.class);
        startActivity(intent);
    }

    private void loadFragment(Fragment fragment) {
        // create a FragmentManager
        FragmentManager fm = getSupportFragmentManager();
        // create a FragmentTransaction to begin the transaction and replace the Fragment
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        // replace the FrameLayout with new Fragment
        fragmentTransaction.replace(R.id.fragment_content_main, fragment);
        fragmentTransaction.commit(); // save the changes
    }

    private void initialize() {
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_projects:
                        loadFragment(ProjectListFragment.newInstance(
                                Config.getInstance().getProjectRepo()));
                        break;
                    case R.id.action_forms:
                        loadFragment(new FormListFragment());
                        break;
                }
                return true;
            }
        });

        settings = PreferenceManager
                .getDefaultSharedPreferences(this);

        loadFragment(new FormListFragment());
    }

    @Override
    public void onListFragmentInteraction(FormController form) {
        MainController.getInstance().getRouter().navigate(UserAction.navigate(this, null, form.getId()));
    }

    protected void checkPermissions() {
        List<String> permsList = new ArrayList<>();

        String[] permissions = new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
        };
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this,
                    permission)
                    != PackageManager.PERMISSION_GRANTED) {
                permsList.add(permission);
            }
        }

        if (permsList.size() > 0) {
            ActivityCompat.requestPermissions(this, permsList
                    .toArray(new String[]{}), PERMISSION_REQUEST);
        } else {
            doInitConfiguration();
        }
    }

    private void doInitConfiguration() {

        //String projectsFolder = getApplicationContext().getFilesDir() + "/projects";
        String projectsFolder = Environment.getExternalStorageDirectory().getAbsolutePath() + "/projects";


        File f = new File(projectsFolder);
        if (!f.exists()) {
            f.mkdir();
        }
        Config config = Config.init(projectsFolder);

        ProjectRepository projectRepo = config.getProjectRepo();
        List<Project> projects = projectRepo.listAll();
        if (CollectionUtils.isEmpty(projects)) {
            Toast.makeText(this, warn("No projects found!!. Create a folder under " + projectsFolder),
                    Toast.LENGTH_LONG).show();
        } else {
            // TODO: extract Project View Helper to FORMIC-27
            Project prj = projects.get(0); // TODO: store in shareSettings the last open project FORMIC-27
            Toast.makeText(this,
                    DevConsole.info(this.getString(R.string.project_opening_init,
                            (String) prj.getId())),
                    Toast.LENGTH_LONG).show();
            try {
                Config.getInstance().setCurrentProject(prj);
                Toast.makeText(this,
                        DevConsole.info(this.getString(R.string.project_opening_finish, (String) prj.getId())),
                        Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                Toast.makeText(this,
                        DevConsole.info(this.getString(R.string.project_opening_error, (String) prj.getId())),
                        Toast.LENGTH_LONG).show();
            }
        }
        initialize();
    }

    @Override
    protected void setTheme() {
        if (currentTheme.equals("dark")) {
            setTheme(R.style.FormudruidDark);
        } else {
            setTheme(R.style.FormudruidLight);
        }

        // change text of the menu item
        invalidateOptionsMenu();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST: {
                if (grantResults.length > 0) {
                    boolean allAcepted = true;
                    for (int result : grantResults) {
                        if (result != PackageManager.PERMISSION_GRANTED) {
                            allAcepted = false;
                            break;
                        }
                    }
                    if (allAcepted) {
                        doInitConfiguration();
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder
                                (this);
                        builder.setTitle(R.string.permissions);
                        builder.setMessage(R.string.mustacceptallpermits)
                                .setPositiveButton(R.string.accept, new
                                        DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.dismiss();
                                                finish();
                                            }
                                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                }
            }
        }
    }

}
