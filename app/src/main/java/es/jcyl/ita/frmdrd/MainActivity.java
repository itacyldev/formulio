package es.jcyl.ita.frmdrd;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

import es.jcyl.ita.frmdrd.configuration.ConfigFacade;
import es.jcyl.ita.frmdrd.forms.FormController;
import es.jcyl.ita.frmdrd.view.activities.FormListFragment;

public class MainActivity extends AppCompatActivity implements FormListFragment.OnListFragmentInteractionListener {
    private static final int PERMISSION_REQUEST = 1234;
    ConfigFacade config = new ConfigFacade();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkPermissions();
        setContentView(R.layout.activity_main);
        initialize();

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
    public void onListFragmentInteraction(FormController form) {
        MainController.getInstance().getRouter().navigate(this, form.getId(), null);
    }

    protected void checkPermissions() {
        List permsList = new ArrayList();

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            permsList.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            permsList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if (permsList.size() > 0) {
            ActivityCompat.requestPermissions(this, (String[]) permsList
                            .toArray(new String[]{}),
                    PERMISSION_REQUEST);
        } else {
            config.init();
        }
    }
}
