package es.jcyl.ita.frmdrd;

import android.os.Bundle;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import org.apache.commons.lang3.StringUtils;

import es.jcyl.ita.frmdrd.config.DevConsole;

public class DevConsoleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dev_console);
        // set messages in edittext
        EditText text = (EditText) this.findViewById(R.id.console_body);
        text.setText(StringUtils.join(DevConsole.getMessages(), "\n"));
    }

}
