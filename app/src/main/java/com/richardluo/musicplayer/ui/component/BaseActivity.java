package com.richardluo.musicplayer.ui.component;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.preference.PreferenceManager;

import com.richardluo.musicplayer.R;
import com.richardluo.musicplayer.ui.App;

import static android.view.View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
import static android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
import static android.view.View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
import static android.view.View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getWindow().getDecorView().setSystemUiVisibility(SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR | SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        int color = Integer.parseInt(sharedPreferences.getString("color", "0"));

        int theme = R.style.Theme_MusicPlayer_indigo;
        switch (color) {
            case 0:
                theme = R.style.Theme_MusicPlayer_indigo;
                break;
            case 1:
                theme = R.style.Theme_MusicPlayer_purple;
                break;
            case 2:
                theme = R.style.Theme_MusicPlayer_red;
                break;
            case 3:
                theme = R.style.Theme_MusicPlayer_teal;
                break;
        }
        setTheme(theme);

        ((App) getApplication()).observePreferenceChange(this, key -> {
            if ("color".equals(key)) recreate();
        });
    }
}
