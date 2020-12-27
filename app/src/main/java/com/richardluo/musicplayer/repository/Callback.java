package com.richardluo.musicplayer.repository;

import com.richardluo.musicplayer.utils.Logger;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.internal.EverythingIsNonNull;

public abstract class Callback<T> implements retrofit2.Callback<Result<T>> {
    public abstract void onResponse(T res);

    @Override
    @EverythingIsNonNull
    public void onResponse(Call<Result<T>> call, Response<Result<T>> response) {
        assert response.body() != null;
        Result<T> result = response.body();
        int code = result.code;
        if (code == 200)
            onResponse(result.getData());
        else Logger.warn("Something wrong");
    }

    @Override
    @EverythingIsNonNull
    public void onFailure(Call<Result<T>> call, Throwable t) {
        Logger.error("Connection lost", t);
    }
}
