package com.richardluo.musicplayer.model;

import androidx.lifecycle.MutableLiveData;

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
}
