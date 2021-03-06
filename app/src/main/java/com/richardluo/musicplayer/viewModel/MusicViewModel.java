package com.richardluo.musicplayer.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.richardluo.musicplayer.entity.Music;
import com.richardluo.musicplayer.entity.Playable;
import com.richardluo.musicplayer.repository.MusicRepo;
import com.richardluo.musicplayer.repository.RepoProvider;

import java.util.List;

public class MusicViewModel extends ViewModel {

    private final MutableLiveData<List<Music>> musicList = new MutableLiveData<>();

    public void refreshMusicList() {
        RepoProvider.get(MusicRepo.class).getHotMusic(musicList);
    }

    public LiveData<List<Music>> getMusicList() {
        return musicList;
    }
}
