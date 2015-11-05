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
import catchla.yep.model.Conversation;
import catchla.yep.model.Message;
import catchla.yep.util.ImageLoaderWrapper;
import catchla.yep.view.holder.ChatEntryViewHolder;

/**
 * Created by mariotaku on 15/4/29.
 */
public class ChatsListAdapter extends LoadMoreSupportAdapter {

    public static final int ITEM_VIEW_TYPE_CHAT_ENTRY = 1;
    public static final int ITEM_VIEW_TYPE_CIRCLES_ENTRY = 2;

    private final LayoutInflater mInflater;
    private List<Conversation> mData;
    private ItemClickListener mItemClickListener;
    private int mCircleLength;

    public ChatsListAdapter(Context context) {
        super(context);
        mInflater = LayoutInflater.from(context);
    }

    public void setItemClickListener(final ItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = mInflater.inflate(R.layout.list_item_chat_entry, parent, false);
        return new ChatEntryViewHolder(view, this, mItemClickListener);
    }

    @Override
    public int getItemViewType(int position) {
        if (mCircleLength > 0 && position == 0) return ITEM_VIEW_TYPE_CIRCLES_ENTRY;
        return ITEM_VIEW_TYPE_CHAT_ENTRY;

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case ITEM_VIEW_TYPE_CHAT_ENTRY: {
                ChatEntryViewHolder chatEntryViewHolder = (ChatEntryViewHolder) holder;
                chatEntryViewHolder.displayConversation(getConversation(position));
                break;
            }
            case ITEM_VIEW_TYPE_CIRCLES_ENTRY: {
                ChatEntryViewHolder chatEntryViewHolder = (ChatEntryViewHolder) holder;
                chatEntryViewHolder.displayCirclesEntry(getConversation(0));
                break;
            }
        }
    }

    @Override
    public int getItemCount() {
        if (mData == null) return 0;
        if (mCircleLength == 0) return mData.size();
        return mData.size() - mCircleLength + 1;
    }

    public void setData(final List<Conversation> data, final boolean circlesOnly) {
        mData = data;
        mCircleLength = 0;
        if (data != null && !circlesOnly) {
            for (int i = 0, dataSize = data.size(); i < dataSize; i++) {
                final Conversation conversation = data.get(i);
                if (Message.RecipientType.CIRCLE.equals(conversation.getRecipientType())) {
                    mCircleLength = i + 1;
                } else {
                    break;
                }
            }
        }
        notifyDataSetChanged();
    }

    public Conversation getConversation(final int position) {
        if (mCircleLength == 0) return getRawConversation(position);
        if (position == 0) return getRawConversation(0);
        return getRawConversation(position + mCircleLength - 1);
    }

    public Conversation getRawConversation(final int position) {
        return mData.get(position);
    }

    public ImageLoaderWrapper getImageLoader() {
        return mImageLoader;
    }
}
