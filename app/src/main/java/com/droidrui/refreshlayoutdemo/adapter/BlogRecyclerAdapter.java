package com.droidrui.refreshlayoutdemo.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.droidrui.refreshlayoutdemo.R;
import com.droidrui.refreshlayoutdemo.model.Blog;
import com.droidrui.refreshlayoutdemo.util.ResUtils;
import com.droidrui.refreshlayoutdemo.view.Toaster;

import java.util.ArrayList;

/**
 * Created by lr on 2016/7/1.
 */
public class BlogRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<Blog> mList;
    private LayoutInflater mInflater;

    public BlogRecyclerAdapter(Activity activity, ArrayList<Blog> list) {
        mList = list;
        mInflater = LayoutInflater.from(activity);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = mInflater.inflate(R.layout.item_blog, parent, false);
        return new ItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        ItemViewHolder ho = (ItemViewHolder) holder;
        Blog item = mList.get(position);
        ho.mDescTv.setText(item.desc);
        ho.mAuthorTv.setText(item.who);
        ho.mTimeTv.setText(item.createdAt);
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

        TextView mDescTv;
        TextView mAuthorTv;
        TextView mTimeTv;

        public ItemViewHolder(View itemView) {
            super(itemView);
            mDescTv = (TextView) itemView.findViewById(R.id.tv_desc);
            mAuthorTv = (TextView) itemView.findViewById(R.id.tv_author);
            mTimeTv = (TextView) itemView.findViewById(R.id.tv_time);
        }
    }

}
