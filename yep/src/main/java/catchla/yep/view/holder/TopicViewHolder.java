package catchla.yep.view.holder;

import android.content.Context;
import android.content.res.Resources;
import android.location.Location;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import catchla.yep.R;
import catchla.yep.adapter.BaseRecyclerViewAdapter;
import catchla.yep.adapter.TopicsAdapter;
import catchla.yep.adapter.iface.ItemClickListener;
import catchla.yep.model.Attachment;
import catchla.yep.model.LatLng;
import catchla.yep.model.Topic;
import catchla.yep.model.User;
import catchla.yep.util.ImageLoaderWrapper;
import catchla.yep.util.Utils;
import catchla.yep.view.ShortTimeView;

/**
 * Created by mariotaku on 15/10/12.
 */
public class TopicViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private final ImageView profileImageView;
    private final TopicsAdapter adapter;
    private final TextView nameView;
    private final TextView textView;
    private final ShortTimeView timeView;
    private final TextView distanceView;
    private final TextView messagesCountView;
    private final Location currentLocation, tempLocation;
    private final ItemClickListener listener;
    private final RecyclerView mediaGallery;
    private final TopicAttachmentsAdapter topicMediaAdapter;

    public TopicViewHolder(final View itemView, final TopicsAdapter adapter, final ItemClickListener listener) {
        super(itemView);
        this.listener = listener;
        itemView.setOnClickListener(this);
        this.adapter = adapter;
        profileImageView = (ImageView) itemView.findViewById(R.id.profile_image);
        nameView = (TextView) itemView.findViewById(R.id.name);
        textView = (TextView) itemView.findViewById(R.id.text);
        timeView = (ShortTimeView) itemView.findViewById(R.id.time);
        distanceView = (TextView) itemView.findViewById(R.id.distance);
        messagesCountView = (TextView) itemView.findViewById(R.id.messages_count);
        currentLocation = Utils.getCachedLocation(adapter.getContext());
        tempLocation = new Location("");
        mediaGallery = (RecyclerView) itemView.findViewById(R.id.media_gallery);
        mediaGallery.setLayoutManager(new LinearLayoutManager(adapter.getContext(), LinearLayoutManager.HORIZONTAL, false));
        topicMediaAdapter = new TopicAttachmentsAdapter(adapter.getContext());
        mediaGallery.setAdapter(topicMediaAdapter);

        final Resources res = adapter.getContext().getResources();
        final int decorPaddingLeft = res.getDimensionPixelSize(R.dimen.icon_size_topic_item_profile_image);

        mediaGallery.setPadding(decorPaddingLeft, 0, 0, 0);
    }

    public void displayTopic(final Topic topic) {
        final User user = topic.getUser();
        final ImageLoaderWrapper imageLoader = adapter.getImageLoader();
        imageLoader.displayProfileImage(user.getAvatarUrl(), profileImageView);
        nameView.setText(Utils.getDisplayName(user));
        textView.setText(topic.getBody());
        timeView.setTime(topic.getCreatedAt().getTime());
        final LatLng userLocation = topic.getUser().getLocation();
        if (currentLocation != null && userLocation != null) {
            distanceView.setVisibility(View.VISIBLE);
            tempLocation.setLatitude(userLocation.getLatitude());
            tempLocation.setLongitude(userLocation.getLongitude());
            distanceView.setText(Utils.getDistanceString(currentLocation.distanceTo(tempLocation)));
        } else {
            distanceView.setVisibility(View.GONE);
        }
        messagesCountView.setText(String.valueOf(topic.getMessageCount()));
        final List<Attachment> attachments = topic.getAttachments();
        topicMediaAdapter.setAttachments(attachments);
        if (attachments == null || attachments.isEmpty()) {
            mediaGallery.setVisibility(View.GONE);
        } else {
            mediaGallery.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(final View v) {
        listener.onItemClick(getLayoutPosition(), this);
    }

    private static class TopicAttachmentsAdapter extends BaseRecyclerViewAdapter {
        private static final int VIEW_TYPE_ITEM = 1;
        private final LayoutInflater mInflater;
        private List<Attachment> mAttachments;

        public TopicAttachmentsAdapter(final Context context) {
            super(context);
            mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getItemViewType(final int position) {
            return VIEW_TYPE_ITEM;
        }

        @Override
        public int getItemCount() {
            if (mAttachments == null) return 0;
            return mAttachments.size();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
            switch (viewType) {
                case VIEW_TYPE_ITEM: {
                    final View view = mInflater.inflate(R.layout.adapter_item_topic_media_item, parent, false);
                    return new TopicMediaItemHolder(view, this);
                }
            }
            throw new UnsupportedOperationException("Unsupported itemType " + viewType);
        }

        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
            switch (getItemViewType(position)) {
                case VIEW_TYPE_ITEM: {
                    final TopicMediaItemHolder itemHolder = (TopicMediaItemHolder) holder;
                    itemHolder.displayMedia(mAttachments.get(position));
                    break;
                }
            }
        }


        public void setAttachments(final List<Attachment> attachments) {
            mAttachments = attachments;
            notifyDataSetChanged();
        }

        private class TopicMediaItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            private final TopicAttachmentsAdapter adapter;
            private final ImageView mediaPreviewView;
            private final ImageView mediaRemoveView;

            public TopicMediaItemHolder(final View itemView, final TopicAttachmentsAdapter adapter) {
                super(itemView);
                this.adapter = adapter;
                this.mediaPreviewView = (ImageView) itemView.findViewById(R.id.media_preview);
                this.mediaRemoveView = (ImageView) itemView.findViewById(R.id.media_remove);

                mediaRemoveView.setVisibility(View.GONE);
            }

            public void displayMedia(final Attachment media) {
                final ImageLoaderWrapper imageLoader = adapter.getImageLoader();
                imageLoader.displayImage(media.getFile().getUrl(), mediaPreviewView);
            }

            @Override
            public void onClick(final View v) {
                switch (v.getId()) {
                }
            }
        }

    }

}
