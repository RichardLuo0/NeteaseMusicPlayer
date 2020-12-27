package com.richardluo.musicplayer.entity;


import androidx.lifecycle.LiveData;
import androidx.room.Entity;

import com.richardluo.musicplayer.repository.MusicRepo;
import com.richardluo.musicplayer.repository.RepoProvider;

import java.util.List;

@Entity
public class PlayList extends Album {

    public int songsSize;

    public PlayList(String name) {
        this.name = name;
        songsSize = 0;
        this.artist = new Artist("0");
    }

    public int getSongsSize() {
        return songsSize;
    }

    @Override
    public LiveData<List<AlbumSong>> getSongs() {
        if (songs == null)
            songs = RepoProvider.get(MusicRepo.class).getPlayListSongs(this);
        return songs;
    }

    @Override
    public Artist getArtist() {
        artist.name = String.valueOf(songsSize);
        return super.getArtist();
    }

    public void addSong(AlbumSong song) {
        blurPicUrl = song.picUrl;
        songsSize++;
    }

    public void removeSong() {
        songsSize--;
    }
}
