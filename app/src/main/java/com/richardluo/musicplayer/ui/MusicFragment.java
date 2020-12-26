package com.richardluo.musicplayer.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import com.richardluo.musicplayer.R;
import com.richardluo.musicplayer.entity.Music;
import com.richardluo.musicplayer.ui.component.RecyclerViewFragment;
import com.richardluo.musicplayer.viewModel.CustomViewModelProvider;
import com.richardluo.musicplayer.viewModel.HomeViewModel;
import com.richardluo.musicplayer.viewModel.MusicViewModel;

import java.util.List;

public class MusicFragment extends RecyclerViewFragment<Music> {

    private HomeViewModel homeViewModel;
    private MusicViewModel musicViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        homeViewModel = HomeViewModel.getInstance(this);
        musicViewModel = new CustomViewModelProvider(this).get(MusicViewModel.class);
    }

    @NonNull
    @Override
    protected LiveData<List<Music>> getLiveData() {
        return musicViewModel.getMusicList();
    }

    @Override
    protected void bindViewHolder(@NonNull MusicViewHolder holder, Music music) {
        holder.name.setText(music.getName());
        if (music.getArtist() != null)
            holder.artist.setText(music.getArtist().getName());
        holder.imageView.setImage(music.getPicUrl(), R.drawable.ic_round_music_note);
        holder.root.setOnClickListener(v -> homeViewModel.setPlayingMusic(music, true));
    }

    @Override
    protected void onRefreshData() {
        musicViewModel.refreshMusicList();
    }
}
