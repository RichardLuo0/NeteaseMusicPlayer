package com.richardluo.musicplayer.ui.component;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Range;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.shape.CornerFamily;
import com.google.android.material.shape.ShapeAppearanceModel;
import com.richardluo.musicplayer.R;
import com.richardluo.musicplayer.entity.Music;
import com.richardluo.musicplayer.utils.UiUtils;
import com.richardluo.musicplayer.viewModel.HomeViewModel;

public abstract class BottomSheetMusicPlayerActivity extends BaseActivity {

    protected BottomSheetBehavior<?> bottomSheetBehavior;
    protected MusicPlayerView musicPlayerView;

    protected abstract HomeViewModel getHomeViewModel();

    protected void initPlayer() {
        initBottomSheet();
        initMusicPlayer();
    }

    private void initBottomSheet() {
        MaterialCardView bottomPlayer = findViewById(R.id.bottomPlayer);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomPlayer);
        bottomPlayer.setOnClickListener(v -> bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED));
        Range<Float> range = new Range<>(0f, 1f);
        Color color;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int colorInt = bottomPlayer.getCardBackgroundColor().getDefaultColor();
            color = Color.valueOf(colorInt);
        } else color = new Color();
        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {

            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    musicPlayerView.pause();
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                if (slideOffset < 0) return;
                // set shape
                MaterialCardView cardView = (MaterialCardView) bottomSheet;
                float roundSize = slideOffset == 1 ? 0 : slideOffset * 100;
                ShapeAppearanceModel model = cardView.getShapeAppearanceModel().toBuilder()
                        .setTopLeftCorner(CornerFamily.ROUNDED, roundSize)
                        .setTopRightCorner(CornerFamily.ROUNDED, roundSize)
                        .build();
                cardView.setShapeAppearanceModel(model);
                // set color
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    float alpha = range.clamp(slideOffset + 0.9f);
                    float red = range.clamp(color.red() * (slideOffset + 1));
                    float green = range.clamp(color.green() * (slideOffset + 1));
                    float blue = range.clamp(color.blue() * (slideOffset + 1));
                    cardView.setCardBackgroundColor(Color.valueOf(red, green, blue, alpha).toArgb());
                }
                musicPlayerView.onSliding(slideOffset);
            }
        });
    }

    private void initMusicPlayer() {
        View miniPlayer = findViewById(R.id.mini_player);
        View expandPlayer = findViewById(R.id.expand_player);
        musicPlayerView = new MusicPlayerView(this, miniPlayer, expandPlayer);
        LiveData<Music> playingMusic = getHomeViewModel().getPlayingMusic();
        if (playingMusic.getValue() == null)
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        playingMusic.observe(this, music -> {
            if (music == null)
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            else
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            musicPlayerView.setMusic(music);
            if (getHomeViewModel().consumeRequestPlayNow())
                musicPlayerView.play();
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        musicPlayerView.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        musicPlayerView.resume(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        musicPlayerView.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        musicPlayerView.destroy();
    }
}
