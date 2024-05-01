package com.example.projet_interim.Commun;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.projet_interim.Candidat.AffichageAnnonces;
import com.example.projet_interim.Candidat.ProfilCandidat;
import com.example.projet_interim.CurentUser;
import com.example.projet_interim.DB;
import com.example.projet_interim.Employeur.ProfilEmployeur;
import com.example.projet_interim.R;

import java.util.ArrayList;

public class Login extends AppCompatActivity {

    EditText login_text;
    Button login_button;
    ImageView image;
    DB db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        DB.exempleFillIfEmpty(getApplicationContext(), this);

        login_text = findViewById(R.id.login_text);
        login_button = findViewById(R.id.login_button);
        image = findViewById(R.id.imageView);

        db = new DB(getApplicationContext(), this);

        image.setRotation(2f);

        login_button.setOnClickListener(view -> {
            String login = login_text.getText().toString();
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
    }

    public void register(View view){
        Intent intent = new Intent(getApplicationContext(), RegisterMenu.class);
        startActivityForResult(intent, 1);
    }

    public void anonConnect(View view){
        Toast.makeText(getApplicationContext(), "Vous êtes connecté en anonyme", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(Login.this, AffichageAnnonces.class);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            db = new DB(getApplicationContext(), this);
        }
    }

    public void gotoMenu(){
        Intent intent = null;

        switch (CurentUser.getInstance().role) {
            case "candidat":
                intent = new Intent(getApplicationContext(), ProfilCandidat.class);
                break;
            case "employeur":
                intent = new Intent(getApplicationContext(), ProfilEmployeur.class);
                break;
        }

        if(intent != null){
            startActivity(intent);
            finish();
        }
    }
}
