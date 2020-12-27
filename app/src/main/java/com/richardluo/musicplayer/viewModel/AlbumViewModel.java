package com.richardluo.musicplayer.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.richardluo.musicplayer.entity.Album;
import com.richardluo.musicplayer.repository.MusicRepo;
import com.richardluo.musicplayer.repository.RepoProvider;

import java.util.List;

public class AlbumViewModel extends ViewModel {

    private final MutableLiveData<List<Album>> albumList = new MutableLiveData<>();

    public void refreshAlbumList() {
        RepoProvider.get(MusicRepo.class).getAlbum(albumList);
    }

    public LiveData<List<Album>> getAlbumList() {
        return albumList;
    }
}
