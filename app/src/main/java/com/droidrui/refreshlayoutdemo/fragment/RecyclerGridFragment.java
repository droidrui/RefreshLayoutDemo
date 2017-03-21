package com.droidrui.refreshlayoutdemo.fragment;


import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.droidrui.refreshlayoutdemo.R;
import com.droidrui.refreshlayoutdemo.adapter.WelfareRecyclerAdapter;
import com.droidrui.refreshlayoutdemo.component.TaskCallback;
import com.droidrui.refreshlayoutdemo.component.TaskError;
import com.droidrui.refreshlayoutdemo.constant.Common;
import com.droidrui.refreshlayoutdemo.model.Welfare;
import com.droidrui.refreshlayoutdemo.task.WelfareTask;
import com.droidrui.refreshlayoutdemo.util.DimenUtils;
import com.droidrui.refreshlayoutdemo.view.RefreshLayout;
import com.droidrui.refreshlayoutdemo.view.Toaster;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class RecyclerGridFragment extends BaseFragment {

    private RefreshLayout mRefreshLayout;

    private RecyclerView mRecyclerView;
    private ArrayList<Welfare> mList;
    private WelfareRecyclerAdapter mAdapter;
    private GridLayoutManager mLayoutManager;

    private int mPage = 1;

    public RecyclerGridFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_recycler_grid, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        mPage = 1;
        getWelfareList();
    }

    private void initView() {
        mRefreshLayout = (RefreshLayout) findViewById(R.id.refresh_layout);
        mRefreshLayout.setOnRefreshListener(new RefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPage = 1;
                getWelfareList();
            }
        });

        mRefreshLayout.setOnLoadMoreListener(new RefreshLayout.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                getWelfareList();
            }
        });

        mRecyclerView = (RecyclerView) findViewById(R.id.content_view);
        mLayoutManager = new GridLayoutManager(mActivity, 2);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {

            private int paddingHalf = DimenUtils.dp2px(1);
            private int padding = DimenUtils.dp2px(2);

            @Override
            public void getItemOffsets(Rect outRect, int itemPosition, RecyclerView parent) {
                if (itemPosition == 0) {
                    outRect.set(0, 0, paddingHalf, 0);
                } else if (itemPosition == 1) {
                    outRect.set(paddingHalf, 0, 0, 0);
                } else if (itemPosition % 2 == 0) {
                    outRect.set(0, padding, paddingHalf, 0);
                } else {
                    outRect.set(paddingHalf, padding, 0, 0);
                }
            }
        });

        mList = new ArrayList<>();
        mAdapter = new WelfareRecyclerAdapter(mActivity, mList);
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int columnHeight = (mRecyclerView.getWidth() - DimenUtils.dp2px(2)) / 2;
                mAdapter.setItemHeight(columnHeight);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    mRecyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    mRecyclerView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
            }
        });
    }

    private void getWelfareList() {
        getTaskManager().start(WelfareTask.getDataList(Common.COUNT, mPage)
                .setCallback(new TaskCallback<ArrayList<Welfare>>() {

                    @Override
                    public void onFinish() {
                        mRefreshLayout.completeRefresh();
                        mRefreshLayout.completeLoadMore();
                    }

                    @Override
                    public void onError(TaskError e) {
                        Toaster.show(e.msg);
                    }

                    @Override
                    public void onSuccess(ArrayList<Welfare> result) {
                        if (mPage == 1) {
                            mList.clear();
                        }
                        if (result.size() > 0) {
                            mList.addAll(result);
                            mAdapter.notifyDataSetChanged();
                            mPage++;
                        } else {
                            Toaster.show(R.string.no_more);
                        }
                    }
                }));
    }
}
