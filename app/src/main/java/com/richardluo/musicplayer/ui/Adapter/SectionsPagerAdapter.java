package com.richardluo.musicplayer.ui.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.richardluo.musicplayer.utils.Logger;

public class SectionsPagerAdapter extends FragmentStateAdapter {
    private final Class<?>[] fragments;

    public SectionsPagerAdapter(FragmentActivity fm, Class<?>[] fragments) {
        super(fm);
        this.fragments = fragments;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        try {
            return (Fragment) fragments[position].newInstance();
        } catch (Exception e) {
            Logger.error("Fragment init fail", e);
        }
        return new Fragment();
    }

    @Override
    public int getItemCount() {
        return fragments.length;
    }
}
