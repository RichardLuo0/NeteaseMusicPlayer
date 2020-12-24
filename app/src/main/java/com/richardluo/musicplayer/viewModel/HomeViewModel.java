package com.richardluo.musicplayer.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.richardluo.musicplayer.entity.Music;
import com.richardluo.musicplayer.model.MusicRepo;
import com.richardluo.musicplayer.model.RepoProvider;

import java.util.List;

public class HomeViewModel extends ViewModel {

    private final MutableLiveData<Music> playingMusic = new MutableLiveData<>();

    public void setPlayingMusic(Music music) {
        playingMusic.postValue(music);
    }

    public LiveData<Music> getPlayingMusic() {
        return playingMusic;
    }
}
