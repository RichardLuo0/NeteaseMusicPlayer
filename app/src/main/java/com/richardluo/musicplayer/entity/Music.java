package com.richardluo.musicplayer.entity;

import com.richardluo.musicplayer.model.Callback;
import com.richardluo.musicplayer.model.NeteaseService;
import com.richardluo.musicplayer.utils.RunnableWithArg;
import com.richardluo.musicplayer.utils.UiUtils;

import java.io.Serializable;
import java.util.List;

public class Music implements Serializable, UiUtils.Identifiable {
    int id;
    String name;
    String picUrl;
    int popularity;
    Song song;
    String playUrl;

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

    public void getPlayUrl(RunnableWithArg<String> callback) {
        if (playUrl != null)
            callback.run(playUrl);
        else
            NeteaseService.getInstance().getMusicPlayUrl(id).enqueue(new Callback<MusicPlayUrl[]>() {
                @Override
                public void onResponse(MusicPlayUrl[] musicPlayUrls) {
                    playUrl = musicPlayUrls[0].url;
                    callback.run(playUrl);
                }
            });
    }

    public long getDuration() {
        return song.duration;
    }

    public static class MusicPlayUrl {
        protected String url;
    }
}

class Song implements Serializable {
    List<Artist> artists;
    long duration;
}
