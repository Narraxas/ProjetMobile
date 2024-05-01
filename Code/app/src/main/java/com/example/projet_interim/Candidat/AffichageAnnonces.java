package com.example.projet_interim.Candidat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.projet_interim.Commun.Login;
import com.example.projet_interim.Commun.Message;
import com.example.projet_interim.CurentUser;
import com.example.projet_interim.DB;
import com.example.projet_interim.Employeur.ProfilEmployeur;
import com.example.projet_interim.OfferAdaptator;
import com.example.projet_interim.R;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

public class AffichageAnnonces extends AppCompatActivity {

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
        setContentView(R.layout.affichage_annonces);

        listView = findViewById(R.id.offre_listview);
        drawerLayout = findViewById(R.id.drawerLayout1);
        navView = findViewById(R.id.navView);
        searchEmployeur_t = findViewById(R.id.employeur_research_text);

        // Side Menu
        barToggled = new ActionBarDrawerToggle(this, drawerLayout, 0, 0);
        barToggled.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        navView.setNavigationItemSelectedListener(item -> {
            gotoMenu(item.getItemId());
            return false;
        });

        // init de la db et récupération du user courant
        user = CurentUser.getInstance();
        db = new DB(getApplicationContext(), this);

        // init de la liste des annonces
        offerList = db.annonces;
        OfferAdaptator adapter = new OfferAdaptator(AffichageAnnonces.this, offerList);
        listView.setAdapter(adapter);

        // Capture l'item cliqué dans la listView
        listView.setOnItemClickListener((parent, view, position, id) -> {
            if (user.id != null) {
                ArrayList<String> annonce = (ArrayList<String>) listView.getItemAtPosition(position);
                Intent intent = new Intent(getApplicationContext(), ApplyCandidature.class);
                intent.putExtra("annonce", annonce);
                startActivity(intent);
            } else {
                Toast.makeText(getApplicationContext(), "Vous n'êtes pas connecté", Toast.LENGTH_SHORT).show();
            }
        });

        searchEmployeur_t.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                refreshListView();
            }

            @Override
            public void afterTextChanged(Editable editable) {}
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
            Intent intent = new Intent(this, Login.class);
            startActivity(intent);
            return true; // ou false selon vos besoins
        }
    }

    void refreshListView() {
        String searchEmployeur = String.valueOf(searchEmployeur_t.getText());

        if (searchEmployeur.equals("")) {
            offerList = db.annonces;
        } else {
            offerList = db.getAnnoncesFromUsername(searchEmployeur);
        }

        OfferAdaptator adapter = new OfferAdaptator(AffichageAnnonces.this, offerList);
        listView.setAdapter(adapter);
    }

    // Change d'Activity
    void gotoMenu(int itemId) {
        Intent intent = null;
        switch (itemId) {
            case R.id.drawer_profil:
                if (user.role.equals("candidat")) {
                    intent = new Intent(getApplicationContext(), ProfilCandidat.class);
                } else {
                    intent = new Intent(getApplicationContext(), ProfilEmployeur.class);
                }
                break;
            case R.id.drawer_msg:
                intent = new Intent(getApplicationContext(), Message.class);
                break;
            case R.id.drawer_disconnect:
                CurentUser.getInstance().id = null;
                CurentUser.getInstance().username = null;
                CurentUser.getInstance().role = null;
                intent = new Intent(getApplicationContext(), Login.class);
                break;
        }

        if (intent != null) {
            if (user.id == null) {
                Toast.makeText(getApplicationContext(), "Vous n'êtes pas connecté", Toast.LENGTH_SHORT).show();
            } else {
                startActivity(intent);
                finish();
            }
        }
    }
}
