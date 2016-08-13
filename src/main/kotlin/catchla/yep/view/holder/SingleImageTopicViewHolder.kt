package catchla.yep.view.holder

import android.content.Context
import android.support.v4.view.ViewCompat
import android.view.View
import android.widget.ImageView

import catchla.yep.Constants
import catchla.yep.R
import catchla.yep.adapter.TopicsAdapter
import catchla.yep.model.Attachment
import catchla.yep.model.FileAttachment
import catchla.yep.model.Topic
import catchla.yep.util.ImageLoaderWrapper

/**
 * Created by mariotaku on 15/12/9.
 */
class SingleImageTopicViewHolder(adapter: TopicsAdapter, itemView: View, context: Context,
                                 imageLoader: ImageLoaderWrapper,
                                 listener: TopicsAdapter.TopicClickListener?) : TopicViewHolder(adapter, itemView, context, imageLoader, listener), Constants {

    private val mediaPreviewView: ImageView

    init {
        itemView.findViewById(R.id.attachmentView).setOnClickListener(this)
        mediaPreviewView = itemView.findViewById(R.id.image) as ImageView
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.attachmentView -> {
                val attachments = adapter.getTopic(layoutPosition).attachments
                listener!!.onMediaClick(attachments.toTypedArray(),
                        attachments[0], v)
                return
            }
        }
        super.onClick(v)
    }

    override fun displayTopic(topic: Topic) {
        super.displayTopic(topic)
        val attachment = topic.attachments[0] as FileAttachment
        val mediaUrl = attachment.file.url
        if (mediaUrl != mediaPreviewView.tag) {
            imageLoader.displayImage(mediaUrl, mediaPreviewView)
        }
        mediaPreviewView.tag = mediaUrl
    }
}
