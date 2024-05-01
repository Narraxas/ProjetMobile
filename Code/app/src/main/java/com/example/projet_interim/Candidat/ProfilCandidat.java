package com.example.projet_interim.Candidat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.example.projet_interim.Commun.Login;
import com.example.projet_interim.Commun.Message;
import com.example.projet_interim.Commun.RegisterFormulaire;
import com.example.projet_interim.CurentUser;
import com.example.projet_interim.DB;
import com.example.projet_interim.OfferAdaptator;
import com.example.projet_interim.R;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

public class ProfilCandidat extends AppCompatActivity {

    CurentUser user;
    ActionBarDrawerToggle barToggled;
    ListView listView_offres_attente;
    ListView listview_offres_prisent;
    ArrayList<ArrayList<String>> candidature_attente = new ArrayList<>();
    ArrayList<ArrayList<String>> candidature_prisent = new ArrayList<>();
    DrawerLayout drawerLayout;
    NavigationView navView;
    DB db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profil_candidat);

        listView_offres_attente = findViewById(R.id.offre_listview);
        listview_offres_prisent = findViewById(R.id.offres_prisent_listview);
        drawerLayout = findViewById(R.id.drawerLayout_profil_candidate);
        navView = findViewById(R.id.navView);

        barToggled = new ActionBarDrawerToggle(this, drawerLayout, 0, 0);
        barToggled.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        navView.setNavigationItemSelectedListener(item -> {
            gotoMenu(item.getItemId());
            return false;
        });

        user = CurentUser.getInstance();
        db = new DB(getApplicationContext(), this);

        candidature_attente = db.getCandidatureAnnonceForUserId(user.id);
        candidature_prisent = db.getAnnonces_prisentForUserId(user.id);

        OfferAdaptator adapter1 = new OfferAdaptator(getApplicationContext(), candidature_attente);
        listView_offres_attente.setAdapter(adapter1);
        OfferAdaptator adapter2 = new OfferAdaptator(getApplicationContext(), candidature_prisent);
        listview_offres_prisent.setAdapter(adapter2);

        listview_offres_prisent.setOnItemClickListener((parent, view, position, id) -> {
            ArrayList<String> candid = (ArrayList<String>) listview_offres_prisent.getItemAtPosition(position);
            Toast.makeText(getApplicationContext(), "offerID : " + candid.get(0), Toast.LENGTH_SHORT).show();
            showDialogCandidature(db.getInfoForCandidatureID(candid.get(0)), candid.get(0), candid.get(2), user.id);
        });
    }

    private void removeCandidatureFromList(String candidatureID) {
        candidature_prisent.removeIf(candidature -> candidature.get(0).equals(candidatureID));
        OfferAdaptator adapter2 = new OfferAdaptator(getApplicationContext(), candidature_prisent);
        listview_offres_prisent.setAdapter(adapter2);
    }

    private void showDialogCandidature(ArrayList<String> userInfo, String candidatureID, String annonceTitle, String employeurId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ProfilCandidat.this);
        builder.setCancelable(true);
        builder.setTitle(annonceTitle);

        builder.setPositiveButton("Accepter", (dialog, id) -> removeCandidatureFromList(candidatureID));
        builder.setNegativeButton("Refuser", (dialog, id) -> removeCandidatureFromList(candidatureID));
        builder.setNeutralButton("Envoyer un message à l'employeur", (dialogInterface, i) -> {
            startActivity(new Intent(getApplicationContext(), Message.class));

        });

        AlertDialog d = builder.create();
        d.show();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return barToggled.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    void gotoMenu(int itemId) {
        Intent intent = null;
        switch (itemId) {
            case R.id.drawer_modifyInfo:
                intent = new Intent(getApplicationContext(), RegisterFormulaire.class);
                intent.putExtra("role", user.role);
                break;
            case R.id.drawer_annonce:
                intent = new Intent(getApplicationContext(), AffichageAnnonces.class);
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
            startActivity(intent);
            finish();
        }
    }
}
