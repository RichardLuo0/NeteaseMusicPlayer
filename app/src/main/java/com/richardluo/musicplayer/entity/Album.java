package com.richardluo.musicplayer.entity;

import com.google.gson.annotations.SerializedName;
import com.richardluo.musicplayer.repository.Callback;
import com.richardluo.musicplayer.repository.NeteaseService;
import com.richardluo.musicplayer.utils.RunnableWithArg;
import com.richardluo.musicplayer.utils.UiUtils;

import java.io.Serializable;
import java.util.List;

public class Album implements UiUtils.Identifiable, Serializable {
    int id;
    String name;
    String blurPicUrl;
    Artist artist;
    List<AlbumSong> songs;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPicUrl() {
        return blurPicUrl.replace("http", "https") + "?param=200y200";
    }

    public Artist getArtist() {
        return artist;
    }

    public void getSongs(RunnableWithArg<List<AlbumSong>> callback) {
        if (songs != null && !songs.isEmpty())
            callback.run(songs);
        else
            NeteaseService.getInstance().getAlbumSongs(id).enqueue(new Callback<List<AlbumSong>>() {
                @Override
                public void onResponse(List<AlbumSong> songs1) {
                    songs = songs1;
                    callback.run(songs);
                }
            });
    }

    public static class AlbumSong extends Music implements UiUtils.Identifiable {
        int no;
        Artist artist;
        int fee;

        @SerializedName(value = "duration", alternate = {"dt"})
        long duration;

        public int getNo() {
            return no;
        }

        public String getName() {
            return name;
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

        public Music toMusic(Album album) {
            this.picUrl = album.blurPicUrl;
            this.artist = album.artist;
            return this;
        }
    }
}
