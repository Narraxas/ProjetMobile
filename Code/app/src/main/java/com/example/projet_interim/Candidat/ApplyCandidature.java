package com.example.projet_interim.Candidat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.projet_interim.Commun.Login;
import com.example.projet_interim.Commun.Message;
import com.example.projet_interim.CurentUser;
import com.example.projet_interim.DB;
import com.example.projet_interim.R;
import com.google.android.material.navigation.NavigationView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class ApplyCandidature extends AppCompatActivity {
    private static final int PICK_FILE_REQUEST = 1;

    ActionBarDrawerToggle barToggled;
    NavigationView navView;
    DrawerLayout drawerLayout;

    CurentUser user;
    DB db;

    Button cancel_button;
    Button apply_button;
    Button share_button;
    Button select_file_button;
    TextView file_name_text;
    TextView title;

    ArrayList<String> annonce;
    String fileContent = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.apply_candidature);

        drawerLayout = findViewById(R.id.drawerLayout_candidate_to_menu);
        navView = findViewById(R.id.navView);

        cancel_button = findViewById(R.id.return_button);
        apply_button = findViewById(R.id.candidate_post_button);
        share_button = findViewById(R.id.share_post_button);
        select_file_button = findViewById(R.id.select_file_button);
        file_name_text = findViewById(R.id.file_name_text);
        title = findViewById(R.id.annonceTitle_text);

        // Side Menu
        barToggled = new ActionBarDrawerToggle(this, drawerLayout, 0, 0);
        barToggled.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        navView.setNavigationItemSelectedListener(item -> {
            gotoMenu(item.getItemId());
            return false;
        });

        // init de la db et récupération du user courant
        user = CurentUser.getInstance();
        db = new DB(getApplicationContext(), this);

        annonce = (ArrayList<String>) getIntent().getExtras().get("annonce");
        title.setText(annonce.get(2));

        select_file_button.setOnClickListener(view -> openFilePicker());
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        startActivityForResult(intent, PICK_FILE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FILE_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri fileUri = data.getData();
            if (fileUri != null) {
                String fileName = getFileName(fileUri);
                file_name_text.setText(fileName);
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

    private String readFileContent(Uri uri) {
        StringBuilder stringBuilder = new StringBuilder();
        try (InputStream inputStream = getContentResolver().openInputStream(uri);
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line).append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    public void back(View view) {
        finish();
    }

    public void post(View view) {
        if (user.id == null) {
            Toast.makeText(getApplicationContext(), "Connectez vous pour postuler à l'annonce", Toast.LENGTH_SHORT).show();
        } else if (fileContent == null) {
            Toast.makeText(getApplicationContext(), "Veuillez sélectionner un fichier", Toast.LENGTH_SHORT).show();
        } else {
            db.applyTo(CurentUser.getInstance().id, annonce.get(0), fileContent);
            Toast.makeText(getApplicationContext(), "Vous avez postulez pour l'annonce : " + annonce.get(2), Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    public void share(View view) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);

        String info = "Voici une annonce qui pourrez vous intéresser. Elle est disponible sur l'application mobile INTERIBOY !\n\n" +
                annonce.get(2) + "\n" +
                annonce.get(3) + "\n" +
                annonce.get(4);

        sendIntent.putExtra(Intent.EXTRA_TEXT, info);
        sendIntent.setType("text/plain");

        Intent shareIntent = Intent.createChooser(sendIntent, null);
        startActivity(shareIntent);
    }

    // Permet d'ouvrir le Side Menu
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return barToggled.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    // Change d'Activity
    void gotoMenu(int itemId) {
        Intent intent = null;
        switch (itemId) {
            case R.id.drawer_profil:
                intent = new Intent(getApplicationContext(), ProfilCandidat.class);
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
            if (user.id == null) {
                Toast.makeText(getApplicationContext(), "Vous n'êtes pas connecté", Toast.LENGTH_SHORT).show();
            } else {
                startActivity(intent);
                finish();
            }
        }
    }
}