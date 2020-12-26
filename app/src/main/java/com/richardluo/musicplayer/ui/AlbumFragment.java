package com.richardluo.musicplayer.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import com.richardluo.musicplayer.R;
import com.richardluo.musicplayer.entity.Album;
import com.richardluo.musicplayer.ui.component.RecyclerViewFragment;
import com.richardluo.musicplayer.viewModel.AlbumViewModel;
import com.richardluo.musicplayer.viewModel.CustomViewModelProvider;

import java.util.List;

public class AlbumFragment extends RecyclerViewFragment<Album> {

    AlbumViewModel albumViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        albumViewModel = new CustomViewModelProvider(this).get(AlbumViewModel.class);
    }

    @NonNull
    @Override
    protected LiveData<List<Album>> getLiveData() {
        return albumViewModel.getAlbumList();
    }

    @Override
    protected void bindViewHolder(@NonNull MusicViewHolder holder, Album album) {
        holder.name.setText(album.getName());
        if (album.getArtist() != null)
            holder.artist.setText(album.getArtist().getName());
        holder.imageView.setImage(album.getPicUrl(), R.drawable.ic_round_music_note);
        holder.root.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AlbumDetailActivity.class);
            intent.putExtra("album", album);
            startActivity(intent);
        });
    }

    @Override
    protected void onRefreshData() {
        albumViewModel.refreshAlbumList();
    }
}
