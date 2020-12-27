package com.richardluo.musicplayer.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.richardluo.musicplayer.R;
import com.richardluo.musicplayer.entity.Music;
import com.richardluo.musicplayer.entity.PlayList;
import com.richardluo.musicplayer.ui.component.RecyclerViewFragment;
import com.richardluo.musicplayer.viewModel.CustomViewModelProvider;
import com.richardluo.musicplayer.viewModel.HomeViewModel;
import com.richardluo.musicplayer.viewModel.MusicViewModel;
import com.richardluo.musicplayer.viewModel.PlayListsViewModel;

import java.util.ArrayList;
import java.util.List;

public class MusicFragment extends RecyclerViewFragment<Music> {

    private HomeViewModel homeViewModel;
    private MusicViewModel musicViewModel;
    private PlayListsViewModel playListsViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        homeViewModel = HomeViewModel.getInstance(this);
        musicViewModel = new CustomViewModelProvider(this).get(MusicViewModel.class);
        playListsViewModel = new CustomViewModelProvider(requireActivity()).get(PlayListsViewModel.class);
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
        holder.root.setOnLongClickListener(v -> {
            LiveData<List<PlayList>> playListsLiveData = playListsViewModel.getPlayLists();
            playListsLiveData.observe(this, new Observer<List<PlayList>>() {
                @Override
                public void onChanged(List<PlayList> playLists) {
                    List<CharSequence> selection = new ArrayList<>();
                    if (playLists != null) {
                        playListsLiveData.removeObserver(this);
                        for (PlayList playList : playLists) {
                            selection.add(playList.getName());
                        }
                        new MaterialAlertDialogBuilder(requireContext())
                                .setTitle(R.string.add_to_play_list)
                                .setItems(selection.toArray(new CharSequence[0]), (dialog, which) -> playListsViewModel.addSongToPlayList(playLists.get(which), music.toAlbumSong())).show();
                    }
                }
            });
            return true;
        });
    }

    @Override
    protected void onRefreshData() {
        musicViewModel.refreshMusicList();
    }
}
