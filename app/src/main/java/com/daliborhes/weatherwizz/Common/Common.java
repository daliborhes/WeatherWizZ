package com.daliborhes.weatherwizz.Common;

import android.location.Location;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Dalibor J. Stanković on 23.04.2019.
 */

public class Common {
    public static final String APP_ID = "caf8008607cf885468b3d4a523da6320";
    public static Location current_location = null;

    public static String convertUnixToDate(int dt) {
        Date date = new Date(dt*1000L);
        SimpleDateFormat sdf = new SimpleDateFormat("EEE dd/MM/yyyy");
        String formatted = sdf.format(date);
        return formatted;
    }

    public static String convertUnixToHour(int hour) {
        Date date = new Date(hour*1000L);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        String formatted = sdf.format(date);
        return formatted;
    }

    public static String convertUnixToDay(int dt) {
        Date date = new Date(dt*1000L);
        SimpleDateFormat sdf = new SimpleDateFormat("EEE HH:mm");
        String formatted = sdf.format(date);
        return formatted;
    }
}
