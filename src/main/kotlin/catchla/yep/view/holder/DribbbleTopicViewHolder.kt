package catchla.yep.view.holder

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.view.View
import catchla.yep.adapter.TopicsAdapter
import catchla.yep.model.DribbbleAttachment
import catchla.yep.model.Topic
import catchla.yep.util.ImageLoaderWrapper
import catchla.yep.util.Utils
import catchla.yep.util.view.ViewSupport
import kotlinx.android.synthetic.main.layout_topic_attachment_dribbble.view.*

/**
 * Created by mariotaku on 15/12/9.
 */
class DribbbleTopicViewHolder(topicsAdapter: TopicsAdapter, itemView: View, context: Context,
                              imageLoader: ImageLoaderWrapper,
                              listener: TopicsAdapter.TopicClickListener?) : TopicViewHolder(topicsAdapter, itemView, context, imageLoader, listener) {
    private val attachmentView by lazy { itemView.attachmentView }
    private val mediaPreviewView by lazy { itemView.mediaPreview }
    private val titleView by lazy { itemView.title }

    init {
        attachmentView.setOnClickListener(this)
        ViewSupport.setClipToOutline(attachmentView, true)
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
