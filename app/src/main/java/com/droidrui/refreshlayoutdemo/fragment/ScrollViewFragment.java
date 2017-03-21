package com.droidrui.refreshlayoutdemo.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.droidrui.refreshlayoutdemo.R;
import com.droidrui.refreshlayoutdemo.adapter.BannerPagerAdapter;
import com.droidrui.refreshlayoutdemo.component.TaskCallback;
import com.droidrui.refreshlayoutdemo.component.TaskError;
import com.droidrui.refreshlayoutdemo.model.Welfare;
import com.droidrui.refreshlayoutdemo.task.WelfareTask;
import com.droidrui.refreshlayoutdemo.view.RefreshLayout;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class ScrollViewFragment extends BaseFragment {

    private RefreshLayout mRefreshLayout;

    private ArrayList<Welfare> mWelfareList1;
    private BannerPagerAdapter mBannerAdapter1;

    private ArrayList<Welfare> mWelfareList2;
    private BannerPagerAdapter mBannerAdapter2;

    public ScrollViewFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_scroll_view, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        getWelfareList1();
        getWelfareList2();
    }

    private void initView() {
        mRefreshLayout = (RefreshLayout) findViewById(R.id.refresh_layout);
        mRefreshLayout.setOnRefreshListener(new RefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getWelfareList1();
            }
        });

        mRefreshLayout.setOnLoadMoreListener(new RefreshLayout.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                getWelfareList2();
            }
        });

        ViewPager viewPager1 = (ViewPager) findViewById(R.id.vp1);
        mWelfareList1 = new ArrayList<>();
        mBannerAdapter1 = new BannerPagerAdapter(mWelfareList1);
        viewPager1.setAdapter(mBannerAdapter1);

        ViewPager viewPager2 = (ViewPager) findViewById(R.id.vp2);
        mWelfareList2 = new ArrayList<>();
        mBannerAdapter2 = new BannerPagerAdapter(mWelfareList2);
        viewPager2.setAdapter(mBannerAdapter2);

        viewPager1.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                mRefreshLayout.setVeritcalScrollEnabled(1, state == ViewPager.SCROLL_STATE_IDLE);
            }
        });

        viewPager2.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                mRefreshLayout.setVeritcalScrollEnabled(2, state == ViewPager.SCROLL_STATE_IDLE);
            }
        });
    }

    private void getWelfareList1() {
        getTaskManager().start(WelfareTask.getDataList(5, 1)
                .setCallback(new TaskCallback<ArrayList<Welfare>>() {

                    @Override
                    public void onFinish() {
                        mRefreshLayout.completeRefresh();
                    }

                    @Override
                    public void onError(TaskError e) {

                    }

                    @Override
                    public void onSuccess(ArrayList<Welfare> result) {
                        mWelfareList1.clear();
                        mWelfareList1.addAll(result);
                        mBannerAdapter1.notifyDataSetChanged();
                    }
                }));
    }

    private void getWelfareList2() {
        getTaskManager().start(WelfareTask.getDataList(5, 2)
                .setCallback(new TaskCallback<ArrayList<Welfare>>() {

                    @Override
                    public void onFinish() {
                        mRefreshLayout.completeLoadMore();
                    }

                    @Override
                    public void onError(TaskError e) {

                    }

                    @Override
                    public void onSuccess(ArrayList<Welfare> result) {
                        mWelfareList2.clear();
                        mWelfareList2.addAll(result);
                        mBannerAdapter2.notifyDataSetChanged();
                    }
                }));
    }

}
