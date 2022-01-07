package com.anadolstudio.adelaide.helpers;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.anadolstudio.adelaide.R;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

public class PermissionHelper {
    public static final int REQUEST_STORAGE_PERMISSION = 1;
    public static final String[] STORAGE_PERMISSION = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private static final String TAG = PermissionHelper.class.getName();

    public static void requestPermission(AppCompatActivity activity, String permission, int requestCode) {
        ActivityCompat.requestPermissions(
                activity,
                new String[]{permission},
                requestCode);
    }

    public static void requestPermission(AppCompatActivity activity, String[] permissions, int requestCode) {
        ActivityCompat.requestPermissions(
                activity,
                permissions,
                requestCode);
    }

    public static boolean hasPermission(AppCompatActivity activity, String permission) {
        int result = ContextCompat.checkSelfPermission(activity, permission);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean hasPermission(AppCompatActivity activity, String[] permissions) {
        for (String p : permissions) {
            if (!hasPermission(activity, p)) {
                return false;
            }
        }
        return true;
    }

    public static void showSettingsSnackbar(AppCompatActivity activity, View rootView) {
        Snackbar snackbar = Snackbar.make(
                rootView,
                activity.getText(R.string.get_permission),
                BaseTransientBottomBar.LENGTH_INDEFINITE);
        snackbar.setAction(R.string.settings, (v -> startAppSettingsActivity(activity)));
        snackbar.show();
    }

    public static void startAppSettingsActivity(AppCompatActivity activity) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
        intent.setData(uri);
        activity.startActivity(intent);
    }
}
