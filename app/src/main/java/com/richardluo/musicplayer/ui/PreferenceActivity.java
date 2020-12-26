package com.richardluo.musicplayer.ui;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.richardluo.musicplayer.R;
import com.richardluo.musicplayer.ui.component.BaseActivity;

import java.util.Objects;

public class PreferenceActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setSupportActionBar(findViewById(R.id.appBar));
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings_container, new PreferenceFragment())
                .commit();
    }
}
