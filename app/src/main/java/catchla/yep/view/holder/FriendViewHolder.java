/*
 * Copyright (c) 2015. Catch Inc,
 */

package catchla.yep.view.holder;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import catchla.yep.R;
import catchla.yep.activity.ChatActivity;
import catchla.yep.adapter.FriendsListAdapter;
import catchla.yep.model.User;

/**
 * Created by mariotaku on 15/4/29.
 */
public class FriendViewHolder extends RecyclerView.ViewHolder {
    private final ImageView profileImageView;
    private final TextView nameView, timeView, descriptionView;

    public FriendViewHolder(final RecyclerView.Adapter<RecyclerView.ViewHolder> adapter, View itemView) {
        super(itemView);
        profileImageView = (ImageView) itemView.findViewById(R.id.profile_image);
        nameView = (TextView) itemView.findViewById(R.id.name);
        timeView = (TextView) itemView.findViewById(R.id.time);
        descriptionView = (TextView) itemView.findViewById(R.id.description);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.getContext().startActivity(new Intent(v.getContext(), ChatActivity.class));
            }
        });
    }

    public void displaySample(int profileImage, String name, String time, String message) {
        profileImageView.setImageResource(profileImage);
        nameView.setText(name);
        timeView.setText(time);
        descriptionView.setText(message);
    }

    public void displayUser(final User user) {
        Picasso.with(profileImageView.getContext()).load(user.getAvatarUrl()).into(profileImageView);
        nameView.setText(user.getNickname());
//        timeView.setText();
        descriptionView.setText(user.getIntroduction());
    }
}
