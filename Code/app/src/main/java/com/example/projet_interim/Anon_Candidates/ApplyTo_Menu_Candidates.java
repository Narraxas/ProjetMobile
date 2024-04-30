package com.example.projet_interim.Anon_Candidates;

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
import android.widget.TextView;
import android.widget.Toast;

import com.example.projet_interim.Commun.LoginScreen;
import com.example.projet_interim.Commun.NotifMenu;
import com.example.projet_interim.CurentUser;
import com.example.projet_interim.DB;
import com.example.projet_interim.R;
import com.google.android.material.navigation.NavigationView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class ApplyTo_Menu_Candidates extends AppCompatActivity {

    ActionBarDrawerToggle barToggled;
    NavigationView navView;
    DrawerLayout drawerLayout;

    CurentUser user;
    DB db;

    Button cancel_button;
    Button apply_button;
    Button share_button;
    EditText LM_text;
    TextView title;

    ArrayList<String> annonce;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.apply_to_menu_candidates);

        drawerLayout = (DrawerLayout)findViewById(R.id.drawerLayout_candidate_to_menu);
        navView = (NavigationView) findViewById(R.id.navView);

        cancel_button = (Button)findViewById(R.id.return_button);
        apply_button = (Button)findViewById(R.id.candidate_post_button);
        share_button = (Button)findViewById(R.id.share_post_button);
        LM_text = (EditText) findViewById(R.id.lm_text);
        title = (TextView) findViewById(R.id.annonceTitle_text);

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

        annonce = (ArrayList<String>) getIntent().getExtras().get("annonce");
        title.setText(annonce.get(2));
    }

    public void back(View view){
        finish();
    }

    public void post(View view)
    {
        if(user.id == null){
            Toast.makeText(getApplicationContext(), "Connectez vous pour postuler à l'annonce", Toast.LENGTH_SHORT).show();
        } else {
            db.applyTo(CurentUser.getInstance().id, annonce.get(0), String.valueOf(LM_text.getText()));
            Toast.makeText(getApplicationContext(), "Vous avez postulez pour l'annonce : " + annonce.get(2), Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    public void share(View view){
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);

        String info = "Voici une annonce qui pourrez vous intéresser. Elle est disponible sur l'application mobile INTERIBOY !\n\n" +
                annonce.get(2)+"\n"+
                annonce.get(3)+"\n"+
                annonce.get(4);

        sendIntent.putExtra(Intent.EXTRA_TEXT, info);
        sendIntent.setType("text/plain");

        Intent shareIntent = Intent.createChooser(sendIntent, null);
        startActivity(shareIntent);
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
                intent = new Intent(getApplicationContext(), Profile_Menu_Candidates.class);
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