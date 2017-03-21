package com.droidrui.refreshlayoutdemo.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.droidrui.refreshlayoutdemo.R;
import com.droidrui.refreshlayoutdemo.adapter.BlogListAdapter2;
import com.droidrui.refreshlayoutdemo.component.TaskCallback;
import com.droidrui.refreshlayoutdemo.component.TaskError;
import com.droidrui.refreshlayoutdemo.constant.Common;
import com.droidrui.refreshlayoutdemo.model.Blog;
import com.droidrui.refreshlayoutdemo.task.BlogTask;
import com.droidrui.refreshlayoutdemo.util.ResUtils;
import com.droidrui.refreshlayoutdemo.view.RefreshLayout;
import com.droidrui.refreshlayoutdemo.view.Toaster;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class ListViewFragment extends BaseFragment {

    private RefreshLayout mRefreshLayout;

    private ArrayList<Blog> mList;
    private BlogListAdapter2 mAdapter;

    private int mPage = 1;

    public ListViewFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_list_view, container, false);
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

        mRefreshLayout.setOnLoadMoreListener(new RefreshLayout.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                getBlogList();
            }
        });

        ListView listView = (ListView) findViewById(R.id.content_view);
        mList = new ArrayList<>();
        mAdapter = new BlogListAdapter2(mActivity, mList);
        listView.setAdapter(mAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toaster.show(String.format(ResUtils.getString(R.string.click_d_item), position));
            }
        });
    }

    private void getBlogList() {
        getTaskManager().start(BlogTask.getDataList(Common.COUNT, mPage)
                .setCallback(new TaskCallback<ArrayList<Blog>>() {

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
                    public void onSuccess(ArrayList<Blog> result) {
                        if (mPage == 1) {
                            mList.clear();
                        }
                        if (result.size() >= 0) {
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
