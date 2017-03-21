package com.droidrui.refreshlayoutdemo.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.LayoutParams;
import android.widget.ListView;

import com.droidrui.refreshlayoutdemo.R;
import com.droidrui.refreshlayoutdemo.adapter.BannerPagerAdapter;
import com.droidrui.refreshlayoutdemo.adapter.BlogListAdapter;
import com.droidrui.refreshlayoutdemo.component.TaskCallback;
import com.droidrui.refreshlayoutdemo.component.TaskError;
import com.droidrui.refreshlayoutdemo.constant.Common;
import com.droidrui.refreshlayoutdemo.model.Blog;
import com.droidrui.refreshlayoutdemo.model.Welfare;
import com.droidrui.refreshlayoutdemo.task.BlogTask;
import com.droidrui.refreshlayoutdemo.task.WelfareTask;
import com.droidrui.refreshlayoutdemo.util.DimenUtils;
import com.droidrui.refreshlayoutdemo.view.RefreshLayout;
import com.droidrui.refreshlayoutdemo.view.Toaster;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class ViewPagerFragment extends BaseFragment {

    private RefreshLayout mRefreshLayout;

    private ArrayList<Blog> mBlogList;
    private BlogListAdapter mBlogAdapter;

    private ArrayList<Welfare> mWelfareList;
    private BannerPagerAdapter mBannerAdapter;

    private int mPage = 1;

    private boolean mLoadingMore;
    private boolean mNoMore;

    public ViewPagerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_view_pager, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        mPage = 1;
        getWelfareList();
        getBlogList();
    }

    private void initView() {
        mRefreshLayout = (RefreshLayout) findViewById(R.id.refresh_layout);
        mRefreshLayout.setOnRefreshListener(new RefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPage = 1;
                getWelfareList();
                getBlogList();
            }
        });

        ViewPager viewPager = new ViewPager(mActivity);
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, DimenUtils.dp2px(200));
        viewPager.setLayoutParams(layoutParams);
        mWelfareList = new ArrayList<>();
        mBannerAdapter = new BannerPagerAdapter(mWelfareList);
        viewPager.setAdapter(mBannerAdapter);

        ListView listView = (ListView) findViewById(R.id.content_view);
        listView.addHeaderView(viewPager);

        mBlogList = new ArrayList<>();
        mBlogAdapter = new BlogListAdapter(mActivity, mBlogList);
        listView.setAdapter(mBlogAdapter);

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                int position = firstVisibleItem + visibleItemCount;
                if (!mNoMore && mBlogList.size() >= Common.COUNT && position == mBlogList.size() && !mLoadingMore) {
                    mLoadingMore = true;
                    getBlogList();
                }
            }
        });
    }

    private void getBlogList() {
        getTaskManager().start(BlogTask.getDataList(Common.COUNT, mPage)
                .setCallback(new TaskCallback<ArrayList<Blog>>() {

                    @Override
                    public void onFinish() {
                        mLoadingMore = false;
                        mRefreshLayout.completeRefresh();
                    }

                    @Override
                    public void onError(TaskError e) {
                        Toaster.show(e.msg);
                    }

                    @Override
                    public void onSuccess(ArrayList<Blog> result) {
                        if (mPage == 1) {
                            mBlogList.clear();
                        }
                        mBlogList.addAll(result);
                        mBlogAdapter.notifyDataSetChanged();
                        if (result.size() >= Common.COUNT) {
                            mPage++;
                        } else {
                            mNoMore = true;
                            mBlogAdapter.setNoMore(true);
                        }
                    }
                }));
    }

    private void getWelfareList() {
        getTaskManager().start(WelfareTask.getDataList(5, 1)
                .setCallback(new TaskCallback<ArrayList<Welfare>>() {

                    @Override
                    public void onError(TaskError e) {

                    }

                    @Override
                    public void onSuccess(ArrayList<Welfare> result) {
                        mWelfareList.clear();
                        mWelfareList.addAll(result);
                        mBannerAdapter.notifyDataSetChanged();
                    }
                }));
    }
}
