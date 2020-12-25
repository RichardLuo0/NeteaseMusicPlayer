package com.richardluo.musicplayer.ui;

import android.os.Bundle;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;
import com.google.android.material.transition.platform.MaterialSharedAxis;
import com.richardluo.musicplayer.R;
import com.richardluo.musicplayer.entity.Music;
import com.richardluo.musicplayer.ui.component.NetworkImageView;
import com.richardluo.musicplayer.utils.Logger;
import com.richardluo.musicplayer.utils.UiUtils;
import com.richardluo.musicplayer.viewModel.CustomViewModelProvider;
import com.richardluo.musicplayer.viewModel.HomeViewModel;
import com.richardluo.musicplayer.viewModel.MusicViewModel;

public class MusicFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private MusicViewModel musicViewModel;

    private SwipeRefreshLayout refreshLayout;

    private final MaterialSharedAxis sharedAxis = new MaterialSharedAxis(MaterialSharedAxis.Y, true);

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        homeViewModel = new CustomViewModelProvider(getActivity()).get(HomeViewModel.class);
        musicViewModel = new CustomViewModelProvider(this).get(MusicViewModel.class);

        Logger.addOnException(this, e -> {
            if (refreshLayout != null) refreshLayout.setRefreshing(false);
        });
    }

    static class MusicViewHolder extends RecyclerView.ViewHolder {
        final MaterialCardView root;
        final MaterialTextView name;
        final MaterialTextView artist;
        final NetworkImageView imageView;

        MusicViewHolder(@NonNull View itemView) {
            super(itemView);
            root = (MaterialCardView) itemView;
            name = itemView.findViewById(R.id.music_name);
            artist = itemView.findViewById(R.id.music_artist);
            imageView = itemView.findViewById(R.id.music_image);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_music, container, false);
        refreshLayout = root.findViewById(R.id.refresh);
        RecyclerView grid = root.findViewById(R.id.grid);
        // Keep space for navigation bar
        UiUtils.setSystemPadding(grid, UiUtils.SystemPadding.BOTTOM);

        initGrid(refreshLayout, grid);
        return root;
    }

    protected void initGrid(SwipeRefreshLayout refreshLayout, RecyclerView grid) {
        UiUtils.bindRecyclerViewViewWithLiveData(this, grid, musicViewModel.getMusicList(), new UiUtils.Bindable<MusicViewHolder, Music>() {
            @Override
            protected MusicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.music_item, parent, false);
                return new MusicViewHolder(v);
            }

            @Override
            protected void bind(@NonNull MusicViewHolder holder, Music music) {
                holder.name.setText(music.getName());
                if (music.getArtist() != null)
                    holder.artist.setText(music.getArtist().getName());
                holder.imageView.setImage(music.getPicUrl(), R.drawable.ic_round_music_note);
                holder.root.setOnClickListener(v -> homeViewModel.setPlayingMusic(music, true));
            }

            @Override
            protected void onUpdate() {
                onDataReady(grid);
            }
        });
        refreshData(grid);
        refreshLayout.setOnRefreshListener(() -> refreshData(grid));
    }

    private void refreshData(RecyclerView grid) {
        TransitionManager.beginDelayedTransition(refreshLayout, sharedAxis);
        grid.setVisibility(View.INVISIBLE);
        musicViewModel.refreshMusicList();
        refreshLayout.setRefreshing(true);
    }

    private void onDataReady(RecyclerView grid) {
        TransitionManager.beginDelayedTransition(refreshLayout, sharedAxis);
        grid.setVisibility(View.VISIBLE);
        refreshLayout.setRefreshing(false);
    }
}
