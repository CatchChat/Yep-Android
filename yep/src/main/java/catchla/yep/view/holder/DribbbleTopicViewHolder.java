package catchla.yep.view.holder;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import catchla.yep.R;
import catchla.yep.adapter.TopicsAdapter;
import catchla.yep.model.DribbbleAttachment;
import catchla.yep.model.Topic;
import catchla.yep.util.ImageLoaderWrapper;

/**
 * Created by mariotaku on 15/12/9.
 */
public class DribbbleTopicViewHolder extends TopicViewHolder {

    private final ImageView mediaPreviewView;
    private final TextView titleView;

    public DribbbleTopicViewHolder(final View itemView, final Context context,
                                   final ImageLoaderWrapper imageLoader,
                                   final TopicsAdapter.TopicClickAdapter listener) {
        super(itemView, context, imageLoader, listener);
        mediaPreviewView = (ImageView) itemView.findViewById(R.id.media_preview);
        titleView = (TextView) itemView.findViewById(R.id.title);
    }

    @Override
    public void displayTopic(final Topic topic) {
        super.displayTopic(topic);
        DribbbleAttachment attachment = (DribbbleAttachment) topic.getAttachments().get(0);
        getImageLoader().displayImage(attachment.getMediaUrl(), mediaPreviewView);
        titleView.setText(attachment.getTitle());
    }
}
