package com.richardluo.musicplayer.ui.component;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Menu;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class CustomBottomNavigationView extends BottomNavigationView {

    public CustomBottomNavigationView(@NonNull Context context) {
        super(context);
    }

    public CustomBottomNavigationView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomBottomNavigationView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setupWithViewPager(ViewPager2 viewPager, @StringRes int[] itemNames, @DrawableRes int[] itemIcons) {
        Menu menu = getMenu();
        for (int i = 0; i < itemNames.length; i++) {
            menu.add(Menu.NONE, i, i, itemNames[i]).setIcon(itemIcons[i]);
        }
        this.setOnNavigationItemSelectedListener(item -> {
            viewPager.setCurrentItem(item.getItemId());
            return true;
        });
    }
}
