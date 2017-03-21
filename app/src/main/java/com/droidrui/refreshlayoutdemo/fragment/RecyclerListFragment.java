package com.droidrui.refreshlayoutdemo.fragment;


import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.droidrui.refreshlayoutdemo.R;
import com.droidrui.refreshlayoutdemo.adapter.BlogRecyclerAdapter;
import com.droidrui.refreshlayoutdemo.component.TaskCallback;
import com.droidrui.refreshlayoutdemo.component.TaskError;
import com.droidrui.refreshlayoutdemo.constant.Common;
import com.droidrui.refreshlayoutdemo.model.Blog;
import com.droidrui.refreshlayoutdemo.task.BlogTask;
import com.droidrui.refreshlayoutdemo.util.DimenUtils;
import com.droidrui.refreshlayoutdemo.view.RefreshLayout;
import com.droidrui.refreshlayoutdemo.view.Toaster;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class RecyclerListFragment extends BaseFragment {

    private RefreshLayout mRefreshLayout;

    private RecyclerView mRecyclerView;
    private BlogRecyclerAdapter mAdapter;
    private ArrayList<Blog> mList;
    private LinearLayoutManager mLayoutManager;

    private int mPage = 1;

    private boolean mLoadingMore;
    private boolean mNoMore;

    public RecyclerListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_recycler_list, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        mPage = 1;
        getBlogList();
    }

    private void initView() {
        mRefreshLayout = (RefreshLayout) findViewById(R.id.refresh_layout);
        mRefreshLayout.setOnRefreshListener(new RefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPage = 1;
                getBlogList();
            }
        });

        mRecyclerView = (RecyclerView) findViewById(R.id.content_view);
        mLayoutManager = new LinearLayoutManager(mActivity);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {

            private int padding = DimenUtils.dp2px(8);

            @Override
            public void getItemOffsets(Rect outRect, int itemPosition, RecyclerView parent) {
                if (itemPosition == 0) {
                    outRect.set(0, 0, 0, 0);
                } else {
                    outRect.set(0, padding, 0, 0);
                }
            }
        });
        mList = new ArrayList<>();
        mAdapter = new BlogRecyclerAdapter(mActivity, mList);
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int position = mLayoutManager.findLastVisibleItemPosition();
                if (!mNoMore && mList.size() >= Common.COUNT && position == mList.size() && !mLoadingMore) {
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
                            mList.clear();
                        }
                        mList.addAll(result);
                        mAdapter.notifyDataSetChanged();
                        if (result.size() >= Common.COUNT) {
                            mPage++;
                        } else {
                            mNoMore = true;
                            mAdapter.setNoMore(true);
                        }
                    }
                }));
    }

}
