package com.richardluo.musicplayer.repository;

import com.google.gson.annotations.SerializedName;

public class Result<T> {
    int code;

    @SerializedName(value = "data", alternate = {"result", "albums", "songs"})
    T data;

    public T getData() {
        return data;
    }
}
