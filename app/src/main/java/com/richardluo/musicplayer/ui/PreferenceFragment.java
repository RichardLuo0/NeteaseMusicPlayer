package com.richardluo.musicplayer.ui;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import com.richardluo.musicplayer.R;

public class PreferenceFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preference_main, rootKey);
    }
}
