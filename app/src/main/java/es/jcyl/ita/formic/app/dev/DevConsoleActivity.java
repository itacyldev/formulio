package es.jcyl.ita.formic.app.dev;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatCheckedTextView;

import ch.qos.logback.classic.Level;
import es.jcyl.ita.formic.R;
import es.jcyl.ita.formic.forms.components.StyleHolder;
import es.jcyl.ita.formic.forms.components.radio.RadioButtonStyleHolder;
import es.jcyl.ita.formic.forms.config.DevConsole;

public class DevConsoleActivity extends AppCompatActivity {

    int logLevel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme();
        setContentView(R.layout.activity_dev_console);

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
                switch((String) ((AppCompatCheckedTextView) view).getText()) {
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
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                setConsoleBodyText(text, editable.toString(), logLevel);
            }
        });

    }

    private Spinner createSpinner(){
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
        spinner.setSelection(((ArrayAdapter<String>)arrayAdapter).getPosition(DevConsole.getStrLevel(logLevel)));
        return spinner;
    }

    private RadioGroup createRadioGroup(){
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
        for (SpannableString spannable : DevConsole.getMessages(filterBy, level)) {
            builder.append(spannable);
        }
        text.setText(builder, TextView.BufferType.SPANNABLE);
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
}
