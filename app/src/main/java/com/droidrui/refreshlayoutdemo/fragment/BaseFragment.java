package com.droidrui.refreshlayoutdemo.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;

import com.droidrui.refreshlayoutdemo.activity.BaseActivity;
import com.droidrui.refreshlayoutdemo.component.TaskManager;

/**
 * Created by Administrator on 2017/3/20.
 */

public class BaseFragment extends Fragment {

    protected BaseActivity mActivity;
    protected View mContentView;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (BaseActivity) context;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mContentView = view;
    }

    protected View findViewById(@IdRes int id) {
        return mContentView.findViewById(id);
    }

    public TaskManager getTaskManager() {
        return mActivity.getTaskManager();
    }


}
