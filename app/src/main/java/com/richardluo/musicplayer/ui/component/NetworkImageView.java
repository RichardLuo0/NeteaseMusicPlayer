package com.richardluo.musicplayer.ui.component;

import android.content.Context;
import android.net.Uri;
import android.util.AttributeSet;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;

import com.google.android.material.imageview.ShapeableImageView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

public class NetworkImageView extends ShapeableImageView {
    public NetworkImageView(Context context) {
        super(context);
        setScaleType(ScaleType.CENTER_CROP);
    }

    public NetworkImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setScaleType(ScaleType.CENTER_CROP);
    }

    public NetworkImageView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setScaleType(ScaleType.CENTER_CROP);
    }

    private RequestCreator getImage(String url, int placeholder) {
        return Picasso.get().load(url).placeholder(placeholder).error(placeholder);
    }

    public void setImage(String url, int placeholder) {
        getImage(url, placeholder).fit().centerCrop().into(this);
    }

    private RequestCreator getImage(Uri uri, int placeholder) {
        return Picasso.get().load(uri).placeholder(placeholder).error(placeholder);
    }

    public void setImage(Uri uri, int placeholder) {
        getImage(uri, placeholder).fit().centerCrop().into(this);
    }

    private RequestCreator getImage(int id, int placeholder) {
        return Picasso.get().load(id).placeholder(placeholder).error(placeholder);
    }

    public void setImage(int id, int placeholder) {
        getImage(id, placeholder).fit().centerCrop().into(this);
    }

    public void setImage(@DrawableRes int id) {
        setImageResource(id);
    }
}
