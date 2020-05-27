package es.jcyl.ita.frmdrd;

import android.Manifest;
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
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;


import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.mini2Dx.collections.CollectionUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import es.jcyl.ita.crtrepo.RepositoryFactory;
import es.jcyl.ita.frmdrd.config.Config;
import es.jcyl.ita.frmdrd.config.DevConsole;
import es.jcyl.ita.frmdrd.forms.FormController;
import es.jcyl.ita.frmdrd.project.Project;
import es.jcyl.ita.frmdrd.project.ProjectRepository;
import es.jcyl.ita.frmdrd.view.activities.FormListFragment;
import es.jcyl.ita.frmdrd.view.fragments.projects.ProjectListFragment;

import static es.jcyl.ita.frmdrd.config.DevConsole.warn;

public class MainActivity extends BaseActivity implements FormListFragment.OnListFragmentInteractionListener {

    protected SharedPreferences settings;

    private static final int PERMISSION_REQUEST = 1234;


    @Override
    protected void doOnCreate() {
        getApplication().getFilesDir();
        checkPermissions();
        setContentView(R.layout.activity_main);
        initialize();
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
                        Toast.makeText(MainActivity.this, getString(R.string.projects),
                                Toast.LENGTH_SHORT).show();
                        Fragment projectListFragment = new ProjectListFragment();
                        loadFragment(projectListFragment);
                        break;
                    case R.id.action_forms:
                        Toast.makeText(MainActivity.this, getString(R.string.forms),
                                Toast.LENGTH_SHORT).show();
                        Fragment formListFragment = new FormListFragment();
                        loadFragment(formListFragment);
                        break;
                }
                return true;
            }
        });

        settings = PreferenceManager
                .getDefaultSharedPreferences(this);
        Fragment projectListFragment = new ProjectListFragment();
        loadFragment(projectListFragment);
    }

    @Override
    public void onListFragmentInteraction(FormController form) {
        MainController.getInstance().getRouter().navigate(this, form.getId(), null);
    }

    protected void checkPermissions() {
        List permsList = new ArrayList();

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            permsList.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            permsList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if (permsList.size() > 0) {
            ActivityCompat.requestPermissions(this, (String[]) permsList
                            .toArray(new String[]{}),
                    PERMISSION_REQUEST);
        } else {
            doInitConfiguration();
        }
    }

    private void doInitConfiguration() {

//        String projectsFolder = getApplicationContext().getFilesDir() + "/projects";
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
            Toast.makeText(this, DevConsole.info("Opening project " + projects.get(0).getId()), Toast.LENGTH_LONG).show();
            try {
                config.readConfig(projects.get(0));
                debugConfig();

            } catch (Exception e) {
                DevConsole.error("Error while trying to open project.", e);
                Toast.makeText(this, "An error occurred while trying to read your projects. See console for details",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    private void debugConfig() {
        RepositoryFactory repoFactory = RepositoryFactory.getInstance();
        Set<String> repoIds = repoFactory.getRepoIds();
        DevConsole.info("Repos registered: " + repoIds);

    }

    @Override
    protected void setTheme() {
        if (currentTheme.equals("dark")) {
            setTheme(R.style.Theme_App_Dark);
        } else {
            setTheme(R.style.Theme_App_Light);
        }

        // change text of the menu item
        invalidateOptionsMenu();
    }
}
