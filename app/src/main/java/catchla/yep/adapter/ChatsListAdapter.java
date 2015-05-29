/*
 * Copyright (c) 2015. Catch Inc,
 */

package catchla.yep.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import catchla.yep.R;
import catchla.yep.adapter.iface.ILoadMoreSupportAdapter;
import catchla.yep.adapter.iface.ItemClickListener;
import catchla.yep.model.Conversation;
import catchla.yep.view.holder.ChatEntryViewHolder;
import io.realm.RealmResults;

/**
 * Created by mariotaku on 15/4/29.
 */
public class ChatsListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements ILoadMoreSupportAdapter {
    private static final int ITEM_VIEW_TYPE_CHAT_ENTRY = 1;
    private final LayoutInflater mInflater;

    private RealmResults<Conversation> mData;

    public void setItemClickListener(final ItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    private ItemClickListener mItemClickListener;

    public ChatsListAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int position) {
        final View view = mInflater.inflate(R.layout.list_item_chat_entry, parent, false);
        return new ChatEntryViewHolder(mItemClickListener, view);
    }

    @Override
    public int getItemViewType(int position) {
        return ITEM_VIEW_TYPE_CHAT_ENTRY;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case ITEM_VIEW_TYPE_CHAT_ENTRY: {
                ChatEntryViewHolder chatEntryViewHolder = (ChatEntryViewHolder) holder;
                chatEntryViewHolder.displayConversation(mData.get(position));
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

    public void setData(final RealmResults<Conversation> data) {
        mData = data;
    }

    public Conversation getConversation(final int position) {
        return mData.get(position);
    }
}
