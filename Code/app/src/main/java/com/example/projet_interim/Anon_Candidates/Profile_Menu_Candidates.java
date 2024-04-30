package com.example.projet_interim.Anon_Candidates;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.projet_interim.CandidatureAdaptator;
import com.example.projet_interim.Commun.LoginScreen;
import com.example.projet_interim.Commun.NotifMenu;
import com.example.projet_interim.Commun.RegisterOrModifyInfoMenu;
import com.example.projet_interim.CurentUser;
import com.example.projet_interim.DB;
import com.example.projet_interim.EmployeurAgence.Profil_Menu_Employeur;
import com.example.projet_interim.OfferAdaptator;
import com.example.projet_interim.R;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

public class Profile_Menu_Candidates extends AppCompatActivity {

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

        setContentView(R.layout.profil_candidate);

        listView_offres_attente = (ListView)findViewById(R.id.offre_listview);
        listview_offres_prisent = (ListView)findViewById(R.id.offres_prisent_listview);
        drawerLayout = (DrawerLayout)findViewById(R.id.drawerLayout_profil_candidate);
        navView = (NavigationView) findViewById(R.id.navView);

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

        user = CurentUser.getInstance();
        DB db = new DB(getApplicationContext(), this);

        candidature_attente = db.getCandidatureAnnonceForUserId(user.id);
        candidature_prisent = db.getAnnonces_prisentForUserId(user.id);

        OfferAdaptator adapter1 = new OfferAdaptator(getApplicationContext(),candidature_attente);
        listView_offres_attente.setAdapter(adapter1);
        OfferAdaptator adapter2 = new OfferAdaptator(getApplicationContext(),candidature_prisent);
        listview_offres_prisent.setAdapter(adapter2);

        listview_offres_prisent.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ArrayList<String> candid = (ArrayList<String>)listview_offres_prisent.getItemAtPosition(position);
                Toast.makeText(getApplicationContext(),"offerID : " + candid.get(0),Toast.LENGTH_SHORT).show();

                showDialogCandidature(db.getInfoForCandidatureID(candid.get(0)), candid.get(0), candid.get(2), user.id);
            }
        });
    }

    private void refreshListView(){
        db = new DB(getApplicationContext(), Profile_Menu_Candidates.this);

        candidature_attente = db.getCandidatureAnnonceForUserId(user.id);
        candidature_prisent = db.getAnnonces_prisentForUserId(user.id);

        OfferAdaptator adapter1 = new OfferAdaptator(getApplicationContext(),candidature_attente);
        listView_offres_attente.setAdapter(adapter1);
        OfferAdaptator adapter2 = new OfferAdaptator(getApplicationContext(),candidature_prisent);
        listview_offres_prisent.setAdapter(adapter2);
    }

    private void removeCandidatureFromList(String candidatureID){

        /*db = new DB(getApplicationContext(), Profil_Menu_Employeur.this);

        candidature_prisent = db.getAnnoncesFromCreatorId(user.id);
        OfferAdaptator adapter1 = new OfferAdaptator(getApplicationContext(), candidature_prisent);
        listview_offres_prisent.setAdapter(adapter1);

        candidaturesList = db.getCandidaturesIntendedForUserID(user.id);

        // construction de la list des candidature a afficher
        displayedCandidaturesList.clear();
        for(ArrayList<String> cand : candidaturesList){
            ArrayList<String> displayedCand = new ArrayList<>();
            displayedCand.add(cand.get(0));
            displayedCand.add(db.getUserNameFromID(cand.get(1)));
            displayedCand.add(db.getAnnonceTitleFromId(cand.get(2)));
            displayedCandidaturesList.add(displayedCand);
        }

        CandidatureAdaptator adapter2 = new CandidatureAdaptator(getApplicationContext(), displayedCandidaturesList);
        listView_candidatures.setAdapter(adapter2);


        // Remove the candidature from candidature_attente or candidature_prisent
        for (ArrayList<String> candidature : candidature_attente) {
            if (candidature.get(0).equals(candidatureID)) {
                candidature_attente.remove(candidature);
                break;
            }
        }

        for (ArrayList<String> candidature : candidature_prisent) {
            if (candidature.get(0).equals(candidatureID)) {
                candidature_prisent.remove(candidature);
                break;
            }
        }

        // Update the list views
        refreshListView();*/
    }

    private void showDialogCandidature(ArrayList<String> userInfo, String candidatureID, String annonceTitle, String employeurId){
        AlertDialog.Builder builder = new AlertDialog.Builder(Profile_Menu_Candidates.this);
        builder.setCancelable(true);
        builder.setTitle(annonceTitle);
        builder.setMessage(userInfo.get(4) + " " + userInfo.get(5) + "\n\n" +
                "Né(e) le : " + userInfo.get(6) + "\n" +
                "Nationalité : " + userInfo.get(7) + "\n" +
                "Mail : " + userInfo.get(3) + "\n\n" +
                "Souhaite travailler pour l'annonce : \n" +
                annonceTitle + "\n\n" +
                "CV :\n\n"+
                userInfo.get(8)+"\n\n"+
                "Lettre de motivation :\n\n" +
                userInfo.get(9));
        builder.setPositiveButton("Accepter", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //db.acceptCandidature(candidatureID);
                removeCandidatureFromList(candidatureID);
            }
        });
        builder.setNegativeButton("Refuser", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //db.addNotif(employeurId, userInfo.get(0), "Refus de candidature", "L'offre " + annonceTitle + " vous a été refusé");
                //db.removeCandidatureFromId(candidatureID);
                removeCandidatureFromList(candidatureID);
            }
        });
        builder.setNeutralButton("Envoyer un message au candidat", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        // Create the AlertDialog object and return it
        AlertDialog d = builder.create();

        d.show();
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(barToggled.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    void gotoMenu(int itemId){

        Intent intent = null;
        switch (itemId) {
            case R.id.drawer_modifyInfo:
                intent = new Intent(getApplicationContext(), RegisterOrModifyInfoMenu.class);
                intent.putExtra("role", user.role);
                break;
            case R.id.drawer_annonce:
                intent = new Intent(getApplicationContext(), AnnonceList_Menu_Anon_Candidates.class);
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
            startActivity(intent);
            finish();
        }
    }


}