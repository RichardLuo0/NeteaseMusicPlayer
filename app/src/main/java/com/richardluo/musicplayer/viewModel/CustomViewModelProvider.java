package com.richardluo.musicplayer.viewModel;

import androidx.activity.ComponentActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.Objects;

public class CustomViewModelProvider extends ViewModelProvider {

    public CustomViewModelProvider(Fragment fragment) {
        super(fragment, ViewModelProvider.AndroidViewModelFactory.getInstance(Objects.requireNonNull(fragment.getActivity()).getApplication()));
    }

    public CustomViewModelProvider(ComponentActivity activity) {
        super(activity, ViewModelProvider.AndroidViewModelFactory.getInstance(activity.getApplication()));
    }
}
