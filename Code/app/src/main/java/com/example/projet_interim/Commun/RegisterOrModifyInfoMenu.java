package com.example.projet_interim.Commun;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.projet_interim.CurentUser;
import com.example.projet_interim.DB;

import java.util.ArrayList;

public class RegisterOrModifyInfoMenu extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DB db = new DB(getApplicationContext(), this);
        CurentUser user = CurentUser.getInstance();
        String role = getIntent().getExtras().getString("role");

        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        EditText username_t = new EditText(this);
        username_t.setHint("Nom d'utilisateur");

        EditText mail_t = new EditText(this);
        mail_t.setHint("Mail");

        EditText mdp_t = new EditText(this);
        mdp_t.setHint("Mot de passe");

        Button bouton = new Button(this);

        if(user.id == null){
            bouton.setText("S'inscrire");
        } else {
            bouton.setText("Modifier le profil");
        }


        switch (role) {
            case "candidat":

                EditText nom_t = new EditText(this);
                nom_t.setHint("Nom *");

                EditText prenom_t = new EditText(this);
                prenom_t.setHint("Prénom *");

                EditText dateNaissance_t = new EditText(this);
                dateNaissance_t.setHint("Date de naissance");

                EditText natio_t = new EditText(this);
                natio_t.setHint("Nationalité");

                EditText cv_t = new EditText(this);
                cv_t.setHint("CV");
                //Numéro de tel
                // Commentaires


                bouton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String un = String.valueOf(username_t.getText());
                        String m = String.valueOf(mail_t.getText());
                        String n = String.valueOf(nom_t.getText());
                        String p = String.valueOf(prenom_t.getText());
                        String dn = String.valueOf(dateNaissance_t.getText());
                        String na = String.valueOf(natio_t.getText());
                        String cv = String.valueOf(cv_t.getText());

                        if(user.id == null){
                            if(un.equals("") || m.equals("") || n.equals("") || p.equals("")){
                                Toast.makeText(getApplicationContext(),"Champ vide", Toast.LENGTH_LONG).show();
                                return;
                            }

                            if(db.getUserByName(un) != null){
                                Toast.makeText(getApplicationContext(),"Nom d'utilisateur déjà utilisé", Toast.LENGTH_LONG).show();
                                return;
                            }

                            db.addCandidate(un, m, n, p, dn, na, cv);
                            Toast.makeText(getApplicationContext(),"Bienvenue " + p, Toast.LENGTH_LONG).show();
                            finish();
                        } else {
                            ArrayList userInfo = new ArrayList<>();
                            userInfo.add(user.role);
                            userInfo.add(m);
                            userInfo.add(n);
                            userInfo.add(p);
                            userInfo.add(dn);
                            userInfo.add(na);
                            userInfo.add(cv);

                            db.modifyUserInfo(user.id, userInfo);
                            finish();
                        }


                    }});

                if(user.id == null){
                    linearLayout.addView(username_t);
                }
                linearLayout.addView(mail_t);
                linearLayout.addView(mdp_t);
                linearLayout.addView(nom_t);
                linearLayout.addView(prenom_t);
                linearLayout.addView(dateNaissance_t);
                linearLayout.addView(natio_t);
                linearLayout.addView(cv_t);
                linearLayout.addView(bouton);

                break;
            case "employeur":
            case "agence":
                // String username, String role, String mail, String nomEntrep, String nomServiceDepartement, String nomSousServiceDepartement, String siren, String mail2
                EditText nomEnt_t = new EditText(this);
                nomEnt_t.setHint("Nom de l'entreprise");

                EditText nomServ_t = new EditText(this);
                nomServ_t.setHint("Nom du service / département");

                EditText nomSServ_t = new EditText(this);
                nomSServ_t.setHint("Nom du sous service / département");

                EditText siren_t = new EditText(this);
                siren_t.setHint("N° SIREN");

                EditText mail2_t = new EditText(this);
                mail2_t.setHint("Mail 2");

                bouton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String un = String.valueOf(username_t.getText());
                        String m = String.valueOf(mail_t.getText());
                        String ne = String.valueOf(nomServ_t.getText());
                        String ns = String.valueOf(nomServ_t.getText());
                        String nss = String.valueOf(nomSServ_t.getText());
                        String s = String.valueOf(siren_t.getText());
                        String m2 = String.valueOf(mail2_t.getText());

                        if(user.id == null){
                            if(un.equals("") || ne.equals("") || m.equals("") || ns.equals("") || s.equals("")){
                                Toast.makeText(getApplicationContext(),"Champ vide", Toast.LENGTH_LONG).show();
                                return;
                            }

                            if(db.getUserByName(un) != null){
                                Toast.makeText(getApplicationContext(),"Nom d'utilisateur déjà utilisé", Toast.LENGTH_LONG).show();
                                return;
                            }

                            db.addEmployerAgency(un, role, m, ne, ns, nss, s, m2);

                            Toast.makeText(getApplicationContext(),"Bienvenu " + un, Toast.LENGTH_LONG).show();
                            finish();
                        } else {
                            ArrayList userInfo = new ArrayList<>();
                            userInfo.add(user.role);
                            userInfo.add(m);
                            userInfo.add(ne);
                            userInfo.add(ns);
                            userInfo.add(nss);
                            userInfo.add(s);
                            userInfo.add(m2);

                            db.modifyUserInfo(user.id, userInfo);
                            finish();
                        }
                    }});

                if(user.id == null){
                    linearLayout.addView(username_t);
                }
                linearLayout.addView(mail_t);
                linearLayout.addView(mdp_t);
                linearLayout.addView(nomServ_t);
                linearLayout.addView(nomSServ_t);
                linearLayout.addView(siren_t);
                linearLayout.addView(mail2_t);
                linearLayout.addView(bouton);

                break;
            case "admin":

                bouton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String un = String.valueOf(username_t.getText());
                        String m = String.valueOf(mail_t.getText());

                        if(un.equals("") || m.equals("")){
                            Toast.makeText(getApplicationContext(),"Champ vide", Toast.LENGTH_LONG).show();
                            return;
                        }

                        if(db.getUserByName(un) != null){
                            Toast.makeText(getApplicationContext(),"Nom d'utilisateur déjà utilisé", Toast.LENGTH_LONG).show();
                            return;
                        }

                        db.addAdmin(un, m);
                        Toast.makeText(getApplicationContext(),"Bienvenu " + un, Toast.LENGTH_LONG).show();
                        finish();
                    }});

                linearLayout.addView(username_t);
                linearLayout.addView(mail_t);
                linearLayout.addView(mdp_t);
                linearLayout.addView(bouton);
                break;
        }

        setContentView(linearLayout);
    }
}