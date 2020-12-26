package com.richardluo.musicplayer.model;

import com.google.gson.Gson;
import com.richardluo.musicplayer.entity.Album;
import com.richardluo.musicplayer.entity.Music;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.internal.EverythingIsNonNull;


public class NeteaseService {
    public final static String SERVER_ADDRESS = "http://10.0.2.2:3000";

    private static INeteaseService instance;

    public static INeteaseService getInstance() {
        if (instance == null)
            synchronized (NeteaseService.class) {
                if (instance == null) {
                    OkHttpClient client = new OkHttpClient.Builder()
                            .connectTimeout(1, TimeUnit.MINUTES)
                            .readTimeout(5, TimeUnit.SECONDS)
                            .writeTimeout(5, TimeUnit.SECONDS)
                            .build();
                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(SERVER_ADDRESS)
                            .addConverterFactory(new ResultConverterFactory())
                            .client(client)
                            .build();
                    instance = retrofit.create(INeteaseService.class);
                }
            }
        return instance;
    }

    public interface INeteaseService {
        @GET("/personalized/newsong")
        Call<Result<List<Music>>> getNewMusic();

        @GET("/song/url")
        Call<Result<Music.MusicPlayUrl[]>> getMusicPlayUrl(@Query("id") int id);

        @GET("/album/newest")
        Call<Result<List<Album>>> getNewAlbum();

        @GET("/album")
        Call<Result<List<Album.AlbumSong>>> getAlbumSongs(@Query("id") int id);
    }
}

class ResultConverterFactory extends Converter.Factory {
    @Override
    @EverythingIsNonNull
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        if (type instanceof ParameterizedType && ((ParameterizedType) type).getRawType().equals(Result.class))
            return new ResultConverter<>(type);
        return null;
    }
}

class ResultConverter<T> implements Converter<ResponseBody, Result<T>> {
    private final Type type;

    ResultConverter(Type type) {
        this.type = type;
    }

    @Override
    public Result<T> convert(ResponseBody value) throws IOException {
        return new Gson().fromJson(value.string(), type);
    }
}


