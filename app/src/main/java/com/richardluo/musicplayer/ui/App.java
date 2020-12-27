package com.richardluo.musicplayer.ui;

import android.app.Application;
import android.content.SharedPreferences;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.preference.PreferenceManager;

import com.richardluo.musicplayer.repository.AppDatabase;
import com.richardluo.musicplayer.repository.MusicRepo;
import com.richardluo.musicplayer.repository.RepoProvider;
import com.richardluo.musicplayer.utils.CustomLiveData;
import com.richardluo.musicplayer.utils.Logger;

public class App extends Application {

    private final CustomLiveData<String> lastModifiedPreference = new CustomLiveData<>();

    // If preference change, this will be called
    SharedPreferences.OnSharedPreferenceChangeListener onSharedPreferenceChangeListener = (sharedPreferences1, key) -> lastModifiedPreference.postValue(key);

    @Override
    public void onCreate() {
        super.onCreate();
        Logger.addOnException(e -> Toast.makeText(this, e, Toast.LENGTH_SHORT).show());

        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener);

        // init repository
        RepoProvider.get(MusicRepo.class).setAppDatabase(AppDatabase.get(this));
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener);
    }

    public void observePreferenceChange(@NonNull LifecycleOwner owner, @NonNull Observer<String> observer) {
        lastModifiedPreference.observe(owner, observer);
    }
}
