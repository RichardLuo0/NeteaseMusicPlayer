package com.richardluo.musicplayer.ui;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Range;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.shape.CornerFamily;
import com.google.android.material.shape.ShapeAppearanceModel;
import com.google.android.material.tabs.TabLayout;
import com.richardluo.musicplayer.R;
import com.richardluo.musicplayer.ui.component.MusicPlayerView;
import com.richardluo.musicplayer.utils.Logger;
import com.richardluo.musicplayer.viewModel.CustomViewModelProvider;
import com.richardluo.musicplayer.viewModel.HomeViewModel;

public class MainActivity extends BaseActivity {

    private HomeViewModel viewModel;
    private BottomSheetBehavior<?> bottomSheetBehavior;
    MusicPlayerView musicPlayerView;
    private FloatingActionButton addButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewModel = new CustomViewModelProvider(this).get(HomeViewModel.class);

        MaterialToolbar appBar = findViewById(R.id.appBar);
        addButton = findViewById(R.id.add_button);
        addButton.hide();
        setSupportActionBar(appBar);
        // app bar text disappear gradually when scrolling
        ((AppBarLayout) findViewById(R.id.appbarLayout)).addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
            // plus because verticalOffset is negative
            appBar.setAlpha(1f + verticalOffset / (float) appBar.getHeight());
        });
        bottomSheetBehavior = BottomSheetBehavior.from(findViewById(R.id.bottomPlayer));
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        initViewPager();
        initBottomSheet();
        initMusicPlayer();
    }

    static class SectionsPagerAdapter extends FragmentPagerAdapter {
        private final Context mContext;

        private final int[] tabs = new int[]{R.string.music, R.string.ablum, R.string.play_list};
        private final Class<?>[] fragmentClass = new Class<?>[]{MusicFragment.class, AlbumFragment.class, PlayListFragment.class};

        public SectionsPagerAdapter(Context context, FragmentManager fm) {
            super(fm, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
            mContext = context;
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            try {
                return (Fragment) fragmentClass[position].newInstance();
            } catch (Exception e) {
                Logger.error("Fragment init fail", e);
            }
            return new Fragment();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return mContext.getResources().getString(tabs[position]);
        }

        @Override
        public int getCount() {
            return tabs.length;
        }
    }

    protected void initViewPager() {
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(new SectionsPagerAdapter(this, getSupportFragmentManager()));
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (position == 2)
                    addButton.show();
                else addButton.hide();
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    protected void initBottomSheet() {
        MaterialCardView bottomPlayer = findViewById(R.id.bottomPlayer);
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
                ShapeAppearanceModel model = cardView.getShapeAppearanceModel().toBuilder().setTopLeftCorner(CornerFamily.ROUNDED, slideOffset * 100)
                        .setTopRightCorner(CornerFamily.ROUNDED, slideOffset * 100)
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

    protected void initMusicPlayer() {
        musicPlayerView = new MusicPlayerView(this, findViewById(R.id.mini_player), findViewById(R.id.expand_player));
        viewModel.getPlayingMusic().observe(this, music -> {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            musicPlayerView.setMusic(music);
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