package com.richardluo.musicplayer.entity;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Music extends Playable {
    public String name;
    public String picUrl;
    public int popularity;
    Song song;

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getPicUrl() {
        return picUrl.replace("http", "https") + "?param=200y200";
    }

    @Override
    public Artist getArtist() {
        if (song.artists.size() > 0)
            return song.artists.get(0);
        return null;
    }

    @Override
    public long getDuration() {
        return song.duration;
    }

    public Album.AlbumSong toAlbumSong() {
        Album.AlbumSong song = new Album.AlbumSong();
        song.id = id;
        song.playUrl = playUrl;
        song.name = name;
        song.picUrl = picUrl;
        song.artist = this.song.artists.get(0);
        song.fee = 0;
        song.duration = getDuration();
        return song;
    }

    public static class Song implements Serializable {
        List<Artist> artists;

        @SerializedName(value = "duration", alternate = {"dt"})
        long duration;
    }
}
