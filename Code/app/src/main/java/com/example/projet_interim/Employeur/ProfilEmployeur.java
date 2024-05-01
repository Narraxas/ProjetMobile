package com.example.projet_interim.Employeur;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.projet_interim.CandidatureAdaptator;
import com.example.projet_interim.Commun.Login;
import com.example.projet_interim.Commun.Message;
import com.example.projet_interim.Commun.RegisterFormulaire;
import com.example.projet_interim.CurentUser;
import com.example.projet_interim.DB;
import com.example.projet_interim.OfferAdaptator;
import com.example.projet_interim.R;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

public class ProfilEmployeur extends AppCompatActivity {

    CurentUser user;
    DB db;
    ActionBarDrawerToggle barToggle;

    ListView listView_offres;
    ListView listView_candidatures;
    ArrayList<ArrayList<String>> candidaturesList = new ArrayList<>();
    ArrayList<ArrayList<String>> displayedCandidaturesList = new ArrayList<>();
    ArrayList<ArrayList<String>> offresList = new ArrayList<>();
    DrawerLayout drawerLayout;
    NavigationView navView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profil_employeur);

        listView_offres = findViewById(R.id.offres_listview_EmployeurAgence);
        listView_candidatures = findViewById(R.id.candidatures_listview_EmployeurAgence);
        drawerLayout = findViewById(R.id.drawerLayout_profil_employeurAgence);
        navView = findViewById(R.id.navView);


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


        user = CurentUser.getInstance();
        db = new DB(getApplicationContext(), this);


        refreshListView();


        listView_offres.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ArrayList<String> offre = offresList.get(position);
                Toast.makeText(getApplicationContext(),"offerID : " + offre.get(0),Toast.LENGTH_SHORT).show();
                showDialogOffre(offre.get(0), offre.get(2));
            }
        });


        listView_candidatures.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ArrayList<String> candid = candidaturesList.get(position);
                Toast.makeText(getApplicationContext(),"offerID : " + candid.get(0),Toast.LENGTH_SHORT).show();
                showDialogCandidature(db.getInfoForCandidatureID(candid.get(0)), candid.get(0), candid.get(2), user.id);
            }
        });
    }

    public void addOffer(View v){
        Intent intent = new Intent(getApplicationContext(), AddAnnonce.class);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            refreshListView();
        }
    }

    private void showDialogOffre(String offerId, String title){
        AlertDialog.Builder builder = new AlertDialog.Builder(ProfilEmployeur.this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage("Voulez-vous supprimer ou modifier cette offre ?");
        builder.setPositiveButton("Supprimer", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                db.removeAnnonceFromId(offerId);
                refreshListView();
            }
        });
        builder.setNeutralButton("Modifier", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                View dialogView = LayoutInflater.from(ProfilEmployeur.this).inflate(R.layout.modify_offer, null);
                EditText editOfferTitle = dialogView.findViewById(R.id.edit_offer_title);
                EditText editOfferContent = dialogView.findViewById(R.id.edit_offer_content);

                AlertDialog.Builder modifyDialogBuilder = new AlertDialog.Builder(ProfilEmployeur.this);
                modifyDialogBuilder.setView(dialogView);
                modifyDialogBuilder.setTitle("Modifier l'offre");
                modifyDialogBuilder.setPositiveButton("Enregistrer", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        String newTitle = editOfferTitle.getText().toString();
                        String newContent = editOfferContent.getText().toString();

                        db.updateOffer(offerId, newTitle, newContent);

                        for (ArrayList<String> annonce : db.getAnnonce()) {
                            String id = annonce.get(0);
                            String titre = annonce.get(1);
                            String contenu = annonce.get(2);
                            System.out.println("ID: " + id + ", Titre: " + titre + ", Contenu: " + contenu);
                        }

                        refreshListView();

                    }
                });
                modifyDialogBuilder.setNegativeButton("Annuler", null);
                modifyDialogBuilder.show();
            }
        });
        builder.setNegativeButton("Annuler", null);
        AlertDialog d = builder.create();

        d.show();
    }

    // Info = {user_ID, username, role, mail, nom, prenom, date de naissance, nationalité, CV, LM}
    private void showDialogCandidature(ArrayList<String> userInfo, String candidatureID, String annonceTitle, String employeurId){
        AlertDialog.Builder builder = new AlertDialog.Builder(ProfilEmployeur.this);
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
                db.acceptCandidature(candidatureID);
                refreshListView();
            }
        });
        builder.setNegativeButton("Refuser", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                db.addNotif(employeurId, userInfo.get(0), "Refus de candidature", "L'offre " + annonceTitle + " vous a été refusé");
                db.removeCandidatureFromId(candidatureID);
                refreshListView();
            }
        });
        builder.setNeutralButton("Envoyer un message au candidat", (dialogInterface, i) -> {
            startActivity(new Intent(getApplicationContext(), Message.class));

        });

        AlertDialog d = builder.create();

        d.show();
    }

    private void refreshListView(){
        db = new DB(getApplicationContext(), ProfilEmployeur.this);
        offresList = db.getAnnoncesFromCreatorId(user.id);
        OfferAdaptator adapter1 = new OfferAdaptator(getApplicationContext(), offresList);
        listView_offres.setAdapter(adapter1);

        candidaturesList = db.getCandidaturesIntendedForUserID(user.id);
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
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(barToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    void gotoMenu(int itemId){
        Intent intent = null;
        switch (itemId) {
            case R.id.drawer_modifyInfo:
                intent = new Intent(getApplicationContext(), RegisterFormulaire.class);
                intent.putExtra("role", user.role);
                break;
            case R.id.drawer_annonce:
                //intent = new Intent(getApplicationContext(), AnnonceList_Menu_Anon_Candidates.class);
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

        if(intent != null){
            startActivity(intent);
            finish();
        }
    }
}
