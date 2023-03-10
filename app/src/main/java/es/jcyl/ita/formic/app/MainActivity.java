package es.jcyl.ita.formic.app;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.apache.commons.lang3.StringUtils;
import org.mini2Dx.collections.CollectionUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import es.jcyl.ita.formic.R;
import es.jcyl.ita.formic.app.about.AboutActivity;
import es.jcyl.ita.formic.app.dev.DevConsoleActivity;
import es.jcyl.ita.formic.app.dialog.JobResultDialog;
import es.jcyl.ita.formic.app.dialog.ProjectDialog;
import es.jcyl.ita.formic.app.jobs.JobProgressListener;
import es.jcyl.ita.formic.app.projects.ProjectListFragment;
import es.jcyl.ita.formic.app.settings.SettingsActivity;
import es.jcyl.ita.formic.forms.App;
import es.jcyl.ita.formic.forms.MainController;
import es.jcyl.ita.formic.forms.StorageContentManager;
import es.jcyl.ita.formic.forms.actions.UserActionHelper;
import es.jcyl.ita.formic.forms.config.DevConsole;
import es.jcyl.ita.formic.forms.controllers.ViewController;
import es.jcyl.ita.formic.forms.controllers.ViewControllerFactory;
import es.jcyl.ita.formic.forms.deploy.HotDeployer;
import es.jcyl.ita.formic.forms.project.Project;
import es.jcyl.ita.formic.forms.project.ProjectImporter;
import es.jcyl.ita.formic.forms.project.ProjectRepository;
import es.jcyl.ita.formic.forms.util.FileUtils;
import es.jcyl.ita.formic.forms.view.UserMessagesHelper;
import es.jcyl.ita.formic.forms.view.activities.BaseActivity;
import es.jcyl.ita.formic.forms.view.activities.FormListFragment;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends BaseActivity implements FormListFragment.OnListFragmentInteractionListener {

    private final Activity activity = this;

    protected SharedPreferences settings;

    private static final int PERMISSION_REQUEST = 1234;
    private static final int RQS_OPEN_DOCUMENT_TREE = 2;
    private static final int PERMISSION_STORAGE_REQUEST = 5708463;
    private static final int PROJECT_IMPORT_FILE_SELECT = 725353137;

    private static final String PROJECT_IMPORT_EXTENSION = "FML";

    protected ProgressDialog pd = null;

    @Override
    protected void doOnCreate() {
        getApplication().getFilesDir();
        setContentView(R.layout.activity_main);
        checkPermissions();
        //checkStoragePermission();
        checkDeviceFeatures();
    }

    private void checkDeviceFeatures() {
        Context context = getApplicationContext();
        /** Check if this device has a camera */
        if (!context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            DevConsole.warn("Camera not available in this device.");
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

    private void initFormicBackend() {
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        FloatingActionButton import_project = findViewById(es.jcyl.ita.formic.forms.R.id.import_project);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_projects:
                        loadFragment(ProjectListFragment.newInstance(
                                App.getInstance().getProjectRepo()));
                        loadImageNoProjects();
                        checkNavigationButton(R.id.action_projects);
                        import_project.setVisibility(View.VISIBLE);
                        break;
                    case R.id.action_forms:
                        loadFragment(new FormListFragment());
                        loadImageNoProjects();
                        checkNavigationButton(R.id.action_forms);
                        import_project.setVisibility(View.GONE);
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


        loadFragment();


    }

    public void loadImageNoProjects() {
        RelativeLayout layoutNoProjects = findViewById(R.id.layout_no_projects);
        if (App.getInstance().getProjectRepo().listAll().size() == 0) {
            layoutNoProjects.setVisibility(View.VISIBLE);
        } else {
            layoutNoProjects.setVisibility(View.GONE);
        }
    }

    public void loadFragment() {
        // get view controles and check if exists a "main" form
        ViewControllerFactory ctlFactory = ViewControllerFactory.getInstance();

        String formId = "";
        for (String a : ctlFactory.getControllerIds()) {
            if (a.startsWith("main-")) {
                formId = a;
                break;
            }
        }
        FloatingActionButton import_project = findViewById(es.jcyl.ita.formic.forms.R.id.import_project);
        if (StringUtils.isNotEmpty(formId)) {
            loadFragment(ProjectListFragment.newInstance(
                    App.getInstance().getProjectRepo()));
            MainController.getInstance().getRouter().navigate(MainActivity.this,
                    UserActionHelper.navigate(formId));
            checkNavigationButton(R.id.action_projects);
            import_project.setVisibility(View.VISIBLE);
        } else {
            // open default form list view
            loadFragment(new FormListFragment());
            checkNavigationButton(R.id.action_forms);
            import_project.setVisibility(View.GONE);
        }

        loadImageNoProjects();
    }

    @Override
    public void onListFragmentInteraction(ViewController form) {
        MainController.getInstance().getRouter().navigate(this,
                UserActionHelper.navigate(form.getId()));
    }

    private void checkStoragePermission() {

        boolean requestStorage = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//            File[] externalStorageVolumes =
//                    ContextCompat.getExternalFilesDirs(this,null);
//            File primaryExternalStorage = externalStorageVolumes[0];
//            currentWorkspace = primaryExternalStorage.getPath();
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
                Manifest.permission.CAMERA,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
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
            Uri uri = this.getIntent().getData();
            if (uri != null) {
                importFromUri(FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", new File(FileUtils.getPath(this, uri))));
            } else {
                doInitConfiguration(this);
                loadImageNoProjects();
            }
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
                    doInitConfiguration(this);
                    //new InitConfigurationTask(this).execute();
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
                    importFromUri(uri);
                    break;
                }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        // Do nothing
    }

    private void importProject(Context context, String origin, String destination, String projectName, ProjectImporter projectImporter) {
        try {
            HotDeployer deployer = App.getInstance().getDeployer();
            deployer.stop();
            projectImporter.moveFiles2BackUp(context, projectName);
            projectImporter.extractFiles(context, origin, destination);
            launchActivity(context, projectName);
            deployer.start();
        } catch (IOException e) {
            Toast.makeText(
                    this,
                    getString(R.string.projectimportfail),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void launchActivity(Context context, String projectName) {
        setSharedPreferences(context, projectName);

        final Intent intent =
                context.getPackageManager()
                        .getLaunchIntentForPackage(
                                context.getPackageName());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }

    private void setSharedPreferences(Context context, String
            selectedFileName) {
        final SharedPreferences.Editor editor = context
                .getSharedPreferences("selectedProject", Context.MODE_PRIVATE)
                .edit();
        editor.putString("name", selectedFileName);
        editor.commit();
    }

    private class ImportTask extends AsyncTask<Uri, String, String> {
        JobResultDialog jobResultDialog;
        Context currenContext;
        String origin;
        String destination;
        String projectName;
        ProjectImporter projectImporter;
        boolean showAcceptButton = false;

        public ImportTask(Context context) {
            currenContext = context;
        }

        protected String doInBackground(final Uri... params) {
            String text = "";

            projectImporter = ProjectImporter.getInstance();

            origin = projectImporter.getPathString(currenContext, params[0]);
            destination = getExternalFilesDir(null).getAbsolutePath() + "/projects";
            Map<String, String> existingFiles = null;
            try {
                existingFiles = projectImporter.getExistingFiles(origin, destination);
                projectName = projectImporter.getProjectName(currenContext, params[0].getLastPathSegment());

                if ("".equals(origin)) {
                    text = currenContext.getString(R.string.projectimportfail);
                } else if (existingFiles.size() > 0) {
                    text = currenContext.getString(R.string.project_import_existing_files);

                    for (String file : existingFiles.keySet()) {
                        text += "\n- " + file;
                    }

                    text += "\n" + currenContext.getString(R.string
                            .project_import_overwrite_files);
                    showAcceptButton = true;
                    //jobResultDialog.getAcceptButton().setVisibility(View.VISIBLE);
                } else {
                    text = currenContext.getString(R.string.project_import_continue);
                    showAcceptButton = true;
                    //jobResultDialog.getAcceptButton().setVisibility(View.VISIBLE);

                }
            } catch (IOException e) {
                text = "Import failed!";
            }

            return text;
        }

        @SuppressLint("NewApi")
        @Override
        protected void onPostExecute(final String text) {
            jobResultDialog.endJob();
            jobResultDialog.getAcceptButton().setVisibility(showAcceptButton ? View.VISIBLE : View.GONE);
            jobResultDialog.setText(text);
        }

        @Override
        protected void onPreExecute() {
            jobResultDialog = new JobResultDialog(activity, false);
            jobResultDialog.show();
            jobResultDialog.getShowConsoleButton().setVisibility(View.GONE);
            jobResultDialog.setProgressTitle(activity.getString(R.string.action_import_project));

            jobResultDialog.getAcceptButton().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    jobResultDialog.dismiss();
                    importProject(currenContext, origin, destination, projectName, projectImporter);
                }
            });
        }
    }

    class InitConfigurationTask extends AsyncTask<String, String, String> {
        JobResultDialog jobResultDialog;
        Context currentContext;

        public InitConfigurationTask(Context context) {
            currentContext = context;
        }

        @Override
        protected String doInBackground(String... params) {
            return doInitConfiguration(currentContext);
        }

        @Override
        protected void onPostExecute(String text) {
            loadImageNoProjects();
            jobResultDialog.endJob();
            if (StringUtils.isEmpty(text)) {
                text = currentContext.getString(R.string.project_opening_finish);
            }
            jobResultDialog.setText(text);
            jobResultDialog.getAcceptButton().setVisibility(View.VISIBLE);

        }

        @Override
        protected void onPreExecute() {
            jobResultDialog = new JobResultDialog(activity, false);
            jobResultDialog.show();
            jobResultDialog.setProgressTitle(activity.getString(R.string.project_opening));
            jobResultDialog.getBackButton().setVisibility(View.GONE);
            jobResultDialog.getShowConsoleButton().setVisibility(View.GONE);
            jobResultDialog.setText(currentContext.getString(R.string.project_opening));

            jobResultDialog.getAcceptButton().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    jobResultDialog.dismiss();
                }
            });
        }

    }

    protected String doInitConfiguration(Context ctx) {
        String text = "";

        // Ver esto: FORMIC-678 Revisar mainActivity
        File[] externalStorageVolumes =
                ContextCompat.getExternalFilesDirs(this, null);
        File primaryExternalStorage = externalStorageVolumes[0];
        currentWorkspace = primaryExternalStorage.getPath() + "/projects";

        String projectsFolder = currentWorkspace;

        File f = new File(projectsFolder);
        if (!f.exists()) {
            f.mkdir();
        }
        // initilize formic configuration
        App app = App.init(ctx, projectsFolder);

        ProjectRepository projectRepo = app.getProjectRepo();
        List<Project> projects = projectRepo.listAll();
        if (CollectionUtils.isEmpty(projects)) {
            text = ctx.getString(R.string.no_projects_found);
            DevConsole.warn(text);
            //UserMessagesHelper.toast(this, warn("No projects found!!. Create a folder under " + projectsFolder), Snackbar.LENGTH_LONG);
        } else {
            // TODO: extract Project View Helper to FORMIC-27
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ctx);
            String projectName = sharedPreferences.getString("projectName", "");
            Project prj = projects.get(0);
            if (projectName != null) {
                for (Project project : projects) {
                    if (project.getName().equalsIgnoreCase(projectName)) {
                        prj = project;
                    }
                }
            }
            DevConsole.setLogFileName(projectsFolder, (String) prj.getId());
            try {
                App.getInstance().openProject(prj);
                DevConsole.info(ctx.getString(R.string.project_opening_finish, prj.getId()));
            } catch (Exception e) {
                text = ctx.getString(R.string.project_opening_error, prj.getId());
                DevConsole.error(text);
                UserMessagesHelper.toast(this, DevConsole.info(this.getString(R.string.project_opening_error, (String) prj.getId())), Toast.LENGTH_LONG);
            }
            App.getInstance().setJobListener(new JobProgressListener());
        }
        initFormicBackend();
        return text;
    }

    @Override
    protected void onResume() {
        super.onResume();
        int id = this.getTitle().toString().startsWith(getString(R.string.projects_of)) ? R.id.action_projects : R.id.action_forms;
        checkNavigationButton(id);
    }

    private void checkNavigationButton(int id) {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.getMenu().findItem(id).setChecked(true);
    }

    private void importFromUri(Uri uri) {
        if (uri != null) {

            final String path = FileUtils.copyFileToInternalStorage(this, uri, this.getString(R.string.app_name));
            if (path != null) {
                final File file = new File(path);
                final String extension = FileUtils
                        .getFileExtension(file);

                if (extension == null || extension.isEmpty() || PROJECT_IMPORT_EXTENSION.equalsIgnoreCase(extension)) {
                    Uri fileUri = Uri.fromFile(file);
                    ImportTask importTask = new ImportTask(this);
                    importTask.execute(fileUri);
                } else {
                    Toast.makeText(
                            this,
                            getString(R.string.error_fileload_extension)
                                    + " " + PROJECT_IMPORT_EXTENSION,
                            Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

}
