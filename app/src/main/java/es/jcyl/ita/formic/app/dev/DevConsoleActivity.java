package es.jcyl.ita.formic.app.dev;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatCheckedTextView;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

import ch.qos.logback.classic.Level;
import es.jcyl.ita.formic.R;
import es.jcyl.ita.formic.app.dialog.JobResultDialog;
import es.jcyl.ita.formic.forms.App;
import es.jcyl.ita.formic.forms.components.StyleHolder;
import es.jcyl.ita.formic.forms.components.radio.RadioButtonStyleHolder;
import es.jcyl.ita.formic.forms.config.DevConsole;
import es.jcyl.ita.formic.forms.project.ProjectImporter;
import es.jcyl.ita.formic.forms.util.FileUtils;
import es.jcyl.ita.formic.forms.view.UserMessagesHelper;
import es.jcyl.ita.formic.forms.view.activities.BaseActivity;
import es.jcyl.ita.formic.jayjobs.task.utils.ContextAccessor;

public class DevConsoleActivity extends BaseActivity {

    public static final int COLOR_ERROR = Color.RED;
    public static final int COLOR_INFO = Color.GREEN;
    public static final int COLOR_WARN = Color.YELLOW;
    public static final int COLOR_DEBUG = Color.BLUE;

    public static final String PROJECT_EXTENSION = "fml";
    public static final String ZIP_EXTENSION = "zip";

    private static Context context;

    @Override
    protected void doOnCreate() {
        setContentView(R.layout.activity_dev_console);
        //setTheme();

        setToolbar(getString(R.string.action_dev_console));

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        logLevel = sharedPreferences.getInt("log_level", Log.DEBUG);

        // set messages in edittext
        EditText text = this.findViewById(R.id.console_body);
        text.setMovementMethod(new ScrollingMovementMethod());

        EditText filterBy = this.findViewById(R.id.dev_console_filter_by);
        /*ImageView resetButton = this.findViewById(R.id.field_layout_x);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View arg0) {
                filterBy.setText(null);
            }
        });*/

        Spinner spinner = createSpinner();
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch ((String) ((AppCompatCheckedTextView) view).getText()) {
                    case "DEBUG":
                        setLogLevel(Log.DEBUG, Level.DEBUG);
                        break;
                    case "INFO":
                        setLogLevel(Log.INFO, Level.INFO);
                        break;
                    case "WARN":
                        setLogLevel(Log.WARN, Level.WARN);
                        break;
                    case "ERROR":
                        setLogLevel(Log.ERROR, Level.ERROR);
                        break;
                    default:
                        setLogLevel(Log.DEBUG, Level.DEBUG);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });


        RadioGroup radioGroup = createRadioGroup();

        setConsoleBodyText(text, filterBy.getText().toString(), logLevel);

        RadioButton input_view = this.findViewById(logLevel);
        input_view.setChecked(true);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                DevConsole.setLevel(checkedId);
                logLevel = DevConsole.getLevel();
                setConsoleBodyText(text, filterBy.getText().toString(), logLevel);
            }
        });

        filterBy.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Do nothing
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Do nothing
            }

            @Override
            public void afterTextChanged(Editable editable) {
                setConsoleBodyText(text, editable.toString(), logLevel);
            }
        });

        Button exportButton = (Button) findViewById(R.id.export);
        exportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                context = exportButton.getContext();
                ExportTask exportTask = new ExportTask(context);
                exportTask.execute();
            }
        });

        Button logtButton = (Button) findViewById(R.id.log);
        logtButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                context = view.getContext();
                try {
                    final Uri uri = FileProvider.getUriForFile(context, context.getPackageName() + ".provider", getLogFile());
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    intent.setDataAndType(uri, "text/plain");
                    context.startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(context, "No application found which can open the file", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private static String getProjectName(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String projectName = sharedPreferences.getString("projectName", "");
        return projectName;
    }

    private static String getProjectsFolder(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String projectsFolder = sharedPreferences.getString("current_workspace", context.getExternalFilesDir(null).getAbsolutePath() + "/projects");
        return projectsFolder;
    }

    private static String getLogsFolder(){
        String projectName = getProjectName();
        String projectsFolder = getProjectsFolder();
        String logsFolder = projectsFolder+"/"+projectName+"/logs";
        return logsFolder;
    }

    private static File getLogFile(){
        String logFolder = getLogsFolder();
        String logFile = logFolder+"/"+getProjectName()+".log";
        return new File(logFile);
    }

    private File copyLogFiles() throws IOException {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String projectName = sharedPreferences.getString("projectName", "");
        String projectsFolder = sharedPreferences.getString("current_workspace", context.getExternalFilesDir(null).getAbsolutePath() + "/projects");
        String logsFolder = projectsFolder+"/"+projectName+"/logs";

        String destination = ContextAccessor.workingFolder(App.getInstance().getGlobalContext());
        //new File(dest).mkdirs();

        File originFile = new File(logsFolder);
        File destinationFile = new File(destination);

        FileUtils.copyDirectory(context, logsFolder, destination);


        /*SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String projectName = sharedPreferences.getString("projectName", "");
        String projectsFolder = sharedPreferences.getString("current_workspace", context.getExternalFilesDir(null).getAbsolutePath() + "/projects");
        String logsFolder = projectsFolder+"/"+projectName+"/logs";
        String urlOrigen = logsFolder+"/"+projectName+".log";
        String urlDestino = ContextAccessor.workingFolder(App.getInstance().getGlobalContext())+"/"+projectName+".log";
        File fileOrigen = new File(urlOrigen);
        File fileDestino = new File(urlDestino);

        FileUtils.copyFile(context, fileOrigen, fileDestino);*/

        return destinationFile;
    }

    private Spinner createSpinner() {
        Spinner spinner = this.findViewById(R.id.dev_console_system_log_level);
        final String[] levels = {
                getString(R.string.debug),
                getString(R.string.info),
                getString(R.string.warn),
                getString(R.string.error)};
        ArrayAdapter arrayAdapter = new ArrayAdapter<String>(this,
                R.layout.spinner_item, levels);
        arrayAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        arrayAdapter.notifyDataSetChanged();

        spinner.setAdapter(arrayAdapter);
        spinner.setSelection(((ArrayAdapter<String>) arrayAdapter).getPosition(DevConsole.getStrLevel(logLevel)));
        return spinner;
    }

    private RadioGroup createRadioGroup() {
        RadioGroup radioGroup = this.findViewById(R.id.dev_console_log_level);
        radioGroup.addView(createRadioButton(getString(R.string.debug), Log.DEBUG));
        radioGroup.addView(createRadioButton(getString(R.string.info), Log.INFO));
        radioGroup.addView(createRadioButton(getString(R.string.warn), Log.WARN));
        radioGroup.addView(createRadioButton(getString(R.string.error), Log.ERROR));
        return radioGroup;
    }

    private RadioButton createRadioButton(String text, int level) {
        RadioButton rb = new RadioButton(this);
        rb.setText(text);
        rb.setId(level);
        StyleHolder<RadioButton> styleHolder = new RadioButtonStyleHolder(this);
        styleHolder.applyStyle(rb);
        return rb;
    }

    private void setConsoleBodyText(EditText text, String filterBy, int level) {
        SpannableStringBuilder builder = new SpannableStringBuilder();
        for (String msg : DevConsole.getMessages(filterBy, level)) {
            SpannableString spannableMsg = getSpannableString(msg);
            builder.append(spannableMsg);
        }
        text.setText(builder, TextView.BufferType.SPANNABLE);
    }

    private SpannableString getSpannableString(String msg) {
        int color = getLevelColor(msg);
        SpannableString spannable = new SpannableString(msg);
        int start = 0;
        int end = msg.length();
        if (color != COLOR_ERROR) {
            start = msg.indexOf("[");
            end = msg.indexOf("]") + 1;
            //end = start + strLevel.length();
        }
        spannable.setSpan(new ForegroundColorSpan(color), start, end, 0);
        return spannable;
    }

    private int getLevelColor(String msg) {
        int color = Color.BLACK;
        if (msg.contains("[DEBUG]")) {
            color = COLOR_DEBUG;
        } else if (msg.contains("[INFO]")) {
            color = COLOR_INFO;
        } else
        if (msg.contains("[ERROR]")) {
            color = COLOR_ERROR;
        } else if (msg.contains("[WARN]")) {
            color = COLOR_WARN;
        }

        return color;
    }

    protected void setTheme() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String theme = sharedPreferences.getString("current_theme", "light");
        if (theme.equals("light")) {
            setTheme(R.style.FormudruidLight_NoActionBar);
        } else {
            setTheme(R.style.FormudruidDark_NoActionBar);
        }
    }

    protected void setLogLevel(int level, Level logLevel) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.edit().putInt("log_level", level).apply();

        /*LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        ch.qos.logback.classic.Logger logger = loggerContext.getLogger(getPackageName());
        logger.setLevel(logLevel);*/

        DevConsole.setLevel(level);
    }

    private class ExportTask extends AsyncTask<String, String, String> {
        JobResultDialog jobResultDialog;
        Context currentContext;

        public ExportTask(Context context) {
            currentContext = context;
        }

        protected String doInBackground(final String... params) {

            String projectsFolder = getProjectsFolder();
            String projectName = getProjectName();

            ProjectImporter projectImporter = ProjectImporter.getInstance();

            String dest = ContextAccessor.workingFolder(App.getInstance().getGlobalContext());
            new File(dest).mkdirs();
            projectImporter.zipFolder(new File(projectsFolder), projectName, projectName, PROJECT_EXTENSION, new File(dest), null);

            dest = currentContext.getCacheDir().getAbsolutePath()+File.separator+currentContext.getString(R.string.export);
            new File(dest).mkdirs();

            Calendar date = Calendar.getInstance();
            date.add(Calendar.MINUTE, -15);

            File file = projectImporter.zipFolder(currentContext.getCacheDir(), "tmp", currentContext.getString(R.string.export), ZIP_EXTENSION, new File(dest), date);
            
            jobResultDialog.addResource(file.getPath());

            return "";
        }

        @SuppressLint("NewApi")
        @Override
        protected void onPostExecute(final String success) {
            if (success.isEmpty()) {
                jobResultDialog.setText("Export successful!");
                UserMessagesHelper.toast(context, "Export successful!", Toast.LENGTH_SHORT);
            } else {
                jobResultDialog.setText("Export failed!");
                UserMessagesHelper.toast(context, "Export failed!", Toast.LENGTH_SHORT);
            }
            jobResultDialog.endJob();
        }

        @Override
        protected void onPreExecute() {
            jobResultDialog = new JobResultDialog((DevConsoleActivity)context, false);
            jobResultDialog.show();
            jobResultDialog.setProgressTitle(context.getString(R.string.export));
            jobResultDialog.setText(context.getString(R.string.exporting));

            jobResultDialog.getAcceptButton().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    jobResultDialog.dismiss();
                }
            });
        }
    }
}
