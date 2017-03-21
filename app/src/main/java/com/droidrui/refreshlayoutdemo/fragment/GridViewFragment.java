package com.droidrui.refreshlayoutdemo.fragment;


import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.GridView;

import com.droidrui.refreshlayoutdemo.R;
import com.droidrui.refreshlayoutdemo.adapter.WelfareGridAdapter;
import com.droidrui.refreshlayoutdemo.component.TaskCallback;
import com.droidrui.refreshlayoutdemo.component.TaskError;
import com.droidrui.refreshlayoutdemo.constant.Common;
import com.droidrui.refreshlayoutdemo.model.Welfare;
import com.droidrui.refreshlayoutdemo.task.WelfareTask;
import com.droidrui.refreshlayoutdemo.util.DimenUtils;
import com.droidrui.refreshlayoutdemo.util.ResUtils;
import com.droidrui.refreshlayoutdemo.view.RefreshLayout;
import com.droidrui.refreshlayoutdemo.view.Toaster;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class GridViewFragment extends BaseFragment {

    private RefreshLayout mRefreshLayout;
    private ArrayList<Welfare> mList;
    private WelfareGridAdapter mAdapter;
    private GridView mGridView;

    private int mPage = 1;

    public GridViewFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_grid_view, container, false);
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

        mGridView = (GridView) findViewById(R.id.content_view);
        mList = new ArrayList<>();
        mAdapter = new WelfareGridAdapter(mActivity, mList);
        mGridView.setAdapter(mAdapter);

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toaster.show(String.format(ResUtils.getString(R.string.click_d_item), position));
            }
        });

        mGridView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int columnHeight = (mGridView.getWidth() - DimenUtils.dp2px(2)) / 2;
                mAdapter.setItemHeight(columnHeight);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    mGridView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    mGridView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
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
