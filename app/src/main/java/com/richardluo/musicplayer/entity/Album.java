package com.richardluo.musicplayer.entity;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;
import com.richardluo.musicplayer.repository.Callback;
import com.richardluo.musicplayer.repository.NeteaseService;
import com.richardluo.musicplayer.utils.UiUtils;

import java.io.Serializable;
import java.util.List;

import static androidx.room.ForeignKey.CASCADE;

public class Album implements UiUtils.Identifiable, Serializable {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String name;
    public String blurPicUrl;
    @Embedded(prefix = "artist_")
    public Artist artist;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean hasPic() {
        return blurPicUrl != null;
    }

    public String getPicUrl() {
        return blurPicUrl.replace("http", "https") + "?param=200y200";
    }

    public Artist getArtist() {
        return artist;
    }

    @Ignore
    protected LiveData<List<AlbumSong>> songs;

    public LiveData<List<AlbumSong>> getSongs() {
        if (songs == null || songs.getValue() == null) {
            songs = new MutableLiveData<>();
            NeteaseService.getInstance().getAlbumSongs(id).enqueue(new Callback<List<AlbumSong>>() {
                @Override
                public void onResponse(List<AlbumSong> songs1) {
                    ((MutableLiveData<List<AlbumSong>>) songs).postValue(songs1);
                }
            });
        }
        return songs;
    }

    @Entity(indices = {@Index("albumId")},
            foreignKeys = @ForeignKey(entity = PlayList.class, parentColumns = "id", childColumns = "albumId", onDelete = CASCADE))
    public static class AlbumSong extends Playable implements UiUtils.Identifiable {
        @PrimaryKey(autoGenerate = true)
        public int uniqueId;
        public int no;
        public String name;
        @Embedded(prefix = "artist_")
        public Artist artist;
        public int fee;
        public int albumId;
        public String picUrl;

        @SerializedName(value = "duration", alternate = {"dt"})
        public long duration;

        public int getNo() {
            return no;
        }

        public String getName() {
            return name;
        }

        @Override
        public String getPicUrl() {
            return picUrl.replace("http", "https") + "?param=200y200";
        }

        @Override
        public long getDuration() {
            return duration;
        }

        @Override
        public int getId() {
            return id;
        }

        @Override
        public Artist getArtist() {
            return artist;
        }

        public int getFee() {
            return fee;
        }

        public Playable toMusic(Album album) {
            this.albumId = album.id;
            if (this.picUrl == null) {
                this.picUrl = album.blurPicUrl;
                this.artist = album.artist;
            }
            return this;
        }
    }
}
