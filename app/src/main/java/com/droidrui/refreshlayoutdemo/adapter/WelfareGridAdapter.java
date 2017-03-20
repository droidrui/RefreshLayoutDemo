package com.droidrui.refreshlayoutdemo.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.droidrui.refreshlayoutdemo.App;
import com.droidrui.refreshlayoutdemo.R;
import com.droidrui.refreshlayoutdemo.model.Welfare;
import com.droidrui.refreshlayoutdemo.util.ImageUtils;
import com.droidrui.refreshlayoutdemo.util.ResUtils;
import com.droidrui.refreshlayoutdemo.view.Toaster;

import java.util.ArrayList;

/**
 * Created by lr on 2016/6/30.
 */
public class WelfareGridAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<Welfare> mList;
    private LayoutInflater mInflater;

    private int mItemHeight;
    private ViewGroup.LayoutParams mItemLayoutParams;

    private boolean mNoMore;

    public WelfareGridAdapter(ArrayList<Welfare> list) {
        mList = list;
        mInflater = LayoutInflater.from(App.getContext());
        mItemLayoutParams = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 0) {
            View v = mInflater.inflate(R.layout.item_welfare, parent, false);
            return new ItemViewHolder(v);
        } else {
            View v = mInflater.inflate(R.layout.item_load_more, parent, false);
            return new LoadMoreViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (getItemViewType(position) == 0) {
            ItemViewHolder ho = (ItemViewHolder) holder;
            if (ho.itemView.getLayoutParams().height != mItemHeight) {
                ho.itemView.setLayoutParams(mItemLayoutParams);
            }
            Welfare item = mList.get(position);
            ImageUtils.load(item.url, ho.mIv, mItemHeight);
            ho.mIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toaster.show(String.format(ResUtils.getString(R.string.click_d_item), position));
                }
            });
        } else {
            LoadMoreViewHolder ho = (LoadMoreViewHolder) holder;
            if (mNoMore) {
                ho.mProgressBar.setVisibility(View.GONE);
                ho.mTv.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mList.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == mList.size()) {
            return 1;
        }
        return 0;
    }

    private class ItemViewHolder extends RecyclerView.ViewHolder {

        ImageView mIv;

        public ItemViewHolder(View itemView) {
            super(itemView);
            mIv = (ImageView) itemView.findViewById(R.id.iv);
        }
    }

    private class LoadMoreViewHolder extends RecyclerView.ViewHolder {

        ProgressBar mProgressBar;
        TextView mTv;

        public LoadMoreViewHolder(View itemView) {
            super(itemView);
            mProgressBar = (ProgressBar) itemView.findViewById(R.id.progress_bar);
            mTv = (TextView) itemView.findViewById(R.id.tv);
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

    public void setNoMore(boolean noMore) {
        mNoMore = noMore;
        notifyItemChanged(mList.size());
    }

}
