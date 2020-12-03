package com.richardluo.musicplayer.utils;

import android.annotation.SuppressLint;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

@SuppressLint("SimpleDateFormat")
public class UnixTimeFormat {

    private static final DateFormat moreThanOneHourFormatter = new SimpleDateFormat("HH:mm:ss");
    private static final DateFormat lessInOneHourFormatter = new SimpleDateFormat("mm:ss");

    static {
        moreThanOneHourFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        lessInOneHourFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    public static String format(long timestamp) {
        if (timestamp > 60 * 60 * 1000) {
            return moreThanOneHourFormatter.format(timestamp);
        } else return lessInOneHourFormatter.format(timestamp);
    }
}
