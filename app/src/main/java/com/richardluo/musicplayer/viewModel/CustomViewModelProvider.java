package com.richardluo.musicplayer.viewModel;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

public class CustomViewModelProvider extends ViewModelProvider {

    public CustomViewModelProvider(Fragment fragment) {
        super(fragment.getActivity(), ViewModelProvider.AndroidViewModelFactory.getInstance(fragment.getActivity().getApplication()));
    }

    public CustomViewModelProvider(AppCompatActivity activity) {
        super(activity, ViewModelProvider.AndroidViewModelFactory.getInstance(activity.getApplication()));
    }
}
