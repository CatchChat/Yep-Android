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
import catchla.yep.model.Friendship;
import catchla.yep.view.holder.FriendViewHolder;

/**
 * Created by mariotaku on 15/4/29.
 */
public class FriendsListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements ILoadMoreSupportAdapter {
    private static final int ITEM_VIEW_TYPE_CHAT_ENTRY = 1;
    private final LayoutInflater mInflater;

    private List<Friendship> mData;

    public FriendsListAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int position) {
        final View view = mInflater.inflate(R.layout.list_item_friend, parent, false);
        return new FriendViewHolder(null, view);
    }

    @Override
    public int getItemViewType(int position) {
        return ITEM_VIEW_TYPE_CHAT_ENTRY;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case ITEM_VIEW_TYPE_CHAT_ENTRY: {
                final FriendViewHolder chatEntryViewHolder = (FriendViewHolder) holder;
                chatEntryViewHolder.displayFriendship(mData.get(position));
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

    public void setData(final List<Friendship> data) {
        mData = data;
        notifyDataSetChanged();
    }
}
