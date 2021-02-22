package es.jcyl.ita.formic.app.dev;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.method.ScrollingMovementMethod;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import es.jcyl.ita.formic.R;
import es.jcyl.ita.formic.forms.config.DevConsole;

public class DevConsoleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme();
        setContentView(R.layout.activity_dev_console);
        // set messages in edittext
        EditText text = (EditText) this.findViewById(R.id.console_body);
        text.setMovementMethod(new ScrollingMovementMethod());
        //text.setText(StringUtils.join(DevConsole.getMessages(), "\n"));
        SpannableStringBuilder builder = new SpannableStringBuilder();
        for (SpannableString spannable: DevConsole.getMessages()){
            builder.append(spannable);
        }
        text.setText(builder, TextView.BufferType.SPANNABLE);


        /*ListView consoleBody = this.findViewById(R.id.console_body);
        ArrayAdapter<String> itemsAdapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, (List<String>) DevConsole.getMessages());
        consoleBody.setAdapter(itemsAdapter);*/

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
