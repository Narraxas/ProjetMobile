package com.example.projet_interim.Commun;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.projet_interim.CurentUser;
import com.example.projet_interim.DB;

import java.util.ArrayList;

public class RegisterFormulaire extends Activity {
    private static final int PICK_FILE_REQUEST = 1;
    private Uri fileUri;
    private TextView cvTextView;
    private String fileContent;

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

                Button cvButton = new Button(this);
                cvButton.setText("Sélectionner CV");

                cvTextView = new TextView(this);
                Log.d("FilePicker", "Nom du fichier : " + cvTextView);

                cvButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.setType("*/*");
                        startActivityForResult(intent, PICK_FILE_REQUEST);
                    }
                });

                bouton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String un = String.valueOf(username_t.getText());
                        String m = String.valueOf(mail_t.getText());
                        String n = String.valueOf(nom_t.getText());
                        String p = String.valueOf(prenom_t.getText());
                        String dn = String.valueOf(dateNaissance_t.getText());
                        String na = String.valueOf(natio_t.getText());

                        if (fileUri == null) {
                            Toast.makeText(getApplicationContext(), "Veuillez sélectionner un fichier CV", Toast.LENGTH_LONG).show();
                            return;
                        }

                        String cv = fileContent;

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
                    }
                });

                if(user.id == null){
                    linearLayout.addView(username_t);
                }
                linearLayout.addView(mail_t);
                linearLayout.addView(mdp_t);
                linearLayout.addView(nom_t);
                linearLayout.addView(prenom_t);
                linearLayout.addView(dateNaissance_t);
                linearLayout.addView(natio_t);
                linearLayout.addView(cvButton);
                linearLayout.addView(cvTextView);
                linearLayout.addView(bouton);

                break;

            case "employeur":
                EditText nomEnt_t = new EditText(this);
                nomEnt_t.setHint("Nom de l'entreprise *");

                EditText nomServ_t = new EditText(this);
                nomServ_t.setHint("Nom du service / département");

                EditText nomSServ_t = new EditText(this);
                nomSServ_t.setHint("Nom du sous service / département");

                EditText siren_t = new EditText(this);
                siren_t.setHint("N° SIREN");

                EditText mail2_t = new EditText(this);
                mail2_t.setHint("Mail");

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

                            Toast.makeText(getApplicationContext(),"Bienvenue " + un, Toast.LENGTH_LONG).show();
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
                    }
                });

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
        }

        setContentView(linearLayout);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FILE_REQUEST && resultCode == RESULT_OK && data != null) {
            fileUri = data.getData();
            if (fileUri != null) {
                String fileName = getFileName(fileUri);
                cvTextView.setText(fileName);
                // fileContent = readFileContent(fileUri);
                fileContent = fileName;
            }
        }
    }

    @SuppressLint("Range")
    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }



}