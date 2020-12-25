package com.richardluo.musicplayer.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Range;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.LiveData;
import androidx.preference.PreferenceManager;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.shape.CornerFamily;
import com.google.android.material.shape.ShapeAppearanceModel;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.richardluo.musicplayer.R;
import com.richardluo.musicplayer.entity.Music;
import com.richardluo.musicplayer.ui.Adapter.SectionsPagerAdapter;
import com.richardluo.musicplayer.ui.component.CustomBottomNavigationView;
import com.richardluo.musicplayer.ui.component.CustomDrawer;
import com.richardluo.musicplayer.ui.component.MusicPlayerView;
import com.richardluo.musicplayer.utils.UiUtils;
import com.richardluo.musicplayer.viewModel.CustomViewModelProvider;
import com.richardluo.musicplayer.viewModel.HomeViewModel;

import java.util.Objects;

import static android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
import static android.view.View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
import static android.view.View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;

public class MainActivity extends BaseActivity {

    int navStyle;

    private HomeViewModel viewModel;

    private BottomSheetBehavior<?> bottomSheetBehavior;
    private MusicPlayerView musicPlayerView;
    private ViewPager2 viewPager;
    private FloatingActionButton addButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        navStyle = Integer.parseInt(sharedPreferences.getString("navigation_style", "0"));

        ((App) getApplication()).observePreferenceChange(this, key -> {
            if ("navigation_style".equals(key)) recreate();
        });

        switch (navStyle) {
            case 0:
                setContentView(R.layout.activity_main);
                break;
            case 1:
                setContentView(R.layout.activity_bottom_navigation);
                getWindow().getDecorView().setSystemUiVisibility(SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR | SYSTEM_UI_FLAG_LAYOUT_STABLE);
                break;
            case 2:
                setContentView(R.layout.activity_navigation_drawer);
                break;
        }

        viewModel = new CustomViewModelProvider(this).get(HomeViewModel.class);

        addButton = findViewById(R.id.add_button);
        addButton.hide();

        initAppBar();
        initViewPager();
        initBottomSheet();
        initMusicPlayer();
    }

    public void initAppBar() {
        MaterialToolbar appBar = findViewById(R.id.appBar);
        setSupportActionBar(appBar);
        if (navStyle == 2) {
            DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);
            ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, appBar, R.string.drawer_open, R.string.drawer_close);
            drawerLayout.addDrawerListener(drawerToggle);
            drawerToggle.syncState();
            appBar.setNavigationOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));
        }
        // app bar title will disappear gradually when scrolling
        ((AppBarLayout) findViewById(R.id.appbarLayout)).addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
            // plus because verticalOffset is negative
            appBar.setAlpha(1f + verticalOffset / (float) appBar.getHeight());
        });
    }

    private final ViewPager2.OnPageChangeCallback onPageChangeCallback = new ViewPager2.OnPageChangeCallback() {
        @Override
        public void onPageSelected(int position) {
            if (position == 2)
                addButton.show();
            else addButton.hide();

            if (navStyle == 1) {
                ((CustomBottomNavigationView) findViewById(R.id.bottomNavigation)).setSelectedItemId(position);
            }
        }
    };

    protected void initViewPager() {
        int[] tabNames = new int[]{R.string.music, R.string.album, R.string.play_list};
        int[] tabIcons = new int[]{R.drawable.ic_notification, R.drawable.ic_outline_album, R.drawable.ic_outline_playlist_play};
        viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(new SectionsPagerAdapter(this, new Class<?>[]{MusicFragment.class, AlbumFragment.class, PlayListFragment.class}));

        switch (navStyle) {
            case 0:
                new TabLayoutMediator(findViewById(R.id.tabs), viewPager, (tab, position) -> tab.setText(getResources().getString(tabNames[position]))).attach();
                break;
            case 1:
                findViewById(R.id.tabs).setVisibility(View.GONE);
                ((CustomBottomNavigationView) findViewById(R.id.bottomNavigation)).setupWithViewPager(viewPager, tabNames, tabIcons);
                break;
            case 2:
                findViewById(R.id.tabs).setVisibility(View.GONE);
                ((CustomDrawer) findViewById(R.id.drawer)).setupWithViewPager(viewPager, tabNames, tabIcons);
                viewPager.setOrientation(ViewPager2.ORIENTATION_VERTICAL);
                viewPager.setUserInputEnabled(false);
                break;
        }

        viewPager.registerOnPageChangeCallback(onPageChangeCallback);
    }

    protected void initBottomSheet() {
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

    protected void initMusicPlayer() {
        View miniPlayer = findViewById(R.id.mini_player);
        View expandPlayer = findViewById(R.id.expand_player);
        if (navStyle != 1) {
            UiUtils.setSystemPadding(miniPlayer.findViewById(R.id.player_horizontal_view), UiUtils.SystemPadding.BOTTOM);
            bottomSheetBehavior.setPeekHeight((int) (80 * getResources().getDisplayMetrics().density));
            UiUtils.setHeight(this, miniPlayer, 80);
        }
        musicPlayerView = new MusicPlayerView(this, miniPlayer, expandPlayer);
        LiveData<Music> playingMusic = viewModel.getPlayingMusic();
        if (playingMusic.getValue() == null)
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        playingMusic.observe(this, music -> {
            if (music == null)
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            else
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            musicPlayerView.setMusic(music);
            musicPlayerView.play();
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.settings) {
            startActivity(new Intent(this, PreferenceActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
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
        viewPager.unregisterOnPageChangeCallback(onPageChangeCallback);
    }
}