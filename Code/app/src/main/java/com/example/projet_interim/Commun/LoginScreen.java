package com.example.projet_interim.Commun;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.projet_interim.Anon_Candidates.AnnonceList_Menu_Anon_Candidates;
import com.example.projet_interim.Anon_Candidates.Profile_Menu_Candidates;
import com.example.projet_interim.CurentUser;
import com.example.projet_interim.DB;
import com.example.projet_interim.EmployeurAgence.Profil_Menu_Employeur;
import com.example.projet_interim.R;

import java.util.ArrayList;

public class LoginScreen extends AppCompatActivity {

    EditText loging_text;
    Button login_button;
    ImageView image;
    DB db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_screen);

        DB.exempleFillIfEmpty(getApplicationContext(), this);

        loging_text = (EditText) findViewById(R.id.login_text);
        login_button = (Button) findViewById(R.id.login_button);
        image = (ImageView) findViewById(R.id.imageView);

        db = new DB(getApplicationContext(), this);

        image.setRotation(2f);

        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String login = String.valueOf(loging_text.getText());
                ArrayList<String> user = db.getUserByName(login);

                if(user != null){
                    CurentUser.getInstance().id = user.get(0);
                    CurentUser.getInstance().username = user.get(1);
                    CurentUser.getInstance().role = user.get(2);

                    Toast.makeText(getApplicationContext(), user.get(0) + " " + user.get(1) + " " + user.get(2), Toast.LENGTH_SHORT).show();
                    gotoMenu();
                } else {
                    Toast.makeText(getApplicationContext(), "Mauvais nom d'utilisateur / mot de passe", Toast.LENGTH_SHORT).show();
                }
            }
        });

        final Handler handler = new Handler();

        Runnable runnable = new Runnable() {
            float x = 0;
            @Override
            public void run() {
                image.setRotationX((float) Math.cos(x));
                image.setRotationY((float) Math.cos(x+Math.PI/2));
                x += 0.02;
                handler.postDelayed(this, 10);
            }
        };

        handler.post(runnable);

        Log.e("DB", db.users.toString());
    }

    public void register(View view){
        Intent intent = new Intent(getApplicationContext(), RegisterMenu1.class);
        startActivityForResult(intent, 1);
    }

    public void anonConnect(View view){
        Toast.makeText(getApplicationContext(), "Vous êtes connecté en anonyme", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(LoginScreen.this, AnnonceList_Menu_Anon_Candidates.class);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                db = new DB(getApplicationContext(), this);
            }
        }
    }

    public void gotoMenu(){

        Intent intent = null;

        switch (CurentUser.getInstance().role) {
            case "candidat":
                intent = new Intent(getApplicationContext(), Profile_Menu_Candidates.class);
                break;
            case "employeur":
                intent = new Intent(getApplicationContext(), Profil_Menu_Employeur.class);
                break;
            case "agence":
                intent = new Intent(getApplicationContext(), Profil_Menu_Employeur.class);
                break;
            case "admin":
                Toast.makeText(getApplicationContext(), "admin menu", Toast.LENGTH_SHORT).show();
                break;
        }

        if(intent != null){
            startActivity(intent);
            finish();
        }
    }
}
