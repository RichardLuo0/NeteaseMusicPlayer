package com.richardluo.musicplayer.service;

import com.google.android.exoplayer2.MediaItem;
import com.richardluo.musicplayer.entity.Playable;

public class PlayableAndMediaItem {
    Playable playable;
    MediaItem mediaItem;

    PlayableAndMediaItem(Playable playable, MediaItem mediaItem) {
        this.playable = playable;
        this.mediaItem = mediaItem;
    }
}
