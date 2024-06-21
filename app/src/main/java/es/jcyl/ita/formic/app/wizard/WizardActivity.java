package es.jcyl.ita.formic.app.wizard;

import android.os.Bundle;
import android.view.DragEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import es.jcyl.ita.formic.R;
import es.jcyl.ita.formic.forms.wizard.MenuAdapter;
import es.jcyl.ita.formic.forms.wizard.MenuItem;


public class WizardActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private RecyclerView recyclerView;
    private MenuAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_wizard);

        // Inicializar el DrawerLayout
        drawerLayout = findViewById(R.id.drawer_layout);

        findViewById(R.id.btn_open_menu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Abrir el menú deslizante
                drawerLayout.openDrawer(GravityCompat.START); // Abre el cajón lateral desde el inicio (izquierda)
            }
        });

        findViewById(R.id.drop_zone).setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                switch (event.getAction()) {
                    case DragEvent.ACTION_DROP:
                        // Lógica para procesar el componente arrastrado
                        Toast.makeText(WizardActivity.this, "Component dropped", Toast.LENGTH_SHORT).show();
                        return true;
                    default:
                        break;
                }
                return false;
            }
        });

        // Inicializar RecyclerView
        recyclerView = findViewById(R.id.nav_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<MenuItem> menuItems = new ArrayList<>();
        menuItems.add(new MenuItem(R.id.input_component, "Input", R.drawable.ic_input));
        menuItems.add(new MenuItem(R.id.select_component, "Select", R.drawable.ic_select));
        menuItems.add(new MenuItem(R.id.radio_component, "Radio", R.drawable.ic_radio));
        menuItems.add(new MenuItem(R.id.radio_component, "Textarea", R.drawable.ic_textarea));
        menuItems.add(new MenuItem(R.id.radio_component, "Switcher", R.drawable.ic_switcher));
        menuItems.add(new MenuItem(R.id.radio_component, "Date", R.drawable.ic_date));
        menuItems.add(new MenuItem(R.id.radio_component, "Button", R.drawable.ic_button));
        menuItems.add(new MenuItem(R.id.radio_component, "Divisor", R.drawable.ic_divisor));
        menuItems.add(new MenuItem(R.id.radio_component, "Paragraph", R.drawable.ic_paragraph));
        menuItems.add(new MenuItem(R.id.radio_component, "Link", R.drawable.ic_link));
        menuItems.add(new MenuItem(R.id.radio_component, "Card", R.drawable.ic_card));
        //https://www.flaticon.com/search?word=input&type=uicon
        // Agrega más elementos según sea necesario

        adapter = new MenuAdapter(menuItems, new MenuAdapter.OnMenuItemClickListener() {
            @Override
            public void onMenuItemClick(MenuItem item) {
                handleMenuItemClick((android.view.MenuItem) item);
            }
        });

        recyclerView.setAdapter(adapter);

        // Botón para cerrar el menú
        ImageButton closeButton = findViewById(R.id.btn_close_menu);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Cerrar el menú deslizante
                drawerLayout.closeDrawer(GravityCompat.START); // Cierra el cajón lateral desde el inicio (izquierda)
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_component_options, menu);

        return true;
    }

    private void handleMenuItemClick(android.view.MenuItem item) {
        switch (item.getItemId()) {
            case R.id.input_component:
                Toast.makeText(this, "Input component selected", Toast.LENGTH_SHORT).show();
                break;
            case R.id.select_component:
                Toast.makeText(this, "Select component selected", Toast.LENGTH_SHORT).show();
                break;
            case R.id.radio_component:
                Toast.makeText(this, "Radio component selected", Toast.LENGTH_SHORT).show();
                break;
            // Agrega más casos según sea necesario
        }
    }


}
