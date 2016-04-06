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
import catchla.yep.adapter.iface.ItemClickListener;
import catchla.yep.model.Skill;
import catchla.yep.view.holder.FriendGridViewHolder;

/**
 * Created by mariotaku on 15/4/29.
 */
public class UsersGridAdapter extends UsersAdapter {

    private final LayoutInflater mInflater;

    public UsersGridAdapter(Context context) {
        super(context);
        mInflater = LayoutInflater.from(context);

    }

    @Override
    protected RecyclerView.ViewHolder onCreateFriendViewHolder(final ViewGroup parent) {
        final View view = mInflater.inflate(R.layout.grid_item_friend, parent, false);
        return new FriendGridViewHolder(view, this, getClickListener());
    }

    @Override
    protected UserGridItemClickListener getClickListener() {
        return (UserGridItemClickListener) super.getClickListener();
    }

    @Override
    public void setItemClickListener(final ItemClickListener listener) {
        super.setItemClickListener(listener);
    }

    @Override
    protected void bindFriendViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        ((FriendGridViewHolder) holder).displayUser(getUser(position));
    }

    public interface UserGridItemClickListener extends ItemClickListener {

        void onSkillClick(int position, Skill skill, FriendGridViewHolder holder);
    }

}
