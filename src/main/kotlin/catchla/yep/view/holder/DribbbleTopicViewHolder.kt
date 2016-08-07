package catchla.yep.view.holder

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.View
import android.widget.ImageView
import android.widget.TextView

import catchla.yep.R
import catchla.yep.adapter.TopicsAdapter
import catchla.yep.model.DribbbleAttachment
import catchla.yep.model.Topic
import catchla.yep.util.ImageLoaderWrapper
import catchla.yep.util.Utils

/**
 * Created by mariotaku on 15/12/9.
 */
class DribbbleTopicViewHolder(topicsAdapter: TopicsAdapter, itemView: View, context: Context,
                              imageLoader: ImageLoaderWrapper,
                              listener: TopicsAdapter.TopicClickListener?) : TopicViewHolder(topicsAdapter, itemView, context, imageLoader, listener) {

    private val mediaPreviewView: ImageView
    private val titleView: TextView

    init {
        itemView.findViewById(R.id.attachment_view).setOnClickListener(this)
        mediaPreviewView = itemView.findViewById(R.id.media_preview) as ImageView
        titleView = itemView.findViewById(R.id.title) as TextView
    }

    override fun displayTopic(topic: Topic) {
        super.displayTopic(topic)
        val attachment = getDribbbleAttachment(topic)
        imageLoader.displayImage(attachment.mediaUrl, mediaPreviewView)
        titleView.text = attachment.title
    }

    private fun getDribbbleAttachment(topic: Topic): DribbbleAttachment {
        return topic.attachments[0] as DribbbleAttachment
    }

    override fun onAttachmentClick() {
        val attachment = getDribbbleAttachment(adapter.getTopic(layoutPosition))
        Utils.openUri(adapter.context as Activity, Uri.parse(attachment.url))
    }
}
