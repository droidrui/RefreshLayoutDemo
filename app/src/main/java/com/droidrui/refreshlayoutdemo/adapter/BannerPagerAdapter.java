package com.droidrui.refreshlayoutdemo.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.droidrui.refreshlayoutdemo.R;
import com.droidrui.refreshlayoutdemo.model.Welfare;
import com.droidrui.refreshlayoutdemo.util.ImageUtils;
import com.droidrui.refreshlayoutdemo.util.ResUtils;
import com.droidrui.refreshlayoutdemo.view.Toaster;

import java.util.ArrayList;

public class BannerPagerAdapter extends PagerAdapter {

    private ArrayList<Welfare> mList;

    public BannerPagerAdapter(ArrayList<Welfare> list) {
        mList = list;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object o) {
        return view == o;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        ImageView view = new ImageView(container.getContext());
        view.setScaleType(ImageView.ScaleType.CENTER_CROP);
        ImageUtils.load(mList.get(position).url, view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toaster.show(String.format(ResUtils.getString(R.string.click_d_item), position));
            }
        });
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

}
