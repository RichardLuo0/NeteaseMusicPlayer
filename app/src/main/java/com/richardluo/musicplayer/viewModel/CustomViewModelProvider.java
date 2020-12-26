package com.richardluo.musicplayer.viewModel;

import android.app.Application;

import androidx.activity.ComponentActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import java.util.Objects;

public class CustomViewModelProvider extends ViewModelProvider {

    public CustomViewModelProvider(Fragment fragment) {
        this(fragment, fragment.requireActivity().getApplication());
    }

    public CustomViewModelProvider(ComponentActivity activity) {
        this(activity, activity.getApplication());
    }

    public CustomViewModelProvider(ViewModelStoreOwner owner, Application application) {
        super(owner, ViewModelProvider.AndroidViewModelFactory.getInstance(application));
    }
}
