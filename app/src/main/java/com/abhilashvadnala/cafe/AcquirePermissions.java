package com.abhilashvadnala.cafe;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.List;

public class AcquirePermissions extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acquire_permissions);
    }

    private void acquirePermissions() {
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.INTERNET,
                        Manifest.permission.ACCESS_NETWORK_STATE,
                        Manifest.permission.ACCESS_WIFI_STATE
                ).withListener(new MultiplePermissionsListener() {
            @Override public void onPermissionsChecked(MultiplePermissionsReport report) {
                if(report.areAllPermissionsGranted() == false) {
                    showError("Please grant required permissions and try again.");
                    if(report.isAnyPermissionPermanentlyDenied()) {
                        showError("Go to Settings and grant required permissions");
                    }
                } else {
                    goToHomeScreen();
                }
            }
            @Override public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

            }
        }).check();
    }

    private void goToHomeScreen() {
        Intent mapScreen = new Intent(this, MainActivity.class);
        startActivity(mapScreen);
    }

    private void showError(String string) {
        View contextView = findViewById(android.R.id.content);
        Snackbar.make(contextView, string, Snackbar.LENGTH_LONG).show();
    }
}
