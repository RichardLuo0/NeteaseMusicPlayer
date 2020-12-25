package com.richardluo.musicplayer.utils;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import androidx.media.app.NotificationCompat;
import androidx.media.session.MediaButtonReceiver;

import com.richardluo.musicplayer.R;
import com.richardluo.musicplayer.ui.MainActivity;
import com.squareup.picasso.Picasso;

import java.io.IOException;

public class MediaNotification {
    private final static String CHANNEL_ID = "MEDIA_PLAY";
    private final static int NOTIFICATION_ID = 1;

    private final MediaControllerCompat controller;
    private final NotificationCompat.MediaStyle mediaStyle;
    private Notification notification;
    private final NotificationManager notificationManager;

    private boolean isPlaying = false;

    public MediaNotification(Context context, MediaSessionCompat mediaSession) {
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mediaStyle = new androidx.media.app.NotificationCompat.MediaStyle().setMediaSession(mediaSession.getSessionToken())
                .setShowActionsInCompactView(0, 1, 2)
                .setShowCancelButton(true)
                .setCancelButtonIntent(MediaButtonReceiver.buildMediaButtonPendingIntent(context, PlaybackStateCompat.ACTION_STOP));
        controller = mediaSession.getController();
        createNotificationChannel(context);
        createNotification(context, null, false);
        controller.registerCallback(new MediaControllerCompat.Callback() {
            @SuppressLint("SwitchIntDef")
            @Override
            public void onPlaybackStateChanged(PlaybackStateCompat state) {
                super.onPlaybackStateChanged(state);
                boolean realPlayingState = state.getState() == PlaybackStateCompat.STATE_PLAYING;
                if (isPlaying != realPlayingState) {
                    updateNotification(context, realPlayingState);
                    isPlaying = realPlayingState;
                }
            }

            @Override
            public void onMetadataChanged(MediaMetadataCompat metadata) {
                super.onMetadataChanged(metadata);
                updateNotification(context, isPlaying);
            }
        });
    }

    private Uri iconUri;
    private Bitmap iconBitmap;

    private void updateNotification(Context context, boolean isPlaying) {
        MediaDescriptionCompat description = controller.getMetadata().getDescription();
        Uri descriptionUri = description.getIconUri();
        if (descriptionUri != null && !descriptionUri.equals(iconUri)) {
            new Thread(() -> {
                try {
                    iconBitmap = Picasso.get().load(descriptionUri).get();
                    iconUri = descriptionUri;
                    createNotification(context, iconBitmap, isPlaying);
                    notificationManager.notify(NOTIFICATION_ID, notification);
                } catch (IOException e) {
                    Logger.error("Icon uri load fail", e);
                }
            }).start();
        }
        createNotification(context, iconBitmap, isPlaying);
        notificationManager.notify(NOTIFICATION_ID, notification);
    }

    private void createNotification(Context context, Bitmap bitmap, boolean isPlaying) {
        MediaDescriptionCompat description = controller.getMetadata().getDescription();
        notification = new androidx.core.app.NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle(description.getTitle())
                .setContentText(description.getSubtitle())
                .setLargeIcon(bitmap != null ? bitmap : description.getIconBitmap())
                .setStyle(mediaStyle)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentIntent(PendingIntent
                        .getActivity(context, 0, new Intent(context, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT))
                .addAction(new androidx.core.app.NotificationCompat.Action(
                        R.drawable.ic_outline_skip_previous,
                        "pre",
                        MediaButtonReceiver.buildMediaButtonPendingIntent(context, PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS)))
                .addAction(new androidx.core.app.NotificationCompat.Action(
                        isPlaying ? R.drawable.ic_outline_pause : R.drawable.ic_outline_play_arrow,
                        "pause",
                        MediaButtonReceiver.buildMediaButtonPendingIntent(context, PlaybackStateCompat.ACTION_PLAY_PAUSE)))
                .addAction(new androidx.core.app.NotificationCompat.Action(
                        R.drawable.ic_outline_skip_next,
                        "next",
                        MediaButtonReceiver.buildMediaButtonPendingIntent(context, PlaybackStateCompat.ACTION_SKIP_TO_NEXT)))
                .setDeleteIntent(MediaButtonReceiver.buildMediaButtonPendingIntent(context,
                        PlaybackStateCompat.ACTION_STOP))
                .build();
    }

    private void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "播放通知";
            String description = "播放通知";
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void startForeground(Service service) {
        service.startForeground(NOTIFICATION_ID, notification);
    }
}
