package catchla.yep.view.holder

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.view.View
import catchla.yep.R
import catchla.yep.adapter.TopicsAdapter
import catchla.yep.model.Topic
import catchla.yep.model.WebPageAttachment
import catchla.yep.util.ImageLoaderWrapper
import catchla.yep.util.Utils
import catchla.yep.util.view.ViewSupport
import kotlinx.android.synthetic.main.layout_topic_attachment_webpage.view.*

/**
 * Created by mariotaku on 15/12/9.
 */
class WebPageTopicViewHolder(
        topicsAdapter: TopicsAdapter,
        itemView: View,
        context: Context,
        imageLoader: ImageLoaderWrapper,
        listener: TopicsAdapter.TopicClickListener?
) : TopicViewHolder(topicsAdapter, itemView, context, imageLoader, listener) {

    private val attachmentView by lazy { itemView.attachmentView }
    private val websiteName by lazy { itemView.websiteName }
    private val websiteDescription by lazy { itemView.websiteDescription }
    private val websiteTitle by lazy { itemView.websiteTitle }
    private val websiteImage by lazy { itemView.websiteImage }

    init {
        attachmentView.setOnClickListener(this)
        ViewSupport.setClipToOutline(attachmentView, true)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.attachmentView -> {
                val attachment = adapter.getTopic(layoutPosition).attachments[0] as WebPageAttachment
                Utils.openUri(adapter.context as Activity, Uri.parse(attachment.url))
                return
            }
        }
        super.onClick(v)
    }

    override fun displayTopic(topic: Topic, highlight: String?) {
        super.displayTopic(topic, highlight)
        val attachment = topic.attachments.firstOrNull() as WebPageAttachment
        websiteName.text = attachment.siteName
        websiteTitle.text = attachment.title
        websiteDescription.text = attachment.description
        imageLoader.displayImage(attachment.imageUrl, websiteImage)
        websiteImage.visibility = if (attachment.imageUrl != null) View.VISIBLE else View.GONE
    }
}
