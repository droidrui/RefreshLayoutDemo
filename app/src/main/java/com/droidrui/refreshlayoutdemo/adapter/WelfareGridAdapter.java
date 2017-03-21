package com.droidrui.refreshlayoutdemo.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.droidrui.refreshlayoutdemo.R;
import com.droidrui.refreshlayoutdemo.model.Welfare;
import com.droidrui.refreshlayoutdemo.util.ImageUtils;

import java.util.ArrayList;

/**
 * Created by lr on 2016/6/30.
 */
public class WelfareGridAdapter extends BaseAdapter {

    private ArrayList<Welfare> mList;
    private LayoutInflater mInflater;

    private int mItemHeight;
    private RelativeLayout.LayoutParams mItemLayoutParams;

    public WelfareGridAdapter(Activity activity, ArrayList<Welfare> list) {
        mList = list;
        mInflater = LayoutInflater.from(activity);
        mItemLayoutParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_welfare, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.bind(position);
        return convertView;
    }

    private class ViewHolder {

        ImageView mIv;

        private ViewHolder(View itemView) {
            mIv = (ImageView) itemView.findViewById(R.id.iv);
        }

        private void bind(final int position) {
            if (mIv.getLayoutParams().height != mItemHeight) {
                mIv.setLayoutParams(mItemLayoutParams);
            }
            Welfare item = mList.get(position);
            ImageUtils.load(item.url, mIv, mItemHeight);
        }
    }

    public void setItemHeight(int height) {
        if (mItemHeight == height) {
            return;
        }
        mItemHeight = height;
        mItemLayoutParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, mItemHeight);
        notifyDataSetChanged();
    }


}
