package com.richardluo.musicplayer.viewModel;

import androidx.activity.ComponentActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.richardluo.musicplayer.entity.Music;

public class HomeViewModel extends ViewModel {

    private final MutableLiveData<Music> playingMusic = new MutableLiveData<>();
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

    public void setPlayingMusic(Music music, boolean requestPlayNow) {
        if (playingMusic.getValue() != music) playingMusic.postValue(music);
        this.requestPlayNow = requestPlayNow;
    }

    public LiveData<Music> getPlayingMusic() {
        return playingMusic;
    }

    public boolean consumeRequestPlayNow() {
        boolean temp = requestPlayNow;
        requestPlayNow = false;
        return temp;
    }
}
