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
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import androidx.core.app.TaskStackBuilder;
import androidx.media.app.NotificationCompat;
import androidx.media.session.MediaButtonReceiver;

import com.richardluo.musicplayer.R;
import com.richardluo.musicplayer.ui.MainActivity;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

public class MediaNotification {
    private final static String CHANNEL_ID = "MEDIA_PLAY";
    private final static int NOTIFICATION_ID = 1;

    private final MediaControllerCompat controller;
    private final NotificationCompat.MediaStyle mediaStyle;
    private Notification notification;
    private final NotificationManager notificationManager;

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
                updateNotification(context, state.getState() == PlaybackStateCompat.STATE_PLAYING);
            }

            @Override
            public void onMetadataChanged(MediaMetadataCompat metadata) {
                super.onMetadataChanged(metadata);
                updateNotification(context, false);
            }
        });
    }

    private Uri iconUri;
    private Bitmap iconBitmap;

    private void updateNotification(Context context, boolean isPlaying) {
        MediaDescriptionCompat description = controller.getMetadata().getDescription();
        Uri descriptionUri = description.getIconUri();
        if (descriptionUri != null && !descriptionUri.equals(iconUri)) {
            Target target = new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    iconBitmap = bitmap;
                    createNotification(context, iconBitmap, isPlaying);
                    notificationManager.notify(NOTIFICATION_ID, notification);
                    iconUri = description.getIconUri();
                }

                @Override
                public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                    Logger.error("Icon uri load fail", e);
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            };
            Picasso.get().load(description.getIconUri()).into(target);
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
                .setSmallIcon(R.drawable.ic_round_music_note)
                .setContentIntent(TaskStackBuilder.create(context)
                        .addNextIntentWithParentStack(new Intent(context, MainActivity.class))
                        .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT))
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
                        "pre",
                        MediaButtonReceiver.buildMediaButtonPendingIntent(context, PlaybackStateCompat.ACTION_SKIP_TO_NEXT)))
                .setDeleteIntent(MediaButtonReceiver.buildMediaButtonPendingIntent(context,
                        PlaybackStateCompat.ACTION_STOP))
                .build();
    }

    private void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "播放通知";
            String description = "播放通知";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
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