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
import catchla.yep.adapter.iface.ItemClickListener;
import catchla.yep.model.User;
import catchla.yep.view.holder.FriendViewHolder;

/**
 * Created by mariotaku on 15/4/29.
 */
public class UsersAdapter extends LoadMoreSupportAdapter {
    private static final int ITEM_VIEW_TYPE_USER_ITEM = 1;

    private final LayoutInflater mInflater;

    public void setClickListener(final ItemClickListener listener) {
        this.mClickListener = listener;
    }

    private ItemClickListener mClickListener;

    private List<User> mData;

    public UsersAdapter(Context context) {
        super(context);
        mInflater = LayoutInflater.from(context);

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int position) {
        final View view = mInflater.inflate(R.layout.list_item_friend, parent, false);
        return new FriendViewHolder(view, this, mClickListener);
    }

    @Override
    public int getItemViewType(int position) {
        return ITEM_VIEW_TYPE_USER_ITEM;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case ITEM_VIEW_TYPE_USER_ITEM: {
                final FriendViewHolder chatEntryViewHolder = (FriendViewHolder) holder;
                chatEntryViewHolder.displayUser(mData.get(position));
                break;
            }
        }
    }

    @Override
    public int getItemCount() {
        if (mData == null) return 0;
        return mData.size();
    }

    public void setData(final List<User> data) {
        mData = data;
        notifyDataSetChanged();
    }

    public User getUser(final int position) {
        return mData.get(position);
    }
}
