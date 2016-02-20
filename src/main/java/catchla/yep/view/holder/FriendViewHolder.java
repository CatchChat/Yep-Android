/*
 * Copyright (c) 2015. Catch Inc,
 */

package catchla.yep.view.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import catchla.yep.R;
import catchla.yep.adapter.BaseRecyclerViewAdapter;
import catchla.yep.adapter.iface.ItemClickListener;
import catchla.yep.model.Friendship;
import catchla.yep.model.User;
import catchla.yep.util.ImageLoaderWrapper;

/**
 * Created by mariotaku on 15/4/29.
 */
public class FriendViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private final BaseRecyclerViewAdapter adapter;
    private final ItemClickListener listener;

    private final ImageView profileImageView;
    private final TextView nameView, timeView, descriptionView;

    public FriendViewHolder(View itemView, final BaseRecyclerViewAdapter adapter, final ItemClickListener listener) {
        super(itemView);
        this.adapter = adapter;
        this.listener = listener;
        profileImageView = (ImageView) itemView.findViewById(R.id.profile_image);
        nameView = (TextView) itemView.findViewById(R.id.name);
        timeView = (TextView) itemView.findViewById(R.id.update_time);
        descriptionView = (TextView) itemView.findViewById(R.id.description);
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (listener == null) return;
        listener.onItemClick(getAdapterPosition(), FriendViewHolder.this);
    }

    public void displaySample(int profileImage, String name, String time, String message) {
        profileImageView.setImageResource(profileImage);
        nameView.setText(name);
        timeView.setText(time);
        descriptionView.setText(message);
    }

    public void displayUser(final User user) {
        final ImageLoaderWrapper imageLoader = adapter.getImageLoader();
        imageLoader.displayProfileImage(user.getAvatarThumbUrl(), profileImageView);
        nameView.setText(user.getNickname());
//        timeView.setText();
        descriptionView.setText(user.getIntroduction());
    }

    public void displayFriendship(final Friendship friendship) {
        displayUser(friendship.getFriend());
    }
}
