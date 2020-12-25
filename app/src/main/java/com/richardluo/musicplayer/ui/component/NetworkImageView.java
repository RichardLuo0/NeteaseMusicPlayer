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
        getImage(url, placeholder).fit().into(this);
    }

    private RequestCreator getImage(Uri uri, int placeholder) {
        return Picasso.get().load(uri).placeholder(placeholder).error(placeholder);
    }

    public void setImage(Uri uri, int placeholder) {
        getImage(uri, placeholder).fit().into(this);
    }

    private RequestCreator getImage(int id, int placeholder) {
        return Picasso.get().load(id).placeholder(placeholder).error(placeholder);
    }

    public void setImage(int id, int placeholder) {
        getImage(id, placeholder).fit().into(this);
    }
}
