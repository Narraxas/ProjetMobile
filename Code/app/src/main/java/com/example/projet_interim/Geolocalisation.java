package com.example.projet_interim;

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

import com.example.projet_interim.Candidat.AffichageAnnonces;

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

        requestLocationPermission();
    }

    private void requestLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_LOCATION_PERMISSION);
        } else {
            getLocation();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocation();
            } else {
                showDefaultAds();
            }
        }
    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location != null) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            saveLocationToPreferences(latitude, longitude);
            showAdsBasedOnLocation();
        } else {
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
            Intent intent = new Intent(this, AffichageAnnonces.class);
            startActivity(intent);
        } else {
            showDefaultAds();
        }
    }

    private void showDefaultAds() {
        Intent intent = new Intent(this, AffichageAnnonces.class);
        startActivity(intent);
    }

    private List<String> selectDefaultAds() {
        List<String> defaultAds = new ArrayList<>();
        defaultAds.add("Ad 1");
        defaultAds.add("Ad 2");
        defaultAds.add("Ad 3");
        return defaultAds;
    }

    private void displayAds(List<String> ads) {
        for (String ad : ads) {
            Toast.makeText(this, ad, Toast.LENGTH_SHORT).show();
        }
    }
}
