package catchla.yep.view.holder;

import android.content.Context;
import android.content.Intent;
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

import catchla.yep.Constants;
import catchla.yep.R;
import catchla.yep.activity.MediaViewerActivity;
import catchla.yep.adapter.LoadMoreSupportAdapter;
import catchla.yep.adapter.TopicsAdapter;
import catchla.yep.model.AppleMediaAttachment;
import catchla.yep.model.Attachment;
import catchla.yep.model.AttachmentFile;
import catchla.yep.model.BasicAttachment;
import catchla.yep.model.DribbbleAttachment;
import catchla.yep.model.GithubAttachment;
import catchla.yep.model.LatLng;
import catchla.yep.model.LocationAttachment;
import catchla.yep.model.Skill;
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
    private final Context context;
    private final ImageLoaderWrapper imageLoader;
    private final TextView nameView;
    private final TextView textView;
    private final ShortTimeView timeView;
    private final TextView distanceView;
    private final TextView messagesCountView;
    private final Location currentLocation, tempLocation;
    private final TopicsAdapter.TopicClickAdapter listener;
    private final RecyclerView mediaGallery;
    private final TextView skillButton;
    private final TopicAttachmentsAdapter topicMediaAdapter;

    public TopicViewHolder(final View itemView, final Context context, final ImageLoaderWrapper imageLoader, final TopicsAdapter.TopicClickAdapter listener) {
        super(itemView);
        this.context = context;
        this.imageLoader = imageLoader;
        this.listener = listener;
        if (listener != null) {
            itemView.setOnClickListener(this);
        }
        profileImageView = (ImageView) itemView.findViewById(R.id.profile_image);
        nameView = (TextView) itemView.findViewById(R.id.name);
        textView = (TextView) itemView.findViewById(R.id.text);
        timeView = (ShortTimeView) itemView.findViewById(R.id.time);
        distanceView = (TextView) itemView.findViewById(R.id.distance);
        messagesCountView = (TextView) itemView.findViewById(R.id.messages_count);
        skillButton = (TextView) itemView.findViewById(R.id.skill_button);
        currentLocation = Utils.getCachedLocation(context);
        tempLocation = new Location("");
        mediaGallery = (RecyclerView) itemView.findViewById(R.id.media_gallery);
        mediaGallery.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        topicMediaAdapter = new TopicAttachmentsAdapter(context);
        mediaGallery.setAdapter(topicMediaAdapter);

        final Resources res = context.getResources();
        final int decorPaddingLeft = res.getDimensionPixelSize(R.dimen.icon_size_topic_item_profile_image);

        mediaGallery.setPadding(decorPaddingLeft, 0, 0, 0);
        skillButton.setOnClickListener(this);
    }

    public void displayTopic(final Topic topic) {
        final User user = topic.getUser();
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
        final Skill skill = topic.getSkill();
        if (skill != null) {
            skillButton.setText(Utils.getDisplayName(skill));
            skillButton.setVisibility(View.VISIBLE);
        } else {
            skillButton.setText(null);
            skillButton.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.item_content: {
                listener.onItemClick(getLayoutPosition(), this);
                break;
            }
            case R.id.skill_button: {
                listener.onSkillClick(getLayoutPosition(), this);
                break;
            }
        }
    }

    public void setReplyButtonVisible(final boolean visible) {
        messagesCountView.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
    }

    private static class TopicAttachmentsAdapter extends LoadMoreSupportAdapter implements Constants {
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
                itemView.setOnClickListener(this);
                this.adapter = adapter;
                this.mediaPreviewView = (ImageView) itemView.findViewById(R.id.media_preview);
                this.mediaRemoveView = (ImageView) itemView.findViewById(R.id.media_remove);

                mediaRemoveView.setVisibility(View.GONE);
            }

            public void displayMedia(final Attachment media) {
                final ImageLoaderWrapper imageLoader = adapter.getImageLoader();
                if (media instanceof DribbbleAttachment) {
                    final String mediaUrl = ((DribbbleAttachment) media).getMediaUrl();
                    if (mediaUrl == null) return;
                    imageLoader.displayImage(mediaUrl, mediaPreviewView);
                } else if (media instanceof GithubAttachment) {

                } else if (media instanceof LocationAttachment) {

                } else if (media instanceof AppleMediaAttachment) {

                } else if (media instanceof BasicAttachment) {
                    final AttachmentFile file = ((BasicAttachment) media).getFile();
                    if (file == null) return;
                    imageLoader.displayImage(file.getUrl(), mediaPreviewView);
                }
            }

            @Override
            public void onClick(final View v) {
                switch (v.getId()) {
                    case R.id.item_content: {
                        adapter.notifyMediaClicked(getLayoutPosition());
                        break;
                    }
                }
            }
        }

        private void notifyMediaClicked(final int position) {
            final Intent intent = new Intent(getContext(), MediaViewerActivity.class);
            intent.putExtra(EXTRA_MEDIA, mAttachments.toArray(new Attachment[mAttachments.size()]));
            intent.putExtra(EXTRA_CURRENT_MEDIA, mAttachments.get(position));
            getContext().startActivity(intent);
        }

    }

}
