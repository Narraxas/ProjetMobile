package com.example.projet_interim.Employeur;

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

import com.example.projet_interim.Candidat.AffichageAnnonces;
import com.example.projet_interim.Commun.Login;
import com.example.projet_interim.CurentUser;
import com.example.projet_interim.DB;
import com.example.projet_interim.R;
import com.google.android.material.navigation.NavigationView;

public class AddAnnonce extends AppCompatActivity {

    ActionBarDrawerToggle barToggle;
    DrawerLayout drawerLayout;
    NavigationView navView;

    EditText titleEditText;
    EditText contentEditText;
    EditText coordEditText;
    Button backButton;
    Button addButton;

    CurentUser user;
    DB db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_annonce);

        drawerLayout = findViewById(R.id.drawerLayout_addAnnonce_menu);
        navView = findViewById(R.id.navView_addAnnonce_menu);

        // Initialize DB and get current user
        user = CurentUser.getInstance();
        db = new DB(getApplicationContext(), this);

        // Side Menu
        barToggle = new ActionBarDrawerToggle(this, drawerLayout, 0, 0);
        barToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                gotoMenu(item.getItemId());
                return false;
            }
        });

        titleEditText = findViewById(R.id.addAnnonce_title_text);
        contentEditText = findViewById(R.id.addAnnonce_content_text);
        coordEditText = findViewById(R.id.addAnnonce_coord_text);
        backButton = findViewById(R.id.addAnnonce_back_button);
        addButton = findViewById(R.id.addAnnonce_send_button);
    }

    public void backToProfile(View v){
        finish();
    }

    public void addAnnonce(View v){
        String title = titleEditText.getText().toString();
        String content = contentEditText.getText().toString();
        String coord = coordEditText.getText().toString();

        if(title.isEmpty() || content.isEmpty() || coord.isEmpty()){
            Toast.makeText(getApplicationContext(),"Champ(s) vide(s)", Toast.LENGTH_LONG).show();
            return;
        }

        db.addAnnonce(user.id, title, content, coord);
        setResult(RESULT_OK);
        finish();
    }

    // Open side menu
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(barToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Switch activity
    void gotoMenu(int itemId){
        Intent intent = null;
        switch (itemId) {
            case R.id.drawer_profil:
                intent = new Intent(getApplicationContext(), ProfilEmployeur.class);
                break;
            case R.id.drawer_annonce:
                intent = new Intent(getApplicationContext(), AffichageAnnonces.class);
                break;
            case R.id.drawer_disconnect:
                CurentUser.getInstance().id = null;
                CurentUser.getInstance().username = null;
                CurentUser.getInstance().role = null;
                intent = new Intent(getApplicationContext(), Login.class);
                break;
        }

        if(intent != null){
            startActivity(intent);
            finish();
        }
    }
}
