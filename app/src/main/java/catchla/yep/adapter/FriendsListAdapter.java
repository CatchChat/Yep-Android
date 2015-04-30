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
import catchla.yep.view.holder.FriendViewHolder;

/**
 * Created by mariotaku on 15/4/29.
 */
public class FriendsListAdapter extends RecyclerView.Adapter implements ILoadMoreSupportAdapter {
    private static final int ITEM_VIEW_TYPE_CHAT_ENTRY = 1;
    private final LayoutInflater mInflater;

    private static final int[] SAMPLE_PROFILE_IMAGES = {R.drawable.ic_profile_image_kevin,
            R.drawable.ic_profile_image_ray, R.drawable.ic_profile_image_jony, R.drawable.ic_profile_image_sakura};
    private static final String[] SAMPLE_PROFILE_NAMES = {"Kevin", "Ray", "Jony", "さくら"};
    private static final String[] SAMPLE_PROFILE_TIMES = {"100m | just now", "500m | 2 min", "1km | 1h", "1.2 | 1d"};
    private static final String[] SAMPLE_PROFILE_DESCRIPTIONS = {"Full-stack developer",
            "Designer & Developer",
            "Designer at Apple",
            "ございございございござい"};

    public FriendsListAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int position) {
        final View view = mInflater.inflate(R.layout.list_item_friend, parent, false);
        return new FriendViewHolder(this, view);
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
                chatEntryViewHolder.displaySample(SAMPLE_PROFILE_IMAGES[position],
                        SAMPLE_PROFILE_NAMES[position], SAMPLE_PROFILE_TIMES[position],
                        SAMPLE_PROFILE_DESCRIPTIONS[position]);
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
