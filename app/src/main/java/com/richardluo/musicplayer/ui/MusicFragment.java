package com.richardluo.musicplayer.ui;

import android.os.Bundle;
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
import com.richardluo.musicplayer.R;
import com.richardluo.musicplayer.entity.Music;
import com.richardluo.musicplayer.ui.component.NetworkImageView;
import com.richardluo.musicplayer.utils.Logger;
import com.richardluo.musicplayer.utils.UiUtils;
import com.richardluo.musicplayer.viewModel.CustomViewModelProvider;
import com.richardluo.musicplayer.viewModel.HomeViewModel;

public class MusicFragment extends Fragment {

    int type;
    private HomeViewModel viewModel;

    private SwipeRefreshLayout refreshLayout;

    public static MusicFragment newInstance(int type) {
        Bundle args = new Bundle();
        args.putInt("page", type);
        MusicFragment fragment = new MusicFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = new CustomViewModelProvider(this).get(HomeViewModel.class);

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
        UiUtils.setSystemPadding(root, UiUtils.SystemPadding.BOTTOM);

        initGrid(refreshLayout, grid);
        return root;
    }

    protected void initGrid(SwipeRefreshLayout refreshLayout, RecyclerView grid) {
        UiUtils.bindRecyclerViewViewWithLiveData(this, grid, viewModel.getMusicList(), new UiUtils.Bindable<MusicViewHolder, Music>() {
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
                holder.imageView.setImage(music.getPicUrl(), R.drawable.ic_round_music_note,1900,1400);
                holder.root.setOnClickListener(v -> viewModel.setPlayingMusic(music));
            }

            @Override
            protected void onUpdate() {
                refreshLayout.setRefreshing(false);
            }
        });
        viewModel.refreshMusicList();

        refreshLayout.setOnRefreshListener(() -> viewModel.refreshMusicList());
    }
}
