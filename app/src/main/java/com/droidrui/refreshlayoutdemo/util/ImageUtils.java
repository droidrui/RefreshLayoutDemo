package com.droidrui.refreshlayoutdemo.util;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.droidrui.refreshlayoutdemo.App;

/**
 * Created by Administrator on 2016/10/17.
 */

public class ImageUtils {

    public static void load(String url, ImageView view) {
        Glide.with(App.getContext())
                .load(url)
                .into(view);
    }

    public static void load(String url, ImageView view, int size) {
        Glide.with(App.getContext())
                .load(url)
                .centerCrop()
                .override(size, size)
                .into(view);
    }

}
