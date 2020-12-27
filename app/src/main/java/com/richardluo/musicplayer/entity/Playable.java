package com.richardluo.musicplayer.entity;

import com.richardluo.musicplayer.repository.Callback;
import com.richardluo.musicplayer.repository.NeteaseService;
import com.richardluo.musicplayer.utils.RunnableWithArg;
import com.richardluo.musicplayer.utils.UiUtils;

import java.io.Serializable;

public abstract class Playable implements Serializable, UiUtils.Identifiable {
    public int id;
    public String playUrl;

    public void getPlayUrl(RunnableWithArg<String> callback) {
        if (playUrl != null)
            callback.run(playUrl);
        else
            NeteaseService.getInstance().getMusicPlayUrl(id).enqueue(new Callback<MusicPlayUrl[]>() {
                @Override
                public void onResponse(Playable.MusicPlayUrl[] musicPlayUrls) {
                    playUrl = musicPlayUrls[0].url;
                    callback.run(playUrl);
                }
            });
    }

    public abstract long getDuration();

    public abstract String getName();

    public abstract String getPicUrl();

    public abstract Artist getArtist();

    public static class MusicPlayUrl {
        protected String url;
    }
}


