package com.richardluo.musicplayer.entity;

import com.richardluo.musicplayer.repository.Callback;
import com.richardluo.musicplayer.repository.NeteaseService;
import com.richardluo.musicplayer.utils.RunnableWithArg;

import java.io.Serializable;

public abstract class Playable implements Serializable {
    int id;
    String playUrl;

    public void getPlayUrl(RunnableWithArg<String> callback) {
        if (playUrl != null)
            callback.run(playUrl);
        else
            NeteaseService.getInstance().getMusicPlayUrl(id).enqueue(new Callback<Music.MusicPlayUrl[]>() {
                @Override
                public void onResponse(Music.MusicPlayUrl[] musicPlayUrls) {
                    playUrl = musicPlayUrls[0].url;
                    callback.run(playUrl);
                }
            });
    }

    public abstract long getDuration();
}
