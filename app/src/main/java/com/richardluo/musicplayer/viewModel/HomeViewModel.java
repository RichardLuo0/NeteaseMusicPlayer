package com.richardluo.musicplayer.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.richardluo.musicplayer.entity.Music;

public class HomeViewModel extends ViewModel {

    private final MutableLiveData<Music> playingMusic = new MutableLiveData<>();
    private boolean requestPlayNow = false;

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
