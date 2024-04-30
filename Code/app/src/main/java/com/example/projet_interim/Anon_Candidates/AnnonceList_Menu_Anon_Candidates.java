package com.example.projet_interim.Anon_Candidates;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.projet_interim.Commun.LoginScreen;
import com.example.projet_interim.Commun.NotifMenu;
import com.example.projet_interim.CurentUser;
import com.example.projet_interim.DB;
import com.example.projet_interim.EmployeurAgence.Profil_Menu_Employeur;
import com.example.projet_interim.OfferAdaptator;
import com.example.projet_interim.R;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

public class AnnonceList_Menu_Anon_Candidates extends AppCompatActivity {

    ActionBarDrawerToggle barToggled;

    ListView listView;
    ArrayList<ArrayList<String>> offerList;
    DrawerLayout drawerLayout;
    NavigationView navView;
    EditText searchEmployeur_t;

    CurentUser user;
    DB db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.annonce_list_menu_anon_candidate);

        listView = (ListView)findViewById(R.id.offre_listview);
        drawerLayout = (DrawerLayout)findViewById(R.id.drawerLayout1);
        navView = (NavigationView) findViewById(R.id.navView);
        searchEmployeur_t = (EditText) findViewById(R.id.employeur_research_text);

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

        // init de la db et récupération du user courant
        user = CurentUser.getInstance();
        db = new DB(getApplicationContext(), this);

        // init de la liste des annonces
        offerList = db.annonces;
        OfferAdaptator adapter = new OfferAdaptator(AnnonceList_Menu_Anon_Candidates.this,offerList);
        listView.setAdapter(adapter);

        // Capture l'item cliqué dans la listView
        listView.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(user.id != null){
                    ArrayList<String> annonce = (ArrayList<String>)listView.getItemAtPosition(position);
                    //Toast.makeText(getApplicationContext(),"You selected : " + item,Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(getApplicationContext(), ApplyTo_Menu_Candidates.class);
                    intent.putExtra("annonce",annonce);
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), "Vous n'êtes pas connecté", Toast.LENGTH_SHORT).show();
                }
            }
        });

        searchEmployeur_t.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                refreshListView();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    // Permet d'ouvrir le Side Menu
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (user != null && user.id != null) {
            if (barToggled != null && barToggled.onOptionsItemSelected(item)) {
                return true;
            }
            return super.onOptionsItemSelected(item);
        } else {
            Toast.makeText(getApplicationContext(), "Vous n'êtes pas connecté", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, LoginScreen.class);
            startActivity(intent);
            return true; // ou false selon vos besoins
        }
    }



    void refreshListView(){
        String searchEmployeur = String.valueOf(searchEmployeur_t.getText());

        if(searchEmployeur.equals("")){
            offerList = db.annonces;

        } else {
            offerList = db.getAnnoncesFromUsername(searchEmployeur);
        }

        OfferAdaptator adapter = new OfferAdaptator(AnnonceList_Menu_Anon_Candidates.this,offerList);
        listView.setAdapter(adapter);
    }

    // Change d'Activity
    void gotoMenu(int itemId){

        Intent intent = null;
        switch (itemId) {
            case R.id.drawer_profil:
                if(user.role.equals("candidat")){
                    intent = new Intent(getApplicationContext(), Profile_Menu_Candidates.class);
                } else {
                    intent = new Intent(getApplicationContext(), Profil_Menu_Employeur.class);
                }
                break;
            case R.id.drawer_msg:
                intent = new Intent(getApplicationContext(), NotifMenu.class);
                break;
            case R.id.drawer_disconnect:
                CurentUser.getInstance().id = null;
                CurentUser.getInstance().username = null;
                CurentUser.getInstance().role = null;
                intent = new Intent(getApplicationContext(), LoginScreen.class);
                break;
        }

        if(intent != null){
            if(user.id == null){
                Toast.makeText(getApplicationContext(), "Vous n'êtes pas connecté", Toast.LENGTH_SHORT).show();
            } else {
                startActivity(intent);
                finish();
            }
        }
    }
}