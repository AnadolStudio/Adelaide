package com.anadolstudio.adelaide.helpers;

import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TimeHelper {
    public static final String TAG = TimeHelper.class.getName();
    public static final String STANDART_FORMAT = "yyyy_MM_dd_HHmmss";

    public static int getCurrentHour() {
        Date currentDate = new Date();
        // Форматирование времени как "часы"
        DateFormat timeFormat = new SimpleDateFormat("HH", Locale.getDefault());
        String timeText = timeFormat.format(currentDate);
        int hour;
        try {
            hour = Integer.parseInt(timeText);
        } catch (NumberFormatException exception) {
            hour = 0;
        }
        Log.d(TAG, "getCurrentHour: " + hour);
        return hour;
    }

    public static long getCurrentMills() {
        Date currentDate = new Date();
        return currentDate.getTime();
    }

    public static String getTime(String format) {
        Date currentDate = new Date();
        DateFormat timeFormat = new SimpleDateFormat(format, Locale.getDefault());
        return timeFormat.format(currentDate);
    }

}
