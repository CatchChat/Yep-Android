/*
 * Copyright (c) 2015. Catch Inc,
 */

package catchla.yep.view.holder;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.apmem.tools.layouts.FlowLayout;

import java.util.List;

import catchla.yep.R;
import catchla.yep.adapter.BaseRecyclerViewAdapter;
import catchla.yep.adapter.iface.ItemClickListener;
import catchla.yep.model.Skill;
import catchla.yep.model.User;
import catchla.yep.util.ImageLoaderWrapper;
import catchla.yep.util.ListUtils;
import catchla.yep.util.Utils;

/**
 * Created by mariotaku on 15/4/29.
 */
public class FriendGridViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private final BaseRecyclerViewAdapter adapter;
    private final ItemClickListener listener;

    private final ImageView profileImageView;
    private final TextView nameView, descriptionView;
    private final FlowLayout userSkills;

    public FriendGridViewHolder(View itemView, final BaseRecyclerViewAdapter adapter, final ItemClickListener listener) {
        super(itemView);
        this.adapter = adapter;
        this.listener = listener;
        profileImageView = (ImageView) itemView.findViewById(R.id.profile_image);
        nameView = (TextView) itemView.findViewById(R.id.name);
        descriptionView = (TextView) itemView.findViewById(R.id.description);
        userSkills = (FlowLayout) itemView.findViewById(R.id.user_skills);
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (listener == null) return;
        listener.onItemClick(getAdapterPosition(), FriendGridViewHolder.this);
    }

    public void displayUser(final User user) {
        final ImageLoaderWrapper imageLoader = adapter.getImageLoader();
        imageLoader.displayProfileImage(user.getAvatarUrl(), profileImageView);
        nameView.setText(user.getNickname());
        descriptionView.setText(user.getIntroduction());
        userSkills.removeAllViews();
        final List<Skill> skills = ListUtils.nonNullList(user.getMasterSkills());
        final LayoutInflater inflater = LayoutInflater.from(adapter.getContext());
        for (int i = 0, skillsSize = Math.min(3, skills.size()); i < skillsSize; i++) {
            final Skill skill = skills.get(i);
            final TextView textView = (TextView) inflater.inflate(R.layout.layout_friend_grid_skill,
                    userSkills, false);
            textView.setText(Utils.getDisplayName(skill));
            userSkills.addView(textView);
        }
    }

}
