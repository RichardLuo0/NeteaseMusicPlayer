package com.richardluo.musicplayer.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.richardluo.musicplayer.R;
import com.richardluo.musicplayer.entity.PlayList;
import com.richardluo.musicplayer.ui.component.EditTextDialogBuilder;
import com.richardluo.musicplayer.ui.component.RecyclerViewFragment;
import com.richardluo.musicplayer.viewModel.PlayListsViewModel;

import java.util.List;

public class PlayListFragment extends RecyclerViewFragment<PlayList> {

    private PlayListsViewModel playListsViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        playListsViewModel = PlayListsViewModel.getInstance(requireActivity());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ((MainActivity) requireActivity()).getAddButton().setOnClickListener(v ->
                new EditTextDialogBuilder(requireContext()).setTitle(R.string.create_play_list).setCallBack(getLayoutInflater(), text -> {
                    playListsViewModel.createPlayList(text);
                    return true;
                }).setHint(R.string.play_list).show());
        View root = super.onCreateView(inflater, container, savedInstanceState);
        assert root != null;
        root.findViewById(R.id.refresh).setEnabled(false);
        return root;
    }

    @NonNull
    @Override
    protected LiveData<List<PlayList>> getLiveData() {
        return playListsViewModel.getPlayLists();
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void bindViewHolder(@NonNull MusicViewHolder holder, PlayList album) {
        holder.name.setText(album.getName());
        holder.artist.setText(album.getArtist().getName() + " " + getString(R.string.songs));
        if (album.hasPic())
            holder.imageView.setImage(album.getPicUrl(), R.drawable.ic_round_music_note);
        else
            holder.imageView.setImage(R.drawable.ic_round_music_note);
        holder.root.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), PlayListDetailActivity.class);
            intent.putExtra("album", album);
            startActivity(intent);
        });
        holder.root.setOnLongClickListener(v -> {
            new MaterialAlertDialogBuilder(requireContext())
                    .setTitle(R.string.delete_play_list)
                    .setMessage(R.string.confirm)
                    .setPositiveButton(getString(android.R.string.ok), (dialog, which) -> playListsViewModel.removePlayList(album))
                    .show();
            return true;
        });
    }

    @Override
    protected void onRefreshData() {
    }
}
