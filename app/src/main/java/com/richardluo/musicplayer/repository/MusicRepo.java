package com.richardluo.musicplayer.repository;

import androidx.lifecycle.MutableLiveData;

import com.richardluo.musicplayer.entity.Album;
import com.richardluo.musicplayer.entity.Music;

import java.util.List;

public class MusicRepo {
    public void getHotMusic(MutableLiveData<List<Music>> musicList) {
        NeteaseService.getInstance().getNewMusic().enqueue(new Callback<List<Music>>() {
            @Override
            public void onResponse(List<Music> musicList1) {
                musicList.postValue(musicList1);
            }
        });
    }

    public void getAlbum(MutableLiveData<List<Album>> albumList) {
        NeteaseService.getInstance().getNewAlbum().enqueue(new Callback<List<Album>>() {
            @Override
            public void onResponse(List<Album> albumList1) {
                albumList.postValue(albumList1);
            }
        });
    }
}
