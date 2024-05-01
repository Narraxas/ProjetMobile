package com.example.projet_interim.Commun;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.projet_interim.Candidat.AffichageAnnonces;
import com.example.projet_interim.Candidat.ProfilCandidat;
import com.example.projet_interim.CurentUser;
import com.example.projet_interim.DB;
import com.example.projet_interim.Employeur.ProfilEmployeur;
import com.example.projet_interim.R;
import com.google.android.material.navigation.NavigationView;

public class WriteMessage extends AppCompatActivity {

    ActionBarDrawerToggle barToggled;
    DrawerLayout drawerLayout;
    NavigationView navView;

    EditText destinataireEditText;
    EditText objetEditText;
    EditText contentEditText;

    CurentUser user;
    DB db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.write_message);

        drawerLayout = findViewById(R.id.drawerLayout_notifMenu);
        navView = findViewById(R.id.navView_notifMenu);

        // Initialize DB and get current user
        user = CurentUser.getInstance();
        db = new DB(getApplicationContext(), this);

        // Change side menu content based on user role
        switch (user.role){
            case "candidat":
                navView.getMenu().clear();
                navView.inflateMenu(R.menu.candidat_message_menu);
                break;
            case "employeur":
                navView.getMenu().clear();
                navView.inflateMenu(R.menu.employeur_message_menu);
                break;
        }

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

        destinataireEditText = findViewById(R.id.writeNotif_destinataire_text);
        objetEditText = findViewById(R.id.writeNotif_title_text);
        contentEditText = findViewById(R.id.writeNotif_content_text);

        // Set recipient and subject if passed from another activity
        if(getIntent().getExtras() != null){
            destinataireEditText.setText(getIntent().getExtras().getString("to"));
            objetEditText.setText(getIntent().getExtras().getString("obj"));
        }
    }

    public void backToNotifList(View v){
        finish();
    }

    public void sendNotif(View v){
        String dest = destinataireEditText.getText().toString();
        String obj = objetEditText.getText().toString();
        String cont = contentEditText.getText().toString();

        // Validate subject
        if(obj.isEmpty()){
            Toast.makeText(getApplicationContext(),"Objet vide", Toast.LENGTH_LONG).show();
            return;
        }

        String destID = db.getUserId(dest);

        // Check if recipient exists
        if(destID == null){
            Toast.makeText(getApplicationContext(),"Le destinataire n'existe pas", Toast.LENGTH_LONG).show();
            return;
        }

        // Add notification to database and finish activity
        db.addNotif(user.id, destID, obj, cont);
        finish();
    }

    // Open side menu
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(barToggled.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Switch activity
    void gotoMenu(int itemId){

        Intent intent = null;
        switch (itemId) {
            case R.id.drawer_profil:
                if(user.role.equals("candidat")){
                    intent = new Intent(getApplicationContext(), ProfilCandidat.class);
                }
                if(user.role.equals("employeur")){
                    intent = new Intent(getApplicationContext(), ProfilEmployeur.class);
                }

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
