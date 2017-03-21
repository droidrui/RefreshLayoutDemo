package com.droidrui.refreshlayoutdemo.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.droidrui.refreshlayoutdemo.R;
import com.droidrui.refreshlayoutdemo.model.Blog;

import java.util.ArrayList;

/**
 * Created by lr on 2016/7/1.
 */
public class BlogListAdapter2 extends BaseAdapter {

    private ArrayList<Blog> mList;
    private LayoutInflater mInflater;

    public BlogListAdapter2(Activity activity, ArrayList<Blog> list) {
        mList = list;
        mInflater = LayoutInflater.from(activity);
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
            convertView = mInflater.inflate(R.layout.item_blog, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.bind(position);
        return convertView;
    }

    private class ViewHolder {

        TextView mDescTv;
        TextView mAuthorTv;
        TextView mTimeTv;

        private ViewHolder(View itemView) {
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

}
