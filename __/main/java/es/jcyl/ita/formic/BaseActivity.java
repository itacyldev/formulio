package es.jcyl.ita.formic;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import androidx.appcompat.app.AppCompatActivity;

public abstract class BaseActivity extends AppCompatActivity  {

    protected SharedPreferences sharedPreferences;
    protected String currentTheme;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        currentTheme = sharedPreferences.getString("current_theme", "light");
        setTheme();
        doOnCreate();
    }

    protected abstract void doOnCreate();

    protected void setTheme() {
        //currentTheme = sharedPreferences.getString("current_theme", "light");
        if (currentTheme.equals("light")) {
            setTheme(R.style.FormudruidLight_NoActionBar);
        } else {
            setTheme(R.style.FormudruidDark_NoActionBar);
        }
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
            setTheme(R.style.FormudruidDark);
            sharedPreferences.edit().putString("current_theme", "dark").apply();
        } else {
            setTheme(R.style.FormudruidLight);
            sharedPreferences.edit().putString("current_theme", "light").apply();
        }

        if (currentTheme.equals("")) {
            sharedPreferences.edit().putString("current_theme", "dark")
                    .apply();
        }
        recreate();

        invalidateOptionsMenu();
    }


}
