package es.jcyl.ita.formic.app;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.mini2Dx.collections.CollectionUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import es.jcyl.ita.formic.R;
import es.jcyl.ita.formic.app.about.AboutActivity;
import es.jcyl.ita.formic.app.dev.DevConsoleActivity;
import es.jcyl.ita.formic.app.dialog.ProjectDialog;
import es.jcyl.ita.formic.app.jobs.JobProgressListener;
import es.jcyl.ita.formic.app.projectimport.ImporterUtils;
import es.jcyl.ita.formic.app.projects.ProjectListFragment;
import es.jcyl.ita.formic.app.settings.SettingsActivity;
import es.jcyl.ita.formic.forms.App;
import es.jcyl.ita.formic.forms.MainController;
import es.jcyl.ita.formic.forms.StorageContentManager;
import es.jcyl.ita.formic.forms.actions.UserAction;
import es.jcyl.ita.formic.forms.config.DevConsole;
import es.jcyl.ita.formic.forms.controllers.ViewController;
import es.jcyl.ita.formic.forms.project.Project;
import es.jcyl.ita.formic.forms.project.ProjectRepository;
import es.jcyl.ita.formic.forms.util.FileUtils;
import es.jcyl.ita.formic.forms.view.UserMessagesHelper;
import es.jcyl.ita.formic.forms.view.activities.BaseActivity;
import es.jcyl.ita.formic.forms.view.activities.FormListFragment;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static es.jcyl.ita.formic.forms.config.DevConsole.warn;

public class MainActivity extends BaseActivity implements FormListFragment.OnListFragmentInteractionListener {

    protected SharedPreferences settings;

    private static final int PERMISSION_REQUEST = 1234;
    private static final int RQS_OPEN_DOCUMENT_TREE = 2;
    private static final int PERMISSION_STORAGE_REQUEST = 5708463;
    private static final int PROJECT_IMPORT_FILE_SELECT = 725353137;

    private static final String PROJECT_IMPORT_EXTENSION = "FRMD";

    @Override
    protected void doOnCreate() {
        getApplication().getFilesDir();
        setContentView(R.layout.activity_main);
        //checkPermissions();
        checkStoragePermission();
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
            showSettings();
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

        if (id == R.id.action_about) {
            showAbout();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void openDevConsole() {
        Intent intent = new Intent(this, DevConsoleActivity.class);
        startActivity(intent);
    }

    protected final void showAbout() {
        final Intent intent = new Intent(this, AboutActivity.class);
        startActivity(intent);
    }

    protected final void showSettings() {
        final Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    private void initialize() {
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        FloatingActionButton import_project = findViewById(es.jcyl.ita.formic.forms.R.id.import_project);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_projects:
                        loadFragment(ProjectListFragment.newInstance(
                                App.getInstance().getProjectRepo()));
                        break;
                    case R.id.action_forms:
                        loadFragment(new FormListFragment());
                        break;
                    default:
                        break;
                }
                return true;
            }
        });

        import_project.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProjectDialog projectDialog = new ProjectDialog
                        (MainActivity.this, currentTheme);
                projectDialog.build().show();
                //openProjectDialog();
            }
        });

        settings = PreferenceManager
                .getDefaultSharedPreferences(this);

        loadFragment(new FormListFragment());
    }

    @Override
    public void onListFragmentInteraction(ViewController form) {
        MainController.getInstance().getRouter().navigate(this,
                UserAction.navigate(form.getId()));
    }

    private void checkStoragePermission() {

        boolean requestStorage = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Uri treeUri = StorageContentManager.getExternalPrimaryStoragePathUri(this);
            if (treeUri != null) {
                currentWorkspace = FileUtils.getPath(this, treeUri);
            } else {
                requestStorage = true;
            }

        } else {
            int result = ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE);
            int result1 = ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE);
            int result2 = ContextCompat.checkSelfPermission(this, CAMERA);
            requestStorage =
                    !(result == PackageManager.PERMISSION_GRANTED || result1 == PackageManager.PERMISSION_GRANTED || result2 == PackageManager.PERMISSION_GRANTED);
        }

        if (requestStorage) {
            warnStoragePermission();
        } else {
            checkPermissions();
        }

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

    private void warnStoragePermission() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, es.jcyl.ita.formic.forms.R.style.DialogStyle);
        builder.setMessage(R.string.specificDirectoryAccessPermission).setPositiveButton(R.string.close, new
                DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                requestStoragePermission();
            }
        });

        builder.create().show();
    }

    private void requestStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
            startActivityForResult(intent, RQS_OPEN_DOCUMENT_TREE);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{READ_EXTERNAL_STORAGE,
                            WRITE_EXTERNAL_STORAGE, CAMERA},
                    RQS_OPEN_DOCUMENT_TREE);
        }
    }

    protected void doInitConfiguration() {

        String projectsFolder = currentWorkspace;

        try {
            FileUtils.copyDirectory(this, Environment.getExternalStorageDirectory()+"/projects", projectsFolder);
        } catch (IOException e) {
            e.printStackTrace();
        }

        File f = new File(projectsFolder);
        if (!f.exists()) {
            f.mkdir();
        }
        // initilize formic configuration
        App app = App.init(this, projectsFolder);

        ProjectRepository projectRepo = app.getProjectRepo();
        List<Project> projects = projectRepo.listAll();
        if (CollectionUtils.isEmpty(projects)) {
            UserMessagesHelper.toast(this, warn("No projects found!!. Create a folder under " + projectsFolder), Snackbar.LENGTH_LONG);
        } else {
            // TODO: extract Project View Helper to FORMIC-27
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            String projectName = sharedPreferences.getString("projectName","");
            Project prj = projects.get(0);
            if (projectName != null) {
                for (Project project : projects) {
                    if (project.getName().equals(projectName)) {
                        prj = project;
                    }
                }
            }
            DevConsole.setLogFileName(projectsFolder, (String) prj.getId());

            UserMessagesHelper.toast(this, DevConsole.info(this.getString(R.string.project_opening_init,
                    (String) prj.getId())), Toast.LENGTH_LONG);
            try {
                App.getInstance().setCurrentProject(prj);
                UserMessagesHelper.toast(this,
                        DevConsole.info(this.getString(R.string.project_opening_finish, (String) prj.getId())),
                        Toast.LENGTH_LONG);
            } catch (Exception e) {
                UserMessagesHelper.toast(this, DevConsole.info(this.getString(R.string.project_opening_error, (String) prj.getId())),
                        Toast.LENGTH_LONG);
            }
            App.getInstance().setJobListener(new JobProgressListener());
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
        if (requestCode == PERMISSION_REQUEST) {
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
        } else if (requestCode == RQS_OPEN_DOCUMENT_TREE) {
            if (grantResults.length > 0) {
                boolean READ_EXTERNAL_STORAGE = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                boolean WRITE_EXTERNAL_STORAGE = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                if (READ_EXTERNAL_STORAGE && WRITE_EXTERNAL_STORAGE) {
                    checkPermissions();
                } else {
                    storagePermissionNotGranted();
                }
            }
        }
    }

    private void storagePermissionNotGranted() {
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    @SuppressLint("MissingSuperCall")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case RQS_OPEN_DOCUMENT_TREE:
                if (resultCode == RESULT_OK) {
                    currentWorkspace = FileUtils.getPath(this, data.getData());

                    final ContentResolver contentResolver = getContentResolver();

                    final Uri treeUri = data != null ? data.getData() : null;
                    try {
                        contentResolver.takePersistableUriPermission(treeUri,
                                Intent.FLAG_GRANT_READ_URI_PERMISSION
                                        | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                        checkPermissions();
                    } catch (SecurityException e) {
                        storagePermissionNotGranted();
                    }
                    break;
                }
            case PROJECT_IMPORT_FILE_SELECT:
                if (resultCode == RESULT_OK) {
                    final Uri uri = data.getData();
                    if (uri != null) {
                        final String path = FileUtils.getPath(this, uri);
                        if (path != null) {
                            final File file = new File(path);
                            final String extension = FileUtils
                                    .getFileExtension(file);

                            if (extension == null || extension.isEmpty() || (PROJECT_IMPORT_EXTENSION.equalsIgnoreCase(extension))) {
                                Uri fileUri = Uri.fromFile(file);

                                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
                                String destination = sharedPreferences.getString("current_workspace", getExternalFilesDir(null).getAbsolutePath() + "/projects");

                                ImporterUtils.importProjectFile(fileUri,
                                        destination, this);
                            } else {
                                Toast.makeText(
                                        this,
                                        getString(R.string.error_fileload_extension)
                                                + " " + PROJECT_IMPORT_EXTENSION,
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                    break;
                }
        }
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PERMISSION_STORAGE_REQUEST) {
            if (!Environment.isExternalStorageManager()) {
                storagePermissionNotGranted();
                currentWorkspace = FileUtils.getPath(this, data.getData());
            } else {
                checkPermissions();
            }
        }
    }

    @Override
    public void onBackPressed() {
        // Do nothing
    }

}
