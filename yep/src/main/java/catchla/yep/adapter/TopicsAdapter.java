/*
 * Copyright (c) 2015. Catch Inc,
 */

package catchla.yep.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import catchla.yep.R;
import catchla.yep.adapter.iface.ILoadMoreSupportAdapter;
import catchla.yep.adapter.iface.ItemClickListener;
import catchla.yep.model.Topic;
import catchla.yep.view.holder.TopicViewHolder;

/**
 * Created by mariotaku on 15/4/29.
 */
public class TopicsAdapter extends BaseRecyclerViewAdapter
        implements ILoadMoreSupportAdapter {
    private static final int ITEM_VIEW_TYPE_USER_ITEM = 1;

    private final LayoutInflater mInflater;

    public void setClickListener(final ItemClickListener listener) {
        this.mClickListener = listener;
    }

    private ItemClickListener mClickListener;

    private List<Topic> mData;

    public TopicsAdapter(Context context) {
        super(context);
        mInflater = LayoutInflater.from(context);

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int position) {
        final View view = mInflater.inflate(R.layout.list_item_topic, parent, false);
        return new TopicViewHolder(view, getContext(), getImageLoader(), mClickListener);
    }

    @Override
    public int getItemViewType(int position) {
        return ITEM_VIEW_TYPE_USER_ITEM;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case ITEM_VIEW_TYPE_USER_ITEM: {
                final TopicViewHolder topicViewHolder = (TopicViewHolder) holder;
                topicViewHolder.displayTopic(mData.get(position));
                break;
            }
        }
    }

    @Override
    public int getItemCount() {
        if (mData == null) return 0;
        return mData.size();
    }

    @Override
    public boolean isLoadMoreIndicatorVisible() {
        return false;
    }

    @Override
    public void setLoadMoreIndicatorVisible(boolean enabled) {

    }

    @Override
    public boolean isLoadMoreSupported() {
        return false;
    }

    @Override
    public void setLoadMoreSupported(boolean supported) {

    }

    public void setData(final List<Topic> data) {
        mData = data;
        notifyDataSetChanged();
    }

    public Topic getTopic(final int position) {
        return mData.get(position);
    }
}
