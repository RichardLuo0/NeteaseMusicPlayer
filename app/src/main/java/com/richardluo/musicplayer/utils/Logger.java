package com.richardluo.musicplayer.utils;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

public class Logger {
    private static final String TAG = "Music Player";

    private static final MutableLiveData<String> error = new MutableLiveData<>();

    public static void addOnException(@NonNull Observer<? super String> observer) {
        error.observeForever(observer);
    }

    public static void addOnException(@NonNull LifecycleOwner owner, @NonNull Observer<? super String> observer) {
        error.observe(owner, observer);
    }

    public static void error(String s, Throwable t) {
        Log.e(TAG, "error: " + s, t);
        error.postValue(s);
    }

    public static void warn(String s) {
        Log.w(TAG, "warn: " + s);
        error.postValue(s);
    }

    public static void warn(String s, Throwable t) {
        Log.w(TAG, "warn: " + s, t);
        error.postValue(s);
    }

    public static void info(String s) {
        Log.i(TAG, "info: " + s);
        error.postValue(s);
    }
}
