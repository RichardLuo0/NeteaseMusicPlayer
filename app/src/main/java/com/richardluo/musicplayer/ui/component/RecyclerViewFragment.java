package com.richardluo.musicplayer.ui.component;

import android.os.Bundle;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;
import com.google.android.material.transition.platform.MaterialSharedAxis;
import com.richardluo.musicplayer.R;
import com.richardluo.musicplayer.utils.Logger;
import com.richardluo.musicplayer.utils.UiUtils;

import java.util.List;

public abstract class RecyclerViewFragment<T extends UiUtils.Identifiable> extends Fragment {

    private SwipeRefreshLayout refreshLayout;

    private final MaterialSharedAxis sharedAxis = new MaterialSharedAxis(MaterialSharedAxis.Y, true);

    @NonNull
    protected abstract LiveData<List<T>> getLiveData();

    protected abstract void bindViewHolder(@NonNull MusicViewHolder holder, T item);

    protected abstract void onRefreshData();

    public static class MusicViewHolder extends RecyclerView.ViewHolder {
        public final MaterialCardView root;
        public final MaterialTextView name;
        public final MaterialTextView artist;
        public final NetworkImageView imageView;

        MusicViewHolder(@NonNull View itemView) {
            super(itemView);
            root = (MaterialCardView) itemView;
            name = itemView.findViewById(R.id.music_name);
            artist = itemView.findViewById(R.id.music_artist);
            imageView = itemView.findViewById(R.id.music_image);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Logger.addOnException(this, e -> {
            if (refreshLayout != null) refreshLayout.setRefreshing(false);
        });
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
        UiUtils.bindRecyclerViewViewWithLiveData(this, grid, getLiveData(), new UiUtils.Bindable<MusicViewHolder, T>() {
            @Override
            protected MusicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.music_item, parent, false);
                return new MusicViewHolder(v);
            }

            @Override
            protected void bind(@NonNull MusicViewHolder holder, T item) {
                bindViewHolder(holder, item);
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
        onRefreshData();
        refreshLayout.setRefreshing(true);
    }

    private void onDataReady(RecyclerView grid) {
        TransitionManager.beginDelayedTransition(refreshLayout, sharedAxis);
        grid.setVisibility(View.VISIBLE);
        refreshLayout.setRefreshing(false);
    }
}
