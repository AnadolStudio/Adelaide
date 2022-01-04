package com.anadolstudio.adelaide.model;

import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO;

import android.content.Context;
import android.preference.PreferenceManager;
import android.util.Log;


public class SettingsPreference {
    public static final String TAG = SettingsPreference.class.getName();

    public static final String FIRST_RUN_APP = "first_run_app";
    public static final String PREMIUM = "premium";
    public static final String NIGHT_MODE = "premium";
    public static final String HISTORY_PURCHASE = "history_purchase";
    public static final String DESTROY = "destroy";


    public static boolean isFirstRunApp(Context context) {
        boolean b = PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(FIRST_RUN_APP, true);
        if (b) {
            Log.d(TAG, "isFirstRunApp");
        }
        return b;
    }

    public static void appFirstRun(Context context) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(FIRST_RUN_APP, false)
                .apply();


    }

    public static boolean hasPremium(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(PREMIUM, false);
    }

    public static void setPremium(Context context, boolean isPremium) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(PREMIUM, isPremium)
                .apply();

    }

    public static int getNightMode(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getInt(NIGHT_MODE, MODE_NIGHT_FOLLOW_SYSTEM);
    }

    public static void setNightMode(Context context, int mode) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putInt(NIGHT_MODE, mode)
                .apply();

    }

}
