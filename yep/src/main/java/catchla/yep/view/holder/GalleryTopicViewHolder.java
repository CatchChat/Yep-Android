package catchla.yep.view.holder;

import android.content.Context;
import android.content.res.Resources;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

import catchla.yep.Constants;
import catchla.yep.R;
import catchla.yep.adapter.LoadMoreSupportAdapter;
import catchla.yep.adapter.TopicsAdapter;
import catchla.yep.model.Attachment;
import catchla.yep.model.AttachmentFile;
import catchla.yep.model.FileAttachment;
import catchla.yep.model.Topic;
import catchla.yep.util.ImageLoaderWrapper;

/**
 * Created by mariotaku on 15/12/9.
 */
public class GalleryTopicViewHolder extends TopicViewHolder {
    private final RecyclerView mediaGallery;
    private final TopicAttachmentsAdapter topicMediaAdapter;

    public GalleryTopicViewHolder(final TopicsAdapter topicsAdapter, final View itemView, final Context context,
                                  final ImageLoaderWrapper imageLoader,
                                  final TopicsAdapter.TopicClickListener listener) {
        super(topicsAdapter, itemView, context, imageLoader, listener);
        mediaGallery = (RecyclerView) itemView.findViewById(R.id.attachment_view);
        mediaGallery.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        topicMediaAdapter = new GalleryTopicViewHolder.TopicAttachmentsAdapter(context, listener);
        mediaGallery.setAdapter(topicMediaAdapter);

        final Resources res = context.getResources();
        final int decorPaddingLeft = res.getDimensionPixelSize(R.dimen.icon_size_topic_item_profile_image);

        mediaGallery.setPadding(decorPaddingLeft, 0, 0, 0);
    }

    @Override
    public void displayTopic(final Topic topic) {
        super.displayTopic(topic);
        final List<Attachment> attachments = topic.getAttachments();
        topicMediaAdapter.setAttachments(attachments);
        if (attachments == null || attachments.isEmpty()) {
            mediaGallery.setVisibility(View.GONE);
        } else {
            mediaGallery.setVisibility(View.VISIBLE);
        }
    }

    private static class TopicAttachmentsAdapter extends LoadMoreSupportAdapter implements Constants {
        private static final int VIEW_TYPE_BASIC_ATTACHMENT = 1;
        private final LayoutInflater mInflater;
        private final TopicsAdapter.TopicClickListener listener;
        private List<Attachment> mAttachments;

        public TopicAttachmentsAdapter(final Context context, final TopicsAdapter.TopicClickListener listener) {
            super(context);
            this.listener = listener;
            mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getItemViewType(final int position) {
            return VIEW_TYPE_BASIC_ATTACHMENT;
        }

        @Override
        public int getItemCount() {
            if (mAttachments == null) return 0;
            return mAttachments.size();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
            switch (viewType) {
                case VIEW_TYPE_BASIC_ATTACHMENT: {
                    final View view = mInflater.inflate(R.layout.adapter_item_topic_media_item, parent, false);
                    return new GalleryAttachmentItemHolder(view, this);
                }
            }
            throw new UnsupportedOperationException("Unsupported itemType " + viewType);
        }

        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
            switch (getItemViewType(position)) {
                case VIEW_TYPE_BASIC_ATTACHMENT: {
                    final GalleryAttachmentItemHolder itemHolder = (GalleryAttachmentItemHolder) holder;
                    itemHolder.displayMedia(mAttachments.get(position));
                    break;
                }
            }
        }


        public void setAttachments(final List<Attachment> attachments) {
            mAttachments = attachments;
            notifyDataSetChanged();
        }


        private static class GalleryAttachmentItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            private final TopicAttachmentsAdapter adapter;
            private final ImageView mediaPreviewView;
            private final ImageView mediaRemoveView;

            public GalleryAttachmentItemHolder(final View itemView, final TopicAttachmentsAdapter adapter) {
                super(itemView);
                itemView.setOnClickListener(this);
                this.adapter = adapter;
                this.mediaPreviewView = (ImageView) itemView.findViewById(R.id.media_preview);
                ViewCompat.setTransitionName(mediaPreviewView, "media");
                this.mediaRemoveView = (ImageView) itemView.findViewById(R.id.media_remove);

                mediaRemoveView.setVisibility(View.GONE);
            }

            public void displayMedia(final Attachment media) {
                final ImageLoaderWrapper imageLoader = adapter.getImageLoader();
                final AttachmentFile file = ((FileAttachment) media).getFile();
                if (file == null) return;
                imageLoader.displayImage(file.getUrl(), mediaPreviewView);
            }

            @Override
            public void onClick(final View v) {
                switch (v.getId()) {
                    case R.id.item_content: {
                        adapter.notifyMediaClicked(getLayoutPosition(), v);
                        break;
                    }
                }
            }
        }

        private void notifyMediaClicked(final int position, final View v) {
            listener.onMediaClick(mAttachments.toArray(new Attachment[mAttachments.size()]),
                    mAttachments.get(position), v);
        }

    }
}
