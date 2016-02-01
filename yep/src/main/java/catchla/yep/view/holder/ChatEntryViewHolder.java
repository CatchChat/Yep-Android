/*
 * Copyright (c) 2015. Catch Inc,
 */

package catchla.yep.view.holder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import catchla.yep.R;
import catchla.yep.adapter.ChatsListAdapter;
import catchla.yep.adapter.iface.ItemClickListener;
import catchla.yep.model.Conversation;
import catchla.yep.model.Message;
import catchla.yep.util.ImageLoaderWrapper;
import catchla.yep.util.Utils;
import catchla.yep.view.ShortTimeView;

/**
 * Created by mariotaku on 15/4/29.
 */
public class ChatEntryViewHolder extends RecyclerView.ViewHolder {

    private final ChatsListAdapter adapter;

    private final ImageView profileImageView;
    private final TextView nameView;
    private final ShortTimeView timeView;
    private final TextView messageView;

    public ChatEntryViewHolder(View itemView, final ChatsListAdapter adapter, final ItemClickListener listener) {
        super(itemView);
        this.adapter = adapter;
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
        if (!conversation.isValid()) return;
        nameView.setText(Utils.getConversationName(conversation));
        final ImageLoaderWrapper imageLoader = adapter.getImageLoader();
        imageLoader.displayProfileImage(Utils.getConversationAvatarUrl(conversation), profileImageView);
        messageView.setText(getConversationSummary(adapter.getContext(), conversation));
        timeView.setTime(Utils.getTime(conversation.getUpdatedAt()));
    }

    public void displayCirclesEntry(final Conversation conversation) {
        nameView.setText(R.string.circles);
        messageView.setText(getConversationSummary(adapter.getContext(), conversation));
        timeView.setTime(Utils.getTime(conversation.getUpdatedAt()));
    }

    private String getConversationSummary(final Context context, final Conversation conversation) {
        if (Message.MediaType.LOCATION.equalsIgnoreCase(conversation.getMediaType())) {
            return context.getString(R.string.location);
        } else if (Message.MediaType.IMAGE.equalsIgnoreCase(conversation.getMediaType())) {
            return context.getString(R.string.image);
        } else if (Message.MediaType.AUDIO.equalsIgnoreCase(conversation.getMediaType())) {
            return context.getString(R.string.audio);
        } else {
            return conversation.getTextContent();
        }
    }
}
