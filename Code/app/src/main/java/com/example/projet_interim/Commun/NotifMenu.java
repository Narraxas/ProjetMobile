package com.example.projet_interim.Commun;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
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

import org.w3c.dom.Text;

import java.util.ArrayList;

public class NotifMenu extends AppCompatActivity {

    ActionBarDrawerToggle barToggled;
    DrawerLayout drawerLayout;
    NavigationView navView;

    ListView listView;
    ArrayList<ArrayList<String>> msgList;
    Button ecrire_b;

    CurentUser user;
    DB db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.notif_menu);

        listView = (ListView)findViewById(R.id.notif_listview);
        drawerLayout = (DrawerLayout)findViewById(R.id.drawerLayout_notifMenu);
        navView = (NavigationView) findViewById(R.id.navView_notifMenu);
        ecrire_b = (Button) findViewById(R.id.ecrire_button);

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

        // init de la liste des annonces
        msgList = db.getNotifForUserID(user.id);
        NotifAdaptator adapter = new NotifAdaptator(getApplicationContext(),msgList);
        listView.setAdapter(adapter);

        // Capture l'item cliqué dans la listView
        listView.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ArrayList<String> notif = (ArrayList<String>)listView.getItemAtPosition(position);
                showCustoDialog(notif.get(0), notif.get(3), notif.get(4), notif.get(5));
            }
        });
    }

    public void ecrireNotif(View v){
        Intent intent = new Intent(getApplicationContext(), WriteNotifMenu.class);
        startActivity(intent);
    }

    // Permet d'ouvrir le Side Menu
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(barToggled.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showCustoDialog(String id, String from, String objet, String content){
        Dialog dialog = new Dialog(NotifMenu.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.open_notif_dialog);

        TextView from_t = (TextView) dialog.findViewById(R.id.viewNotif_from_t);
        TextView obj_t = (TextView)dialog.findViewById(R.id.viewNotif_objet_t);
        TextView content_t = (TextView)dialog.findViewById(R.id.viewNotif_content_t);

        Button del_b = (Button) dialog.findViewById(R.id.viewNotif_delete_b);
        Button reply_b = (Button) dialog.findViewById(R.id.viewNotif_reply_b);

        from_t.setText("From : " + from);
        obj_t.setText("Objet : " + objet);
        content_t.setText(content);

        del_b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.removeNotifFromId(id);
                msgList = db.getNotifForUserID(user.id);
                NotifAdaptator adapter = new NotifAdaptator(getApplicationContext(),msgList);
                listView.setAdapter(adapter);
                dialog.dismiss();
            }
        });

        reply_b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), WriteNotifMenu.class);
                intent.putExtra("to", from);
                intent.putExtra("obj", "Re: " + objet);
                startActivity(intent);
                dialog.dismiss();
            }
        });

        dialog.show();
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