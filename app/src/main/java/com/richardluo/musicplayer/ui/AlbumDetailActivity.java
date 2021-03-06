package com.richardluo.musicplayer.ui;

import android.os.Bundle;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textview.MaterialTextView;
import com.google.android.material.transition.platform.MaterialSharedAxis;
import com.richardluo.musicplayer.R;
import com.richardluo.musicplayer.entity.Album;
import com.richardluo.musicplayer.ui.component.BottomSheetMusicPlayerActivity;
import com.richardluo.musicplayer.ui.component.NetworkImageView;
import com.richardluo.musicplayer.utils.Logger;
import com.richardluo.musicplayer.utils.UiUtils;
import com.richardluo.musicplayer.utils.UnixTimeFormat;
import com.richardluo.musicplayer.viewModel.HomeViewModel;

import java.util.Objects;

public class AlbumDetailActivity extends BottomSheetMusicPlayerActivity {

    HomeViewModel homeViewModel;

    Album album;

    private final MaterialSharedAxis sharedAxis = new MaterialSharedAxis(MaterialSharedAxis.Y, true);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_detail);

        homeViewModel = HomeViewModel.getInstance(this);

        album = ((Album) getIntent().getSerializableExtra("album"));

        MaterialToolbar appBar = findViewById(R.id.appBar);
        setSupportActionBar(appBar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        appBar.setTitle(album.getName());

        NetworkImageView background = findViewById(R.id.background);
        if (album.hasPic())
            background.setImage(album.getPicUrl(), R.drawable.ic_round_music_note);
        else background.setImage(R.drawable.ic_round_music_note);

        initSongList();
        initPlayer();

        View miniPlayer = findViewById(R.id.mini_player);
        UiUtils.setSystemPadding(miniPlayer.findViewById(R.id.player_horizontal_view), UiUtils.SystemPadding.BOTTOM);
        bottomSheetBehavior.setPeekHeight((int) (80 * getResources().getDisplayMetrics().density));
        UiUtils.setHeight(this, miniPlayer, 80);
    }

    @Override
    protected HomeViewModel getHomeViewModel() {
        return homeViewModel;
    }

    static class SongViewHolder extends RecyclerView.ViewHolder {
        LinearLayout root;
        MaterialTextView orderView;
        MaterialTextView titleView;
        MaterialTextView artistView;
        ImageView needMoneyView;

        public SongViewHolder(@NonNull View itemView) {
            super(itemView);
            root = itemView.findViewById(R.id.root);
            orderView = itemView.findViewById(R.id.order);
            titleView = itemView.findViewById(R.id.title);
            artistView = itemView.findViewById(R.id.artist);
            needMoneyView = itemView.findViewById(R.id.need_money);
        }
    }

    public void initSongList() {
        RecyclerView recyclerView = findViewById(R.id.list);
        UiUtils.bindRecyclerViewViewWithLiveData(this, recyclerView, album.getSongs(), new UiUtils.Bindable<SongViewHolder, Album.AlbumSong>() {
            @Override
            protected SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.song_item, parent, false);
                return new SongViewHolder(v);
            }

            @Override
            protected void bind(@NonNull SongViewHolder holder, Album.AlbumSong song) {
                bindViewHolder(holder, song);
            }

            @Override
            protected void onUpdate() {
                super.onUpdate();
                TransitionManager.beginDelayedTransition(recyclerView, sharedAxis);
                recyclerView.setVisibility(View.VISIBLE);
            }
        });
    }

    protected void bindViewHolder(@NonNull SongViewHolder holder, Album.AlbumSong song) {
        holder.orderView.setText(String.valueOf(song.getNo()));
        holder.titleView.setText(song.getName());
        holder.artistView.setText(UnixTimeFormat.format(song.getDuration()));
        if (song.getFee() > 0) {
            holder.needMoneyView.setVisibility(View.VISIBLE);
            holder.root.setOnClickListener(v -> Logger.info(song.getName() + "???????????????!"));
        } else
            holder.root.setOnClickListener(v -> homeViewModel.setPlayingMusic(song.toMusic(album), true));
    }
}
