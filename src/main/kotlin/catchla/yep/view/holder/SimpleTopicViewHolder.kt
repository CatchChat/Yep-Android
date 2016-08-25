package catchla.yep.view.holder

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import catchla.yep.R
import catchla.yep.adapter.iface.IBaseRecyclerViewAdapter
import catchla.yep.model.FileAttachment
import catchla.yep.model.Topic
import catchla.yep.util.highlightedString
import kotlinx.android.synthetic.main.list_item_topic_simple.view.*

/**
 * Created by mariotaku on 16/8/25.
 */
class SimpleTopicViewHolder(
        itemView: View,
        val adapter: IBaseRecyclerViewAdapter,
        listener: ((Int, SimpleTopicViewHolder) -> Unit)?
) : RecyclerView.ViewHolder(itemView) {

    var dataPosition: Int = RecyclerView.NO_POSITION

    val summaryView: TextView by lazy { itemView.summary }
    val topicImageView: ImageView by lazy { itemView.topicImage }

    init {
        itemView.setOnClickListener {
            listener?.invoke(dataPosition, this)
        }
    }

    fun display(topic: Topic, highlight: String? = null) {
        val image = topic.attachments.firstOrNull() as? FileAttachment
        if (image != null) {
            topicImageView.scaleType = ImageView.ScaleType.CENTER_CROP
            adapter.imageLoader.displayProviderPreviewImage(image.file.url, topicImageView)
        } else {
            topicImageView.scaleType = ImageView.ScaleType.CENTER_INSIDE
            topicImageView.setImageResource(R.drawable.ic_feed_placeholder_text)
        }
        summaryView.text = topic.body?.highlightedString(highlight)
    }

}