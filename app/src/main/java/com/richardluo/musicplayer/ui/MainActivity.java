package com.richardluo.musicplayer.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.preference.PreferenceManager;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayoutMediator;
import com.richardluo.musicplayer.R;
import com.richardluo.musicplayer.ui.Adapter.SectionsPagerAdapter;
import com.richardluo.musicplayer.ui.component.BottomSheetMusicPlayerActivity;
import com.richardluo.musicplayer.ui.component.CustomBottomNavigationView;
import com.richardluo.musicplayer.ui.component.CustomDrawer;
import com.richardluo.musicplayer.ui.component.NetworkImageView;
import com.richardluo.musicplayer.utils.UiUtils;
import com.richardluo.musicplayer.viewModel.HomeViewModel;

import java.util.Objects;

import static android.view.View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
import static android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
import static android.view.View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
import static android.view.View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;

public class MainActivity extends BottomSheetMusicPlayerActivity {

    int navStyle;

    private HomeViewModel viewModel;

    private ViewPager2 viewPager;
    private FloatingActionButton addButton;

    @Override
    protected HomeViewModel getHomeViewModel() {
        return viewModel;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
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

        viewModel = HomeViewModel.getInstance(this);

        addButton = findViewById(R.id.add_button);
        addButton.hide();

        initAppBar();
        initViewPager();
        initPlayer();

        if (navStyle != 1) {
            View miniPlayer = findViewById(R.id.mini_player);
            UiUtils.setSystemPadding(miniPlayer.findViewById(R.id.player_horizontal_view), UiUtils.SystemPadding.BOTTOM);
            bottomSheetBehavior.setPeekHeight((int) (80 * getResources().getDisplayMetrics().density));
            UiUtils.setHeight(this, miniPlayer, 80);
        }
    }

    public void initAppBar() {
        MaterialToolbar appBar = findViewById(R.id.appBar);
        setSupportActionBar(appBar);
        AppBarLayout appBarLayout = findViewById(R.id.appbarLayout);
        if (navStyle == 1) appBarLayout.setLiftOnScroll(true);
        else if (navStyle == 2) {
            DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);
            ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, appBar, R.string.drawer_open, R.string.drawer_close);
            drawerLayout.addDrawerListener(drawerToggle);
            drawerToggle.syncState();
            appBar.setNavigationOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));
        }
        // app bar title will disappear gradually when scrolling
        appBarLayout.addOnOffsetChangedListener((appBarLayout1, verticalOffset) -> {
            // plus because verticalOffset is negative
            appBar.setAlpha(1f + verticalOffset / (float) appBar.getHeight());
        });
    }

    private final int[] tabNames = new int[]{R.string.music, R.string.album, R.string.play_list};
    private final int[] tabIcons = new int[]{R.drawable.ic_notification, R.drawable.ic_outline_album, R.drawable.ic_outline_playlist_play};

    private final ViewPager2.OnPageChangeCallback onPageChangeCallback = new ViewPager2.OnPageChangeCallback() {
        @Override
        public void onPageSelected(int position) {
            Objects.requireNonNull(getSupportActionBar()).setTitle(tabNames[position]);

            if (position == 2)
                addButton.show();
            else addButton.hide();

            if (navStyle == 1) {
                ((CustomBottomNavigationView) findViewById(R.id.bottomNavigation)).setSelectedItemId(position);
            }
        }
    };

    protected void initViewPager() {
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
                CustomDrawer drawer = findViewById(R.id.drawer);
                drawer.setupWithViewPager(viewPager, tabNames, tabIcons);
                ((NetworkImageView) drawer.getHeaderView(0).findViewById(R.id.background)).setImage("https://api.dujin.org/bing/1366.php", R.drawable.ic_round_music_note);
                viewPager.setOrientation(ViewPager2.ORIENTATION_VERTICAL);
                viewPager.setUserInputEnabled(false);
                break;
        }

        viewPager.registerOnPageChangeCallback(onPageChangeCallback);
    }

    public FloatingActionButton getAddButton() {
        return addButton;
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
    protected void onDestroy() {
        super.onDestroy();
        viewPager.unregisterOnPageChangeCallback(onPageChangeCallback);
    }
}