package com.richardluo.musicplayer.ui.component;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.SeekBar;

import com.google.android.material.textview.MaterialTextView;
import com.richardluo.musicplayer.R;
import com.richardluo.musicplayer.entity.Playable;
import com.richardluo.musicplayer.service.MusicPlayerService;
import com.richardluo.musicplayer.utils.RunnableWithArg;
import com.richardluo.musicplayer.utils.UnixTimeFormat;

import java.text.DateFormat;
import java.util.ArrayList;

public class MusicPlayerView {
    private final Activity activity;
    private final MediaBrowserCompat mediaBrowser;
    private MediaControllerCompat mediaController;
    private final ArrayList<RunnableWithArg<MediaControllerCompat>> pendingControl = new ArrayList<>();
    private MediaBrowserCompat.ConnectionCallback connectionCallback;
    private MediaControllerCompat.Callback controllerCallback;

    private View miniPlayer;
    private NetworkImageView miniPlayerImageView;
    private MaterialTextView miniPlayerTextView;
    private ImageButton miniPlayerButton;
    private ProgressBar miniPlayerProgressBar;

    private View expandedPlayer;
    private NetworkImageView playerImageView;
    private MaterialTextView playerTextView;
    private MaterialTextView playerArtistTextView;
    private ImageButton playerButton;
    private SeekBar playerProgressSlider;
    private MaterialTextView playerNowTimeView;
    private MaterialTextView playerEndTimeView;

    private final View.OnClickListener playOrPauseListener = v -> {
        int state = mediaController.getPlaybackState().getState();
        if (state == PlaybackStateCompat.STATE_PLAYING)
            pause();
        else
            play();
    };

    public MusicPlayerView(Activity activity, View miniPlayer, View expandedPlayer) {
        this.activity = activity;
        initControllerCallback();
        initConnectionCallback();
        mediaBrowser = new MediaBrowserCompat(activity,
                new ComponentName(activity, MusicPlayerService.class),
                connectionCallback,
                null);
        mediaBrowser.connect();
        bindView(miniPlayer, expandedPlayer);
    }

    public void bindView(View miniPlayer, View expandedPlayer) {
        this.miniPlayer = miniPlayer;
        this.expandedPlayer = expandedPlayer;
        // Init UI
        initMiniPlayer();
        initExpandPlayer();
    }

    protected void initControllerCallback() {
        controllerCallback = new MediaControllerCompat.Callback() {
            @Override
            public void onMetadataChanged(MediaMetadataCompat metadata) {
                miniPlayerImageView.setImage(metadata.getDescription().getIconUri(), R.drawable.ic_round_music_note);
                miniPlayerTextView.setText(metadata.getDescription().getTitle());
                playerImageView.setImage(metadata.getDescription().getIconUri(), R.drawable.ic_round_music_note);
                playerTextView.setText(metadata.getDescription().getTitle());
                playerArtistTextView.setText(metadata.getDescription().getSubtitle());
                playerEndTimeView.setText(UnixTimeFormat.format(getDuration()));
            }

            @SuppressLint("SwitchIntDef")
            @Override
            public void onPlaybackStateChanged(PlaybackStateCompat state) {
                switch (state.getState()) {
                    case PlaybackStateCompat.STATE_PAUSED:
                        miniPlayerButton.setImageResource(R.drawable.ic_outline_play_arrow);
                        playerButton.setImageResource(R.drawable.ic_outline_play_arrow);
                        break;
                    case PlaybackStateCompat.STATE_PLAYING:
                        miniPlayerButton.setImageResource(R.drawable.ic_outline_pause);
                        float duration = getDuration();
                        int progress = (int) (state.getPosition() / duration * 100);
                        int buffedProgress = (int) (state.getBufferedPosition() / duration * 100);
                        miniPlayerProgressBar.setProgress(progress);
                        miniPlayerProgressBar.setSecondaryProgress(buffedProgress);
                        playerButton.setImageResource(R.drawable.ic_outline_pause);
                        playerProgressSlider.setProgress(progress);
                        playerProgressSlider.setSecondaryProgress(buffedProgress);
                        playerNowTimeView.setText(UnixTimeFormat.format(state.getPosition()));
                        break;
                }
            }
        };
    }

    protected void initConnectionCallback() {
        connectionCallback = new MediaBrowserCompat.ConnectionCallback() {
            public void onConnected() {
                MediaSessionCompat.Token token = mediaBrowser.getSessionToken();
                // Create a MediaControllerCompat
                mediaController = new MediaControllerCompat(activity, token);
                // Save the controller
                MediaControllerCompat.setMediaController(activity, mediaController);
                // Register callback to stay in sync
                mediaController.registerCallback(controllerCallback);

                controllerCallback.onPlaybackStateChanged(mediaController.getPlaybackState());
                controllerCallback.onMetadataChanged(mediaController.getMetadata());

                for (RunnableWithArg<MediaControllerCompat> runnable : pendingControl) {
                    runnable.run(mediaController);
                }
            }
        };
    }

    protected void initMiniPlayer() {
        miniPlayerImageView = miniPlayer.findViewById(R.id.mini_player_image);
        miniPlayerTextView = miniPlayer.findViewById(R.id.mini_player_name);
        miniPlayerButton = miniPlayer.findViewById(R.id.mini_player_play_button);
        miniPlayerProgressBar = miniPlayer.findViewById(R.id.mini_player_progress);
        miniPlayerButton.setOnClickListener(playOrPauseListener);
    }

    protected void initExpandPlayer() {
        playerImageView = expandedPlayer.findViewById(R.id.player_image);
        playerTextView = expandedPlayer.findViewById(R.id.player_name);
        playerArtistTextView = expandedPlayer.findViewById(R.id.player_artist);
        playerButton = expandedPlayer.findViewById(R.id.player_play_button);
        ImageButton playerSkipToPreButton = expandedPlayer.findViewById(R.id.player_skip_previous);
        ImageButton playerSkipToNextButton = expandedPlayer.findViewById(R.id.player_skip_next);
        playerProgressSlider = expandedPlayer.findViewById(R.id.player_progress);
        playerNowTimeView = expandedPlayer.findViewById(R.id.player_now_time);
        playerEndTimeView = expandedPlayer.findViewById(R.id.player_end_time);
        playerNowTimeView.setText(DateFormat.getTimeInstance().format(0L));
        playerButton.setOnClickListener(playOrPauseListener);
        playerSkipToPreButton.setOnClickListener(v -> mediaController.getTransportControls().skipToPrevious());
        playerSkipToNextButton.setOnClickListener(v -> mediaController.getTransportControls().skipToNext());
        playerProgressSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    long position = (long) (progress / 100f * getDuration());
                    playerNowTimeView.setText(UnixTimeFormat.format(position));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                long position = (long) (seekBar.getProgress() / 100f * getDuration());
                mediaController.getTransportControls().seekTo(position);
            }
        });
    }

    public synchronized void setMusic(Playable playable) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("playable", playable);
        playable.getPlayUrl(url -> {
            RunnableWithArg<MediaControllerCompat> runnable = mediaController -> mediaController.getTransportControls().prepareFromUri(Uri.parse(url), bundle);
            if (mediaController == null)
                pendingControl.add(runnable);
            else runnable.run(mediaController);
        });
    }

    public void play() {
        RunnableWithArg<MediaControllerCompat> runnable = mediaController -> mediaController.getTransportControls().play();
        if (mediaController == null)
            pendingControl.add(runnable);
        else runnable.run(mediaController);
    }

    public void pause() {
        RunnableWithArg<MediaControllerCompat> runnable = mediaController -> mediaController.getTransportControls().pause();
        if (mediaController == null)
            pendingControl.add(runnable);
        else runnable.run(mediaController);
    }

    protected long getDuration() {
        return mediaController.getMetadata().getLong(MediaMetadataCompat.METADATA_KEY_DURATION);
    }

    public void onSliding(float slideOffset) {
        miniPlayer.setAlpha(1 - slideOffset);
        expandedPlayer.setAlpha(slideOffset);
    }

    public void start() {
        if (mediaController != null) {
            mediaController.registerCallback(controllerCallback);
            controllerCallback.onPlaybackStateChanged(mediaController.getPlaybackState());
            controllerCallback.onMetadataChanged(mediaController.getMetadata());
        }
    }

    public void resume(Activity activity) {
        activity.setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }

    public void stop() {
        if (mediaController != null)
            mediaController.unregisterCallback(controllerCallback);
    }

    public void destroy() {
        mediaBrowser.disconnect();
    }
}
