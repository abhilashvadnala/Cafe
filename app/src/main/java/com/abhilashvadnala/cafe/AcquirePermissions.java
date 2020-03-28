package com.abhilashvadnala.cafe;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class AcquirePermissions extends AppCompatActivity {

    int index = 0;

    String[] permissions = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_WIFI_STATE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acquire_permissions);
        checkForPermission(permissions[index], index);
    }



    private void checkForPermission(String permission, int RESULT_CODE) {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this, permission)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{permission},
                        RESULT_CODE);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted
            index++;
            if(index == permissions.length) {
                goToHomeScreen();
            } else {
                checkForPermission(permissions[index],index);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if(grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            index++;
            if(index == permissions.length) {
                goToHomeScreen();
            } else {
                checkForPermission(permissions[index],index);
            }
        } else {
            showError("Please restart app and grant all permissions");
        }
        return;
    }

    private void goToHomeScreen() {
        Intent mapScreen = new Intent(this, MapActivity.class);
        startActivity(mapScreen);
    }

    private void showError(String string) {
        View contextView = findViewById(android.R.id.content);
        Snackbar.make(contextView, string, Snackbar.LENGTH_LONG).show();
    }
}
