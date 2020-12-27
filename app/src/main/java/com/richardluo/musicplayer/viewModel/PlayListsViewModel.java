package com.richardluo.musicplayer.viewModel;

import androidx.activity.ComponentActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.richardluo.musicplayer.entity.Album;
import com.richardluo.musicplayer.entity.PlayList;
import com.richardluo.musicplayer.repository.MusicRepo;
import com.richardluo.musicplayer.repository.RepoProvider;

import java.util.List;

public class PlayListsViewModel extends ViewModel {

    LiveData<List<PlayList>> playLists;

    MusicRepo musicRepo;

    private static PlayListsViewModel instance;

    public static PlayListsViewModel getInstance(ComponentActivity activity) {
        if (instance == null)
            synchronized (PlayListsViewModel.class) {
                if (instance == null)
                    instance = new CustomViewModelProvider(activity).get(PlayListsViewModel.class);
            }
        return instance;
    }

    public PlayListsViewModel() {
        musicRepo = RepoProvider.get(MusicRepo.class);
        playLists = musicRepo.getPlayList();
    }

    public void createPlayList(String name) {
        RepoProvider.get(MusicRepo.class).createPlayList(new PlayList(name));
    }

    public void removePlayList(PlayList album) {
        musicRepo.removePlayList(album);
    }

    public LiveData<List<PlayList>> getPlayLists() {
        return playLists;
    }

    public void addSongToPlayList(PlayList playList, Album.AlbumSong song) {
        musicRepo.addSongToPlayList(playList, song);
    }

    public void removeSongFromPlayList(PlayList playList, Album.AlbumSong song) {
        musicRepo.removeSongFromPlayList(playList, song);
    }
}
