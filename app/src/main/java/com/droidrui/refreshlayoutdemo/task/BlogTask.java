package com.droidrui.refreshlayoutdemo.task;

import com.alibaba.fastjson.JSON;
import com.droidrui.refreshlayoutdemo.R;
import com.droidrui.refreshlayoutdemo.component.HttpResponse;
import com.droidrui.refreshlayoutdemo.component.OkHttpHelper;
import com.droidrui.refreshlayoutdemo.component.Task;
import com.droidrui.refreshlayoutdemo.constant.API;
import com.droidrui.refreshlayoutdemo.model.Blog;
import com.droidrui.refreshlayoutdemo.util.ResUtils;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by lr on 2016/6/30.
 */
public class BlogTask {

    public static Task<ArrayList<Blog>> getDataList(final int count, final int page) {
        return new Task<ArrayList<Blog>>() {
            @Override
            protected void call() {
                try {
                    String method = API.BLOG + "/" + count + "/" + page;
                    HttpResponse response = OkHttpHelper.getInstance().get(method);
                    if (response.error != null) {
                        onError(response.error);
                        return;
                    }
                    ArrayList<Blog> dataList = (ArrayList<Blog>) JSON.parseArray(response.result, Blog.class);
                    if (dataList != null) {
                        onSuccess(dataList);
                    } else {
                        onError(ResUtils.getString(R.string.data_error));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    onError(e);
                }
            }
        };
    }


}
