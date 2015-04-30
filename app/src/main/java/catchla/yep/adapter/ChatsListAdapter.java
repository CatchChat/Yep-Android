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
import catchla.yep.view.holder.ChatEntryViewHolder;

/**
 * Created by mariotaku on 15/4/29.
 */
public class ChatsListAdapter extends RecyclerView.Adapter implements ILoadMoreSupportAdapter {
    private static final int ITEM_VIEW_TYPE_CHAT_ENTRY = 1;
    private final LayoutInflater mInflater;

    private static final int[] SAMPLE_PROFILE_IMAGES = {R.drawable.ic_profile_image_lucy,
            R.drawable.ic_profile_image_jony, R.drawable.ic_profile_image_robert, R.drawable.ic_profile_image_sakura,
            R.drawable.ic_profile_image_kevin};
    private static final String[] SAMPLE_PROFILE_NAMES = {"Lucy", "Jony", "Robert", "さくら", "Kevin"};
    private static final String[] SAMPLE_PROFILE_TIMES = {"just now", "2 mins ago", "1 hour ago", "yesterday", "yesterday"};
    private static final String[] SAMPLE_PROFILE_MESSAGES = {"So we may should meet at 9",
            "Lets talk about the iOS 10 Design\nWould like to know your idea",
            "I want you teach me about coding",
            "おはよごぜいます 🐱あいしてる",
            "今晚一起去研究Xbox吧，顺便学学Xcode"};

    public ChatsListAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int position) {
        final View view = mInflater.inflate(R.layout.list_item_chat_entry, parent, false);
        return new ChatEntryViewHolder(this, view);
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
                chatEntryViewHolder.displaySample(SAMPLE_PROFILE_IMAGES[position],
                        SAMPLE_PROFILE_NAMES[position], SAMPLE_PROFILE_TIMES[position],
                        SAMPLE_PROFILE_MESSAGES[position]);
                break;
            }
        }
    }

    @Override
    public int getItemCount() {
        return SAMPLE_PROFILE_IMAGES.length;
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
}
