package es.jcyl.ita.frmdrd;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import es.jcyl.ita.frmdrd.configuration.parser.DummyFormConfigParser;
import es.jcyl.ita.frmdrd.configuration.parser.FormConfigParser;
import es.jcyl.ita.frmdrd.context.JexlTest;
import es.jcyl.ita.frmdrd.ui.form.UIForm;

public class MainActivity extends AppCompatActivity implements FormListFragment.OnListFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loadFormConfig();

        setContentView(R.layout.activity_main);

        initializeDagger();

        Integer result = JexlTest.test();
        initialize();


    }

    private void loadFormConfig() {
        FormConfigParser parser = new DummyFormConfigParser();
        parser.parseFormConfig("");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
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

        return super.onOptionsItemSelected(item);
    }

    private void initializeDagger() {

    }

    private void initialize() {
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_previous:
                        Toast.makeText(MainActivity.this, "Previous",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.action_new:
                        Toast.makeText(MainActivity.this, "New",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.action_next:
                        Toast.makeText(MainActivity.this, "Next",
                                Toast.LENGTH_SHORT).show();
                        break;
                }
                return true;
            }
        });
    }

    @Override
    public void onListFragmentInteraction(UIForm form) {
        NavigationManager navigationManager = new NavigationManager();
        navigationManager.navigate(this, UserFormAlphaEditActivity.class,
                "formId", form.getId());
    }
}
