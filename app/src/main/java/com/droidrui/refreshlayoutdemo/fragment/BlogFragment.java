package com.droidrui.refreshlayoutdemo.fragment;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.droidrui.refreshlayoutdemo.R;
import com.droidrui.refreshlayoutdemo.adapter.BlogListAdapter;
import com.droidrui.refreshlayoutdemo.component.TaskCallback;
import com.droidrui.refreshlayoutdemo.component.TaskError;
import com.droidrui.refreshlayoutdemo.constant.Common;
import com.droidrui.refreshlayoutdemo.constant.KEY;
import com.droidrui.refreshlayoutdemo.model.Blog;
import com.droidrui.refreshlayoutdemo.task.BlogTask;
import com.droidrui.refreshlayoutdemo.view.Toaster;

import java.util.ArrayList;


/**
 * Created by Administrator on 2016/11/16.
 */

public class BlogFragment extends BaseFragment {

    private String mCategory;

    private RecyclerView mRecyclerView;
    private BlogListAdapter mAdapter;
    private ArrayList<Blog> mList;
    private LinearLayoutManager mLayoutManager;

    private int mPage = 1;

    private boolean mLoadingMore;
    private boolean mNoMore;

    public BlogFragment() {
    }

    public static BlogFragment newInstance(String category) {
        BlogFragment fragment = new BlogFragment();
        Bundle args = new Bundle();
        args.putString(KEY.CATEGORY, category);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_blog, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mCategory = getArguments().getString(KEY.CATEGORY);

        initView();
        getDataList();
    }

    private void initView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mLayoutManager = new LinearLayoutManager(mActivity);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {

            @Override
            public void getItemOffsets(Rect outRect, int itemPosition, RecyclerView parent) {
                if (itemPosition == 0) {
                    outRect.set(0, 0, 0, 0);
                } else {
                    outRect.set(0, 16, 0, 0);
                }
            }
        });
        mList = new ArrayList<>();
        mAdapter = new BlogListAdapter(mActivity, mList);
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int position = mLayoutManager.findLastVisibleItemPosition();
                if (!mNoMore && mList.size() >= Common.COUNT && position == mList.size() && !mLoadingMore) {
                    mLoadingMore = true;
                    getDataList();
                }
            }
        });
    }

    private void getDataList() {
        getTaskManager().start(BlogTask.getDataList(mCategory, Common.COUNT, mPage)
                .setCallback(new TaskCallback<ArrayList<Blog>>() {

                    @Override
                    public void onFinish() {
                        mLoadingMore = false;
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
