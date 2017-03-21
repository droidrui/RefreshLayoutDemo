package com.droidrui.refreshlayoutdemo.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.droidrui.refreshlayoutdemo.R;
import com.droidrui.refreshlayoutdemo.model.Blog;

import java.util.ArrayList;

/**
 * Created by lr on 2016/7/1.
 */
public class BlogListAdapter extends BaseAdapter {

    private static final int ITEM_TYPE_BLOG = 0;
    private static final int ITEM_TYPE_LOAD_MORE = 1;

    private ArrayList<Blog> mList;
    private LayoutInflater mInflater;

    private boolean mNoMore;

    public BlogListAdapter(Activity activity, ArrayList<Blog> list) {
        mList = list;
        mInflater = LayoutInflater.from(activity);
    }

    @Override
    public int getCount() {
        return mList.size() + 1;
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
        int type = getItemViewType(position);
        if (type == ITEM_TYPE_BLOG) {
            return getBlogItemView(position, convertView, parent);
        } else {
            return getLoadMoreItemView(position, convertView, parent);
        }
    }

    private View getBlogItemView(int position, View convertView, ViewGroup parent) {
        ItemViewHolder holder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_blog, parent, false);
            holder = new ItemViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ItemViewHolder) convertView.getTag();
        }
        holder.bind(position);
        return convertView;
    }

    private View getLoadMoreItemView(int position, View convertView, ViewGroup parent) {
        LoadMoreViewHolder holder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_load_more, parent, false);
            holder = new LoadMoreViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (LoadMoreViewHolder) convertView.getTag();
        }
        holder.bind(position);
        return convertView;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == mList.size()) {
            return ITEM_TYPE_LOAD_MORE;
        }
        return ITEM_TYPE_BLOG;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    private class ItemViewHolder {

        TextView mDescTv;
        TextView mAuthorTv;
        TextView mTimeTv;

        private ItemViewHolder(View itemView) {
            mDescTv = (TextView) itemView.findViewById(R.id.tv_desc);
            mAuthorTv = (TextView) itemView.findViewById(R.id.tv_author);
            mTimeTv = (TextView) itemView.findViewById(R.id.tv_time);
        }

        private void bind(int position) {
            Blog item = mList.get(position);
            mDescTv.setText(item.desc);
            mAuthorTv.setText(item.who);
            mTimeTv.setText(item.createdAt);
        }
    }

    private class LoadMoreViewHolder {

        ProgressBar mProgressBar;
        TextView mTv;

        private LoadMoreViewHolder(View itemView) {
            mProgressBar = (ProgressBar) itemView.findViewById(R.id.progress_bar);
            mTv = (TextView) itemView.findViewById(R.id.tv);
        }

        private void bind(int position) {
            if (mNoMore) {
                mProgressBar.setVisibility(View.GONE);
                mTv.setVisibility(View.VISIBLE);
            }
        }
    }

    public void setNoMore(boolean noMore) {
        mNoMore = noMore;
        notifyDataSetChanged();
    }

}
