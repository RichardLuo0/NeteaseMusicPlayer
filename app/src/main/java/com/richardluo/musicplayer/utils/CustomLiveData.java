package com.richardluo.musicplayer.utils;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

public class CustomLiveData<T> extends MutableLiveData<T> {

    int mVersion = -1;

    @SuppressWarnings("unchecked")
    @Override
    public void observe(@NonNull LifecycleOwner owner, @NonNull androidx.lifecycle.Observer<? super T> observer) {
        super.observe(owner, new Observer<T>((androidx.lifecycle.Observer<T>) observer, this));
    }

    @Override
    public void postValue(T value) {
        mVersion++;
        super.postValue(value);
    }

    @Override
    public void setValue(T value) {
        mVersion++;
        super.setValue(value);
    }

    public static class Observer<T> implements androidx.lifecycle.Observer<T> {

        private final CustomLiveData<T> liveData;
        private final androidx.lifecycle.Observer<T> observer;
        private int mLastVersion;

        Observer(androidx.lifecycle.Observer<T> observer, CustomLiveData<T> liveData) {
            this.observer = observer;
            this.liveData = liveData;
            mLastVersion = liveData.mVersion;
        }

        @Override
        public void onChanged(T t) {
            if (mLastVersion >= liveData.mVersion) {
                return;
            }
            mLastVersion = liveData.mVersion;
            observer.onChanged(t);
        }
    }
}
