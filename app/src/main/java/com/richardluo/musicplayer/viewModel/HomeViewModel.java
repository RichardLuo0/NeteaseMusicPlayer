package com.richardluo.musicplayer.viewModel;

import androidx.activity.ComponentActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.richardluo.musicplayer.entity.Playable;
import com.richardluo.musicplayer.utils.CustomLiveData;

public class HomeViewModel extends ViewModel {

    private final CustomLiveData<Playable> playingMusic = new CustomLiveData<>();
    private boolean requestPlayNow = false;

    private static HomeViewModel instance;

    public static HomeViewModel getInstance(ComponentActivity activity) {
        if (instance == null)
            synchronized (HomeViewModel.class) {
                if (instance == null)
                    instance = new CustomViewModelProvider(activity).get(HomeViewModel.class);
            }
        return instance;
    }

    public static HomeViewModel getInstance(Fragment fragment) {
        if (instance == null)
            synchronized (HomeViewModel.class) {
                if (instance == null)
                    instance = new CustomViewModelProvider(fragment).get(HomeViewModel.class);
            }
        return instance;
    }

    public void setPlayingMusic(Playable playable, boolean requestPlayNow) {
        if (playingMusic.getValue() != playable) playingMusic.postValue(playable);
        this.requestPlayNow = requestPlayNow;
    }

    public LiveData<Playable> getPlayingMusic() {
        return playingMusic;
    }

    public boolean consumeRequestPlayNow() {
        boolean temp = requestPlayNow;
        requestPlayNow = false;
        return temp;
    }
}
