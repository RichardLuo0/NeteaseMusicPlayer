package com.richardluo.musicplayer.ui;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.richardluo.musicplayer.R;

public class PreferenceActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings_container, new PreferenceFragment())
                .commit();
    }
}
