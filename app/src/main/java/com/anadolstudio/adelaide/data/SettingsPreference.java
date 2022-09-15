package com.anadolstudio.adelaide.data;

import android.content.Context;
import android.preference.PreferenceManager;

import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;


public class SettingsPreference {
    public static final String TAG = SettingsPreference.class.getName();

    public static final String FIRST_RUN_APP = "first_run_app";
    public static final String FIRST_SHARE_ACTIVITY_OPEN = "first_share_activity_open";
    public static final String PREMIUM = "premium";
    public static final String NIGHT_MODE = "night_mode";


    public static boolean isFirstRunApp(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(FIRST_RUN_APP, true);
    }

    public static void appFirstRun(Context context) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(FIRST_RUN_APP, false)
                .apply();
    }

    public static boolean isFirstRunShareActivity(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(FIRST_SHARE_ACTIVITY_OPEN, true);
    }

    public static void firstRunShareActivity(Context context) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(FIRST_SHARE_ACTIVITY_OPEN, false)
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
