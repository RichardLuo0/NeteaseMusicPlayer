package com.richardluo.musicplayer.ui;

import android.app.Application;
import android.widget.Toast;

import com.richardluo.musicplayer.utils.Logger;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Logger.addOnException(e -> Toast.makeText(this, e, Toast.LENGTH_SHORT).show());
    }
}
