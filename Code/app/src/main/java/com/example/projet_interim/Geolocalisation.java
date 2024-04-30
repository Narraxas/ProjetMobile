package com.example.projet_interim;

import static androidx.core.content.ContextCompat.getSystemService;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.projet_interim.Anon_Candidates.AnnonceList_Menu_Anon_Candidates;

import java.util.ArrayList;
import java.util.List;

public class Geolocalisation extends AppCompatActivity {
    private static final int REQUEST_CODE_LOCATION_PERMISSION = 1001;
    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.geolocalisation);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // Demander l'autorisation d'accéder à la localisation
        requestLocationPermission();
    }

    private void requestLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Demande de permission d'accéder à la localisation
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_LOCATION_PERMISSION);
        } else {
            // L'autorisation est déjà accordée, obtenir la localisation
            getLocation();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // L'utilisateur a accordé la permission
                getLocation();
            } else {
                // L'utilisateur a refusé la permission, afficher des annonces par défaut
                showDefaultAds();
            }
        }
    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location != null) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            // Enregistrer la localisation dans les préférences de l'application
            saveLocationToPreferences(latitude, longitude);
            // Afficher les annonces basées sur la localisation
            showAdsBasedOnLocation();
        } else {
            // La localisation n'est pas disponible, afficher des annonces par défaut
            showDefaultAds();
        }
    }

    private void saveLocationToPreferences(double latitude, double longitude) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("user_latitude", String.valueOf(latitude));
        editor.putString("user_longitude", String.valueOf(longitude));
        editor.apply();
    }

    private void showAdsBasedOnLocation() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String latitudeString = preferences.getString("user_latitude", null);
        String longitudeString = preferences.getString("user_longitude", null);

        if (latitudeString != null && longitudeString != null) {
            double latitude = Double.parseDouble(latitudeString);
            double longitude = Double.parseDouble(longitudeString);

            Intent intent = new Intent(this, AnnonceList_Menu_Anon_Candidates.class);
            startActivity(intent);
        } else {
            // Gérer le cas où les valeurs de localisation ne sont pas disponibles
            // Peut-être afficher des annonces par défaut ou un message à l'utilisateur
            showDefaultAds();
        }
    }

    private void showDefaultAds() {
        // Sélectionner des annonces par défaut basées sur un algorithme prédéfini
        // List<String> defaultAds = selectDefaultAds();
        // Afficher les annonces sélectionnées dans l'interface utilisateur
        // displayAds(defaultAds);

        Intent intent = new Intent(this, AnnonceList_Menu_Anon_Candidates.class);
        startActivity(intent);
    }

    private List<String> selectDefaultAds() {
        // Implémenter un algorithme pour sélectionner des annonces par défaut
        // Par exemple, récupérer les annonces les plus récentes ou les plus populaires
        // à partir d'une source de données ou générer des annonces aléatoires
        List<String> defaultAds = new ArrayList<>();
        defaultAds.add("Annonce 1");
        defaultAds.add("Annonce 2");
        defaultAds.add("Annonce 3");
        return defaultAds;
    }

    private void displayAds(List<String> ads) {
        // Afficher les annonces dans l'interface utilisateur, par exemple dans un RecyclerView
        // Adapter le RecyclerView avec les annonces récupérées
        for (String ad : ads) {
            Toast.makeText(this, ad, Toast.LENGTH_SHORT).show();
        }
    }
}



