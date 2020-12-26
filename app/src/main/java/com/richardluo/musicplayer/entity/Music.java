package com.richardluo.musicplayer.entity;

import com.google.gson.annotations.SerializedName;
import com.richardluo.musicplayer.utils.UiUtils;

import java.io.Serializable;
import java.util.List;

public class Music extends Playable implements Serializable, UiUtils.Identifiable {
    String name;
    String picUrl;
    int popularity;
    Song song;

    @Override
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPicUrl() {
        return picUrl.replace("http", "https");
    }

    public Artist getArtist() {
        if (song.artists.size() > 0)
            return song.artists.get(0);
        return null;
    }

    @Override
    public long getDuration() {
        return song.duration;
    }

    public static class MusicPlayUrl {
        protected String url;
    }

    public static class Song implements Serializable {
        List<Artist> artists;

        @SerializedName(value = "duration", alternate = {"dt"})
        long duration;
    }
}


