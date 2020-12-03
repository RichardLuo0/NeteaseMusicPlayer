package com.richardluo.musicplayer.model;

import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

public class RepoProvider {
    private static final HashMap<Class<?>, Object> viewModels = new HashMap<>();

    @SuppressWarnings("unchecked")
    public static <T> T get(Class<T> c) {
        T object = (T) viewModels.get(c);
        if (object == null) {
            try {
                object = c.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                Log.e("Repo init failed", "get: ", e);
            }
            viewModels.put(c, object);
        }
        return object;
    }
}
