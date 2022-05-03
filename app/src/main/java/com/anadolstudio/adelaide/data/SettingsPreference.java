package com.anadolstudio.adelaide.data;

import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
import static com.anadolstudio.adelaide.data.SettingsPreference.GridColumn.THREE;

import android.content.Context;
import android.preference.PreferenceManager;


public class SettingsPreference {
    public static final String TAG = SettingsPreference.class.getName();

    public static final String FIRST_RUN_APP = "first_run_app";
    public static final String FIRST_SHARE_ACTIVITY_OPEN = "first_share_activity_open";
    public static final String PREMIUM = "premium";
    public static final String NIGHT_MODE = "night_mode";
    public static final String GRID_COLUMN = "grid_column";
    public static final String HISTORY_PURCHASE = "history_purchase";
    public static final String DESTROY = "destroy";


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

    public static int getCurrentGridColumn(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getInt(GRID_COLUMN, THREE.column);
    }

    public static void setCurrentGridColumn(Context context, int gridColumn) {
        boolean isCorrect = false;
        for (GridColumn value : GridColumn.values()) {
            if (gridColumn == value.column) {
                isCorrect = true;
                break;
            }
        }
        if (!isCorrect) return;

        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putInt(GRID_COLUMN, gridColumn)
                .apply();
    }

    public enum GridColumn {
        THREE(3), FOUR(4);

        private final int column;

        GridColumn(int column) {
            this.column = column;
        }

        public int getColumn() {
            return column;
        }
    }
}
