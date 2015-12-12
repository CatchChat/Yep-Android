package catchla.yep.view.holder;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.view.View;
import android.widget.ImageView;

import java.util.List;

import catchla.yep.Constants;
import catchla.yep.R;
import catchla.yep.adapter.TopicsAdapter;
import catchla.yep.model.Attachment;
import catchla.yep.model.FileAttachment;
import catchla.yep.model.Topic;
import catchla.yep.util.ImageLoaderWrapper;

/**
 * Created by mariotaku on 15/12/9.
 */
public class SingleImageTopicViewHolder extends TopicViewHolder implements Constants {

    private final ImageView mediaPreviewView;

    public SingleImageTopicViewHolder(final TopicsAdapter adapter, final View itemView, final Context context,
                                      final ImageLoaderWrapper imageLoader,
                                      final TopicsAdapter.TopicClickListener listener) {
        super(adapter, itemView, context, imageLoader, listener);
        itemView.findViewById(R.id.attachment_view).setOnClickListener(this);
        mediaPreviewView = (ImageView) itemView.findViewById(R.id.image);
        ViewCompat.setTransitionName(mediaPreviewView, "media");
    }

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.attachment_view: {
                final List<Attachment> attachments = adapter.getTopic(getLayoutPosition()).getAttachments();
                listener.onMediaClick(attachments.toArray(new Attachment[attachments.size()]),
                        attachments.get(0), v);
                return;
            }
        }
        super.onClick(v);
    }

    @Override
    public void displayTopic(final Topic topic) {
        super.displayTopic(topic);
        FileAttachment attachment = (FileAttachment) topic.getAttachments().get(0);
        final String mediaUrl = attachment.getFile().getUrl();
        if (!mediaUrl.equals(mediaPreviewView.getTag())) {
            getImageLoader().displayImage(mediaUrl, mediaPreviewView);
        }
        mediaPreviewView.setTag(mediaUrl);
    }
}
