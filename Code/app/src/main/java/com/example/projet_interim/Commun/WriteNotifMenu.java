package com.example.projet_interim.Commun;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.projet_interim.Anon_Candidates.AnnonceList_Menu_Anon_Candidates;
import com.example.projet_interim.Anon_Candidates.Profile_Menu_Candidates;
import com.example.projet_interim.CurentUser;
import com.example.projet_interim.DB;
import com.example.projet_interim.EmployeurAgence.Profil_Menu_Employeur;
import com.example.projet_interim.NotifAdaptator;
import com.example.projet_interim.OfferAdaptator;
import com.example.projet_interim.R;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

public class WriteNotifMenu extends AppCompatActivity {

    ActionBarDrawerToggle barToggled;
    DrawerLayout drawerLayout;
    NavigationView navView;

    EditText destinataire_t;
    EditText objet_t;
    EditText content_t;

    CurentUser user;
    DB db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.write_notif_menu);

        drawerLayout = (DrawerLayout)findViewById(R.id.drawerLayout_notifMenu);
        navView = (NavigationView) findViewById(R.id.navView_notifMenu);

        // init de la db et récupération du user courant
        user = CurentUser.getInstance();
        db = new DB(getApplicationContext(), this);

        // Change le contenu du side menu
        switch (user.role){
            case "candidat":
                navView.getMenu().clear();
                navView.inflateMenu(R.menu.drawer_candidate_notif);
                break;
            case "employeur":
            case "agence":
                navView.getMenu().clear();
                navView.inflateMenu(R.menu.drawer_employeur_agence_notif);
                break;
            case "admin":
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

        destinataire_t = (EditText) findViewById(R.id.writeNotif_destinataire_text);
        objet_t = (EditText) findViewById(R.id.writeNotif_title_text);
        content_t = (EditText) findViewById(R.id.writeNotif_content_text);

        if(getIntent().getExtras() != null){
            destinataire_t.setText(getIntent().getExtras().getString("to"));
            objet_t.setText(getIntent().getExtras().getString("obj"));
        }
    }

    public void backToNotifList(View v){
        finish();
    }

    public void sendNotif(View v){
        String dest = String.valueOf(destinataire_t.getText());
        String obj = String.valueOf(objet_t.getText());
        String cont = String.valueOf(content_t.getText());

        if(obj.equals("")){
            Toast.makeText(getApplicationContext(),"Objet vide", Toast.LENGTH_LONG).show();
            return;
        }

        String destID = db.getUserId(dest);

        if(destID == null){
            Toast.makeText(getApplicationContext(),"Le destinataire n'existe pas", Toast.LENGTH_LONG).show();
            return;
        }

        db.addNotif(user.id, destID, obj, cont);
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
                if(user.role.equals("candidat")){
                    intent = new Intent(getApplicationContext(), Profile_Menu_Candidates.class);
                }
                if(user.role.equals("employeur") || user.role.equals("agence")){
                    intent = new Intent(getApplicationContext(), Profil_Menu_Employeur.class);
                }
                if(user.role.equals("admin")){
                    // TODO : relier le menu profil admin
                    //intent = new Intent(getApplicationContext(), Profile_Menu_Candidates.class);
                }
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
