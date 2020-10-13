package es.jcyl.ita.formic.app.dev;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import org.apache.commons.lang3.StringUtils;

import es.jcyl.ita.formic.forms.config.DevConsole;

public class DevConsoleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme();
        setContentView(R.layout.activity_dev_console);
        // set messages in edittext
        EditText text = (EditText) this.findViewById(R.id.console_body);
        text.setText(StringUtils.join(DevConsole.getMessages(), "\n"));
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
}
