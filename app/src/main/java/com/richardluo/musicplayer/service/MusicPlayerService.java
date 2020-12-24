package com.richardluo.musicplayer.service;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.view.KeyEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.media.AudioAttributesCompat;
import androidx.media.AudioFocusRequestCompat;
import androidx.media.AudioManagerCompat;
import androidx.media.MediaBrowserServiceCompat;
import androidx.media.session.MediaButtonReceiver;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.richardluo.musicplayer.entity.Music;
import com.richardluo.musicplayer.utils.MediaNotification;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class MusicPlayerService extends MediaBrowserServiceCompat {

    private final static String LOG_TAG = "MusicPlayerService";
    private final static String ROOT_ID = "root";

    private final MusicPlayerService service = this;
    private MediaSessionCompat mediaSession;
    private MediaMetadataCompat.Builder metadataBuilder;
    private PlaybackStateCompat.Builder playbackStateBuilder;

    private ExoPlayer exoPlayer;
    private MediaNotification mediaNotification;
    private final Queue<Music> playQueue = new LinkedList<>();

    MediaSessionCompat.Callback mediaSessionCallback = new MediaSessionCompat.Callback() {
        AudioFocusRequestCompat audioFocusRequest;

        @Override
        public boolean onMediaButtonEvent(Intent mediaButtonEvent) {
            KeyEvent keyEvent = mediaButtonEvent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
            int keyCode = keyEvent.getKeyCode();
            switch (keyCode) {
                case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                    if (exoPlayer.isPlaying())
                        onPause();
                    else onPlay();
                    break;
                case KeyEvent.KEYCODE_MEDIA_SKIP_FORWARD:
                    onSkipToNext();
                    break;
                case KeyEvent.KEYCODE_MEDIA_SKIP_BACKWARD:
                    onSkipToPrevious();
                    break;
            }
            return super.onMediaButtonEvent(mediaButtonEvent);
        }

        @Override
        public void onPrepareFromUri(Uri uri, Bundle extras) {
            super.onPrepareFromUri(uri, extras);
            Music music = (Music) extras.getSerializable("music");
            setMusic(music, uri);
        }

        @Override
        public void onPlay() {
            AudioManager am = (AudioManager) service.getSystemService(Context.AUDIO_SERVICE);
            // Request audio focus for playback, this registers the afChangeListener
            AudioAttributesCompat attrs = new AudioAttributesCompat.Builder()
                    .setContentType(AudioAttributesCompat.CONTENT_TYPE_MUSIC)
                    .build();
            AudioFocusRequestCompat audioFocusRequest = new AudioFocusRequestCompat.Builder(AudioManagerCompat.AUDIOFOCUS_GAIN)
                    .setOnAudioFocusChangeListener(focusChange -> {
                    })
                    .setAudioAttributes(attrs)
                    .build();
            int result = AudioManagerCompat.requestAudioFocus(am, audioFocusRequest);
            if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                service.play();
            }
        }

        @Override
        public void onStop() {
            AudioManager am = (AudioManager) service.getSystemService(Context.AUDIO_SERVICE);
            // Abandon audio focus
            AudioManagerCompat.abandonAudioFocusRequest(am, audioFocusRequest);
            // Stop the service
            service.stopSelf();
            // Set the session inactive  (and update metadata and state)
            mediaSession.setActive(false);
        }

        @Override
        public void onPause() {
            // pause the player
            service.pause();
        }

        @Override
        public void onSeekTo(long pos) {
            exoPlayer.seekTo(pos);
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        exoPlayer = new SimpleExoPlayer.Builder(this).build();
        // Create a MediaSessionCompat
        mediaSession = new MediaSessionCompat(this, LOG_TAG);
        // Set an initial PlaybackState with ACTION_PLAY, so media buttons can start the player
        playbackStateBuilder = new PlaybackStateCompat.Builder()
                .setActions(PlaybackStateCompat.ACTION_PLAY | PlaybackStateCompat.ACTION_PLAY_PAUSE);
        metadataBuilder = new MediaMetadataCompat.Builder();
        mediaSession.setPlaybackState(playbackStateBuilder.build());
        mediaSession.setMetadata(metadataBuilder.build());
        // MySessionCallback() has methods that handle callbacks from a media controller
        mediaSession.setCallback(mediaSessionCallback);
        // Set the session's token so that client activities can communicate with it.
        setSessionToken(mediaSession.getSessionToken());
        mediaNotification = new MediaNotification(this, mediaSession);
        exoPlayer.addListener(new Player.EventListener() {

            @Override
            public void onIsPlayingChanged(boolean isPlaying) {
                if (isPlaying) service.startMonitorProgress();
            }
        });
    }

    @Nullable
    @Override
    public BrowserRoot onGetRoot(@NonNull String clientPackageName, int clientUid, @Nullable Bundle rootHints) {
        return new BrowserRoot(ROOT_ID, null);
    }

    @Override
    public void onLoadChildren(@NonNull String parentId, @NonNull Result<List<MediaBrowserCompat.MediaItem>> result) {
        result.sendResult(null);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        MediaButtonReceiver.handleIntent(mediaSession, intent);
        return super.onStartCommand(intent, flags, startId);
    }

    protected void addMusic(Music music, Uri uri) {
        playQueue.add(music);
        exoPlayer.addMediaItem(MediaItem.fromUri(uri));
        setMetadata(music);
    }

    protected void setMusic(Music music, Uri uri) {
        playQueue.clear();
        playQueue.add(music);
        exoPlayer.setMediaItem(MediaItem.fromUri(uri));
        setMetadata(music);
    }

    protected void setMetadata(Music music) {
        mediaSession.setMetadata(metadataBuilder
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, music.getName())
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, music.getArtist().getName())
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI, String.valueOf(Uri.parse(music.getPicUrl())))
                .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, music.getDuration())
                .build());
    }

    protected void setPlaybackState(@PlaybackStateCompat.State int state) {
        mediaSession.setPlaybackState(playbackStateBuilder
                .setState(state, exoPlayer.getCurrentPosition(), exoPlayer.getPlaybackParameters().speed)
                .setBufferedPosition(exoPlayer.getBufferedPosition())
                .setActions(PlaybackStateCompat.ACTION_SEEK_TO)
                .build());
    }

    public void play() {
        mediaNotification.startForeground(this);
        setPlaybackState(PlaybackStateCompat.STATE_PLAYING);
        exoPlayer.prepare();
        exoPlayer.play();
    }

    public void pause() {
        stopForeground(false);
        setPlaybackState(PlaybackStateCompat.STATE_PAUSED);
        exoPlayer.pause();
    }

    public void stop() {
        stopForeground(false);
        exoPlayer.stop();
    }

    private boolean isExisting = false;
    private final Handler handler = new Handler(Looper.getMainLooper());

    protected void updateProgressBar() {
        //Remove scheduled updates.
        handler.removeCallbacks(this::updateProgressBar);
        if (exoPlayer == null)
            return;
        if (exoPlayer.isPlaying()) {
            setPlaybackState(PlaybackStateCompat.STATE_PLAYING);
            //Schedule an update
            int playbackState = exoPlayer == null ? Player.STATE_IDLE : exoPlayer.getPlaybackState();
            long delayMs;
            assert exoPlayer != null;
            if (exoPlayer.getPlayWhenReady() && playbackState == Player.STATE_READY) {
                delayMs = 1000 - (exoPlayer.getCurrentPosition() % 1000);
                if (delayMs < 200) {
                    delayMs += 1000;
                }
            } else {
                delayMs = 1000;
            }
            handler.postDelayed(this::updateProgressBar, delayMs);
        } else {
            setPlaybackState(PlaybackStateCompat.STATE_PAUSED);
            isExisting = false;
        }
    }

    public void startMonitorProgress() {
        if (!isExisting) {
            handler.post(this::updateProgressBar);
            isExisting = true;
        }
    }

    @Override
    public void onDestroy() {
        stop();
        exoPlayer.release();
        exoPlayer = null;
    }
}
