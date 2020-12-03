package com.richardluo.musicplayer.model;

public class Result<T> {
    int code;
    int category;
    T result;
    T data;

    public T getData() {
        return result == null ? data : result;
    }
}
