package com.richardluo.musicplayer.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.richardluo.musicplayer.entity.Album;
import com.richardluo.musicplayer.entity.Music;
import com.richardluo.musicplayer.entity.PlayList;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MusicRepo {

    private final NeteaseService.INeteaseService neteaseService = NeteaseService.getInstance();
    private AppDatabase appDatabase;

    private final Executor executor = Executors.newFixedThreadPool(10);

    public void setAppDatabase(AppDatabase appDatabase) {
        this.appDatabase = appDatabase;
    }

    public void getHotMusic(MutableLiveData<List<Music>> musicList) {
        neteaseService.getNewMusic().enqueue(new Callback<List<Music>>() {
            @Override
            public void onResponse(List<Music> playableList1) {
                musicList.postValue(playableList1);
            }
        });
    }

    public void getAlbum(MutableLiveData<List<Album>> albumList) {
        neteaseService.getNewAlbum().enqueue(new Callback<List<Album>>() {
            @Override
            public void onResponse(List<Album> albumList1) {
                albumList.postValue(albumList1);
            }
        });
    }

    public void createPlayList(PlayList album) {
        executor.execute(() -> appDatabase.playListDao().createPlayList(album));
    }

    public LiveData<List<PlayList>> getPlayList() {
        return appDatabase.playListDao().getPlayLists();
    }

    public void removePlayList(PlayList album) {
        executor.execute(() -> appDatabase.playListDao().deletePlayList(album));
    }

    public LiveData<List<Album.AlbumSong>> getPlayListSongs(PlayList playList) {
        return appDatabase.playListDao().getPlayListSongs(playList.id);
    }

    public void addSongToPlayList(PlayList playList, Album.AlbumSong song) {
        executor.execute(() -> {
            song.toMusic(playList);
            song.no = playList.getSongsSize() + 1;
            if (appDatabase.playListDao().addSongToPlayList(song) != -1) {
                playList.addSong(song);
                appDatabase.playListDao().savePlayList(playList);
            }
        });
    }

    public void removeSongFromPlayList(PlayList playList, Album.AlbumSong song) {
        executor.execute(() -> {
            appDatabase.playListDao().removeSong(song);
            playList.removeSong();
            appDatabase.playListDao().savePlayList(playList);
        });
    }
}
