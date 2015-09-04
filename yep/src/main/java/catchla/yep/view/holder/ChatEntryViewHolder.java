/*
 * Copyright (c) 2015. Catch Inc,
 */

package catchla.yep.view.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import catchla.yep.R;
import catchla.yep.adapter.iface.ItemClickListener;
import catchla.yep.model.Conversation;
import catchla.yep.model.Message;
import catchla.yep.model.User;
import catchla.yep.util.Utils;
import catchla.yep.view.ShortTimeView;

/**
 * Created by mariotaku on 15/4/29.
 */
public class ChatEntryViewHolder extends RecyclerView.ViewHolder {
    private final ImageView profileImageView;
    private final TextView nameView;
    private final ShortTimeView timeView;
    private final TextView messageView;

    public ChatEntryViewHolder(final ItemClickListener listener, View itemView) {
        super(itemView);
        profileImageView = (ImageView) itemView.findViewById(R.id.profile_image);
        nameView = (TextView) itemView.findViewById(R.id.name);
        timeView = (ShortTimeView) itemView.findViewById(R.id.update_time);
        messageView = (TextView) itemView.findViewById(R.id.message);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener == null) return;
                listener.onItemClick(getAdapterPosition(), ChatEntryViewHolder.this);
            }
        });
    }

    public void displayConversation(final Conversation conversation) {
        final String recipientType = conversation.getRecipientType();
        if (Message.RecipientType.USER.equalsIgnoreCase(recipientType)) {
            final User sender = conversation.getUser();
            nameView.setText(sender.getNickname());
            Picasso.with(profileImageView.getContext())
                    .load(sender.getAvatarUrl())
                    .placeholder(R.drawable.ic_profile_image_default)
                    .fit()
                    .into(profileImageView);
        } else if (Message.RecipientType.CIRCLE.equalsIgnoreCase(recipientType)) {
            nameView.setText(conversation.getCircle().getName());
        } else {
            throw new UnsupportedOperationException(recipientType);
        }
        if (Message.MediaType.LOCATION.equalsIgnoreCase(conversation.getMediaType())) {
            messageView.setText(R.string.location);
        } else if (Message.MediaType.IMAGE.equalsIgnoreCase(conversation.getMediaType())) {
            messageView.setText(R.string.image);
        } else if (Message.MediaType.AUDIO.equalsIgnoreCase(conversation.getMediaType())) {
            messageView.setText(R.string.audio);
        } else {
            messageView.setText(conversation.getTextContent());
        }
        timeView.setTime(Utils.getTime(conversation.getUpdatedAt()));
    }
}
