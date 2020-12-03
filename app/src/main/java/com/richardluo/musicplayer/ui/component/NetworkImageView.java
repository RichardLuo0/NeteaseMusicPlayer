package com.richardluo.musicplayer.ui.component;

import android.content.Context;
import android.net.Uri;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

import com.google.android.material.imageview.ShapeableImageView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

public class NetworkImageView extends ShapeableImageView {
    public NetworkImageView(Context context) {
        super(context);
    }

    public NetworkImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public NetworkImageView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    private RequestCreator getImage(String url, int placeholder) {
        return Picasso.get().load(url).placeholder(placeholder).error(placeholder);
    }

    public void setImage(String url, int placeholder) {
        setImage(url, placeholder, getWidth(), getHeight());
    }

    public void setImage(String url, int placeholder, int width, int height) {
        getImage(url, placeholder).centerCrop().resize(width, height).onlyScaleDown().into(this);
    }

    private RequestCreator getImage(Uri uri, int placeholder) {
        return Picasso.get().load(uri).placeholder(placeholder).error(placeholder);
    }

    public void setImage(Uri uri, int placeholder) {
        setImage(uri, placeholder, getWidth(), getHeight());
    }

    public void setImage(Uri uri, int placeholder, int width, int height) {
        getImage(uri, placeholder).centerCrop().resize(width, height).onlyScaleDown().into(this);
    }

    private RequestCreator getImage(int id, int placeholder) {
        return Picasso.get().load(id).placeholder(placeholder).error(placeholder);
    }

    public void setImage(int id, int placeholder) {
        setImage(id, placeholder, getWidth(), getHeight());
    }

    public void setImage(int id, int placeholder, int width, int height) {
        getImage(id, placeholder).centerCrop().resize(width, height).onlyScaleDown().into(this);
    }
}
