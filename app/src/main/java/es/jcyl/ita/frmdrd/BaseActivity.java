package es.jcyl.ita.frmdrd;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import androidx.appcompat.app.AppCompatActivity;

public abstract class BaseActivity extends AppCompatActivity {

    protected SharedPreferences sharedPreferences;
    protected String currentTheme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        currentTheme = sharedPreferences.getString("current_theme", "light");
        setTheme();
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
            setTheme(R.style.Theme_App_Dark);
            sharedPreferences.edit().putString("current_theme", "dark").apply();
        } else {
            setTheme(R.style.Theme_App_Light);
            sharedPreferences.edit().putString("current_theme", "light").apply();
        }

        if (currentTheme.equals("")) {
            sharedPreferences.edit().putString("current_theme", "dark")
                    .apply();
        }
        recreate();

        invalidateOptionsMenu();
    }

    protected abstract void setTheme();

}
