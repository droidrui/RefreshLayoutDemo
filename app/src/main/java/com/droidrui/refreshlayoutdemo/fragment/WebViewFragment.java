package com.droidrui.refreshlayoutdemo.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.droidrui.refreshlayoutdemo.R;
import com.droidrui.refreshlayoutdemo.view.RefreshLayout;

/**
 * A simple {@link Fragment} subclass.
 */
public class WebViewFragment extends BaseFragment {


    public WebViewFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_web_view, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
    }

    private void initView() {
        final WebView webView = (WebView) findViewById(R.id.content_view);
        webView.loadUrl("https://github.com/");
        final RefreshLayout refreshLayout = (RefreshLayout) findViewById(R.id.refresh_layout);
        refreshLayout.setOnRefreshListener(new RefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                webView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshLayout.completeRefresh();
                        webView.reload();
                    }
                }, 2000);
            }
        });
    }
}
