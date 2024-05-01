package com.example.projet_interim.Commun;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.projet_interim.Candidat.AffichageAnnonces;
import com.example.projet_interim.Candidat.ProfilCandidat;
import com.example.projet_interim.CurentUser;
import com.example.projet_interim.DB;
import com.example.projet_interim.Employeur.ProfilEmployeur;
import com.example.projet_interim.MessageAdaptator;
import com.example.projet_interim.R;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

public class Message extends AppCompatActivity {

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

        listView = findViewById(R.id.notif_listview);
        drawerLayout = findViewById(R.id.drawerLayout_notifMenu);
        navView = findViewById(R.id.navView_notifMenu);
        ecrire_b = findViewById(R.id.ecrire_button);

        user = CurentUser.getInstance();
        db = new DB(getApplicationContext(), this);

        setDrawerMenu();

        barToggled = new ActionBarDrawerToggle(this, drawerLayout, 0, 0);
        barToggled.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        navView.setNavigationItemSelectedListener(item -> {
            gotoMenu(item.getItemId());
            return false;
        });

        msgList = db.getNotifForUserID(user.id);
        MessageAdaptator adapter = new MessageAdaptator(getApplicationContext(), msgList);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            ArrayList<String> notif = msgList.get(position);
            showCustomDialog(notif.get(0), notif.get(3), notif.get(4), notif.get(5));
        });
    }

    private void setDrawerMenu() {
        navView.getMenu().clear();
        switch (user.role) {
            case "candidat":
                navView.inflateMenu(R.menu.candidat_message_menu);
                break;
            case "employeur":
                navView.inflateMenu(R.menu.employeur_message_menu);
                break;
        }
    }

    public void ecrireNotif(View v) {
        Intent intent = new Intent(getApplicationContext(), WriteMessage.class);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return barToggled.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    private void showCustomDialog(String id, String from, String objet, String content) {
        Dialog dialog = new Dialog(Message.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.open_message);

        TextView from_t = dialog.findViewById(R.id.viewNotif_from_t);
        TextView obj_t = dialog.findViewById(R.id.viewNotif_objet_t);
        TextView content_t = dialog.findViewById(R.id.viewNotif_content_t);

        Button del_b = dialog.findViewById(R.id.viewNotif_delete_b);
        Button reply_b = dialog.findViewById(R.id.viewNotif_reply_b);

        from_t.setText("From : " + from);
        obj_t.setText("Objet : " + objet);
        content_t.setText(content);

        del_b.setOnClickListener(view -> {
            db.removeNotifFromId(id);
            msgList = db.getNotifForUserID(user.id);
            MessageAdaptator adapter = new MessageAdaptator(getApplicationContext(), msgList);
            listView.setAdapter(adapter);
            dialog.dismiss();
        });

        reply_b.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), WriteMessage.class);
            intent.putExtra("to", from);
            intent.putExtra("obj", "Re: " + objet);
            startActivity(intent);
            dialog.dismiss();
        });

        dialog.show();
    }

    void gotoMenu(int itemId) {
        Intent intent = null;
        switch (itemId) {
            case R.id.drawer_profil:
                if (user.role.equals("candidat")) {
                    intent = new Intent(getApplicationContext(), ProfilCandidat.class);
                } else if (user.role.equals("employeur")) {
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

        if (intent != null) {
            startActivity(intent);
            finish();
        }
    }
}
