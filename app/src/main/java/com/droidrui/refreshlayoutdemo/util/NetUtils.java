package com.droidrui.refreshlayoutdemo.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.droidrui.refreshlayoutdemo.App;

public class NetUtils {

    public static boolean isNetworkAvailable() {
        ConnectivityManager connectivity = (ConnectivityManager) App.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (info != null && info.isConnected()) {
                return true;
            }
        }
        return false;
    }

}
