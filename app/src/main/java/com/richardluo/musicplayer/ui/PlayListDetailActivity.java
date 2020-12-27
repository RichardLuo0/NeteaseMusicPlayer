package com.richardluo.musicplayer.ui;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.richardluo.musicplayer.R;
import com.richardluo.musicplayer.entity.Album;
import com.richardluo.musicplayer.entity.PlayList;
import com.richardluo.musicplayer.viewModel.PlayListsViewModel;

public class PlayListDetailActivity extends AlbumDetailActivity {

    private PlayListsViewModel playListsViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        playListsViewModel = PlayListsViewModel.getInstance(this);
    }

    @Override
    protected void bindViewHolder(@NonNull SongViewHolder holder, Album.AlbumSong song) {
        super.bindViewHolder(holder, song);
        Context _this = this;
        holder.root.setOnLongClickListener(v -> {
            new MaterialAlertDialogBuilder(_this)
                    .setTitle(R.string.remove_from_play_list)
                    .setMessage(R.string.confirm)
                    .setPositiveButton(getString(android.R.string.ok), (dialog, which) -> playListsViewModel.removeSongFromPlayList((PlayList) album, song))
                    .show();
            return true;
        });
    }
}
