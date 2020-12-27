package com.richardluo.musicplayer.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Database;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.Update;

import com.richardluo.musicplayer.entity.Album;
import com.richardluo.musicplayer.entity.PlayList;

import java.util.List;

@Database(entities = {PlayList.class, Album.AlbumSong.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase instance;

    public static AppDatabase get(Context context) {
        if (instance == null)
            synchronized (AppDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "app").build();
                }
            }
        return instance;
    }

    public abstract PlayListDao playListDao();

    @Dao
    interface PlayListDao {
        @Insert(onConflict = OnConflictStrategy.IGNORE)
        void createPlayList(PlayList album);

        @Update
        void savePlayList(PlayList playList);

        @Delete()
        void deletePlayList(PlayList album);

        @Query("SELECT * FROM PlayList")
        LiveData<List<PlayList>> getPlayLists();

        @Query("SELECT * FROM AlbumSong WHERE albumId = :id")
        LiveData<List<Album.AlbumSong>> getPlayListSongs(int id);

        @Insert(onConflict = OnConflictStrategy.IGNORE)
        long addSongToPlayList(Album.AlbumSong song);

        @Delete
        void removeSong(Album.AlbumSong song);
    }
}
