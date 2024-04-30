package com.example.projet_interim.EmployeurAgence;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.projet_interim.Anon_Candidates.AnnonceList_Menu_Anon_Candidates;
import com.example.projet_interim.Commun.LoginScreen;
import com.example.projet_interim.CurentUser;
import com.example.projet_interim.DB;
import com.example.projet_interim.R;
import com.google.android.material.navigation.NavigationView;

public class AddAnnonceMenu extends AppCompatActivity {

    ActionBarDrawerToggle barToggled;
    DrawerLayout drawerLayout;
    NavigationView navView;

    EditText title_t;
    EditText content_t;
    EditText coord_t;
    Button back_b;
    Button add_b;

    CurentUser user;
    DB db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.add_annonce_menu);

        drawerLayout = (DrawerLayout)findViewById(R.id.drawerLayout_addAnnonce_menu);
        navView = (NavigationView) findViewById(R.id.navView_addAnnonce_menu);

        // init de la db et récupération du user courant
        user = CurentUser.getInstance();
        db = new DB(getApplicationContext(), this);


        // Side Menu
        barToggled = new ActionBarDrawerToggle(this, drawerLayout, 0, 0);
        barToggled.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                gotoMenu(item.getItemId());
                return false;
            }
        });

        title_t = (EditText) findViewById(R.id.addAnnonce_title_text);
        content_t = (EditText) findViewById(R.id.addAnnonce_content_text);
        coord_t = (EditText) findViewById(R.id.addAnnonce_coord_text);
        back_b = (Button) findViewById(R.id.addAnnonce_back_button);
        add_b = (Button) findViewById(R.id.addAnnonce_send_button);
    }

    public void backToProfil(View v){
        finish();
    }

    public void addAnnonce(View v){
        String title = String.valueOf(title_t.getText());
        String content = String.valueOf(content_t.getText());
        String coord = String.valueOf(coord_t.getText());

        if(title.equals("") || content.equals("") || coord.equals("")){
            Toast.makeText(getApplicationContext(),"Objet vide", Toast.LENGTH_LONG).show();
            return;
        }

        db.addAnnonce(user.id, title, content, coord);
        setResult(RESULT_OK);
        finish();
    }

    // Permet d'ouvrir le Side Menu
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(barToggled.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Change d'Activity
    void gotoMenu(int itemId){
        Intent intent = null;
        switch (itemId) {
            case R.id.drawer_profil:
                intent = new Intent(getApplicationContext(), Profil_Menu_Employeur.class);
                break;
            case R.id.drawer_annonce:
                intent = new Intent(getApplicationContext(), AnnonceList_Menu_Anon_Candidates.class);
                break;

            case R.id.drawer_disconnect:
                CurentUser.getInstance().id = null;
                CurentUser.getInstance().username = null;
                CurentUser.getInstance().role = null;
                intent = new Intent(getApplicationContext(), LoginScreen.class);
                break;
        }

        if(intent != null){
            startActivity(intent);
            finish();
        }
    }
}
