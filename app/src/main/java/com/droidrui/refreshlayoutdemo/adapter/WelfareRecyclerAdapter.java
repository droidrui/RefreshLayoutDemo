package com.droidrui.refreshlayoutdemo.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.droidrui.refreshlayoutdemo.R;
import com.droidrui.refreshlayoutdemo.model.Welfare;
import com.droidrui.refreshlayoutdemo.util.ImageUtils;
import com.droidrui.refreshlayoutdemo.util.ResUtils;
import com.droidrui.refreshlayoutdemo.view.Toaster;

import java.util.ArrayList;

/**
 * Created by lr on 2016/6/30.
 */
public class WelfareRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<Welfare> mList;
    private LayoutInflater mInflater;

    private int mItemHeight;
    private ViewGroup.LayoutParams mItemLayoutParams;

    public WelfareRecyclerAdapter(Activity activity, ArrayList<Welfare> list) {
        mList = list;
        mInflater = LayoutInflater.from(activity);
        mItemLayoutParams = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = mInflater.inflate(R.layout.item_welfare, parent, false);
        return new ItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        ItemViewHolder ho = (ItemViewHolder) holder;
        if (ho.itemView.getLayoutParams().height != mItemHeight) {
            ho.itemView.setLayoutParams(mItemLayoutParams);
        }
        Welfare item = mList.get(position);
        ImageUtils.load(item.url, ho.mIv, mItemHeight);
        ho.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toaster.show(String.format(ResUtils.getString(R.string.click_d_item), position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    private class ItemViewHolder extends RecyclerView.ViewHolder {

        private ImageView mIv;

        private ItemViewHolder(View itemView) {
            super(itemView);
            mIv = (ImageView) itemView.findViewById(R.id.iv);
        }
    }

    public void setItemHeight(int height) {
        if (mItemHeight == height) {
            return;
        }
        mItemHeight = height;
        mItemLayoutParams = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, mItemHeight);
        notifyDataSetChanged();
    }

}
