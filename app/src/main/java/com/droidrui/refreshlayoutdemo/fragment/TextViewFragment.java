package com.droidrui.refreshlayoutdemo.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.droidrui.refreshlayoutdemo.R;
import com.droidrui.refreshlayoutdemo.view.RefreshLayout;

/**
 * A simple {@link Fragment} subclass.
 */
public class TextViewFragment extends BaseFragment {

    private RefreshLayout mRefreshLayout;
    private TextView mTextView;

    public TextViewFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_text_view, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
    }

    private void initView() {
        mRefreshLayout = (RefreshLayout) findViewById(R.id.refresh_layout);
        mRefreshLayout.setOnRefreshListener(new RefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mTextView.setText("刷新中");
                mRefreshLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mRefreshLayout.completeRefresh();
                        mTextView.setText("刷新完成");
                    }
                }, 2000);
            }
        });

        mRefreshLayout.setOnLoadMoreListener(new RefreshLayout.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                mTextView.setText("加载中");
                mRefreshLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mRefreshLayout.completeLoadMore();
                        mTextView.setText("加载完成");
                    }
                }, 2000);
            }
        });

        mTextView = (TextView) findViewById(R.id.content_view);
    }
}
