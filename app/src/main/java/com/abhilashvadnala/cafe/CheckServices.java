package com.abhilashvadnala.cafe;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.material.snackbar.Snackbar;

public class CheckServices extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_services);
        checkServices();
    }

    private void checkServices() {
        int flags = 0;

        if(checkNetwork(this)) flags++;
        else {
            showError("You're offline. Try connecting to a network!!");
            return;
        }

        if(checkPlayServices(this)) flags++;
        else {
            showError("Google Play Services is disabled. Please enable it.");
            return;
        }

        if(checkGPS()) flags++;
        else {
            showError("Your Location is OFF. Please Enable GPS.");
            return;
        }

        if(flags==3)
            acquirePermissions();
    }

    private void acquirePermissions() {
        Intent permissions = new Intent(this, AcquirePermissions.class);
        startActivity(permissions);
        finish();
    }

    private boolean checkNetwork(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    private boolean checkGPS() {
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean gps_on = false;
        boolean network_on = false;

        try{
            gps_on = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception e) {}

        try{
            network_on = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch(Exception e ) {}

        return gps_on && network_on;
    }

    private boolean checkPlayServices(Context context) {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int status = googleApiAvailability.isGooglePlayServicesAvailable(context);
        return status == ConnectionResult.SUCCESS;
    }

    private void showError(String string) {
        View contextView = findViewById(android.R.id.content);
        Snackbar.make(contextView, string, Snackbar.LENGTH_LONG).show();
    }

}
