package com.richardluo.musicplayer.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.richardluo.musicplayer.R;
import com.richardluo.musicplayer.ui.component.EditTextDialogBuilder;

import java.util.Objects;

public class PlayListFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_playlist, container, false);

        ((MainActivity) requireActivity()).getAddButton().setOnClickListener(v -> {
            new EditTextDialogBuilder(requireContext()).setTitle(R.string.create_play_list).setCallBack(getLayoutInflater(), text -> {
                return true;
            }).setHint(R.string.play_list).show();
        });
        return root;
    }
}
