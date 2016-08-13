package catchla.yep.view.holder

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import catchla.yep.Constants
import catchla.yep.R
import catchla.yep.adapter.LoadMoreSupportAdapter
import catchla.yep.adapter.TopicsAdapter
import catchla.yep.model.Attachment
import catchla.yep.model.FileAttachment
import catchla.yep.model.Topic
import catchla.yep.util.ImageLoaderWrapper

/**
 * Created by mariotaku on 15/12/9.
 */
class GalleryTopicViewHolder(topicsAdapter: TopicsAdapter, itemView: View, context: Context,
                             imageLoader: ImageLoaderWrapper,
                             listener: TopicsAdapter.TopicClickListener?) : TopicViewHolder(topicsAdapter, itemView, context, imageLoader, listener) {
    private val mediaGallery: RecyclerView
    private val topicMediaAdapter: TopicAttachmentsAdapter

    init {
        mediaGallery = itemView.findViewById(R.id.attachmentView) as RecyclerView
        mediaGallery.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        topicMediaAdapter = GalleryTopicViewHolder.TopicAttachmentsAdapter(context, listener)
        mediaGallery.adapter = topicMediaAdapter

        val res = context.resources
        val decorPaddingLeft = res.getDimensionPixelSize(R.dimen.icon_size_topic_item_profile_image)

        mediaGallery.setPadding(decorPaddingLeft, 0, 0, 0)
    }

    override fun displayTopic(topic: Topic) {
        super.displayTopic(topic)
        val attachments = topic.attachments
        topicMediaAdapter.attachments = attachments
        if (attachments == null || attachments.isEmpty()) {
            mediaGallery.visibility = View.GONE
        } else {
            mediaGallery.visibility = View.VISIBLE
        }
    }

    private class TopicAttachmentsAdapter(context: Context, private val listener: TopicsAdapter.TopicClickListener?) : LoadMoreSupportAdapter<RecyclerView.ViewHolder>(context), Constants {
        private val inflater: LayoutInflater
        var attachments: List<Attachment>? = null
            set(value) {
                field = value
                notifyDataSetChanged()
            }

        init {
            inflater = LayoutInflater.from(context)
        }

        override fun getItemViewType(position: Int): Int {
            return VIEW_TYPE_BASIC_ATTACHMENT
        }

        override fun getItemCount(): Int {
            if (attachments == null) return 0
            return attachments!!.size
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            when (viewType) {
                VIEW_TYPE_BASIC_ATTACHMENT -> {
                    val view = inflater.inflate(R.layout.adapter_item_topic_media_item, parent, false)
                    return GalleryAttachmentItemHolder(view, this)
                }
            }
            throw UnsupportedOperationException("Unsupported itemType " + viewType)
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            when (getItemViewType(position)) {
                VIEW_TYPE_BASIC_ATTACHMENT -> {
                    val itemHolder = holder as GalleryAttachmentItemHolder
                    itemHolder.displayMedia(attachments!![position])
                }
            }
        }

        private class GalleryAttachmentItemHolder(itemView: View, private val adapter: TopicAttachmentsAdapter) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
            private val mediaPreviewView: ImageView
            private val mediaRemoveView: ImageView

            init {
                itemView.setOnClickListener(this)
                this.mediaPreviewView = itemView.findViewById(R.id.media_preview) as ImageView
                this.mediaRemoveView = itemView.findViewById(R.id.media_remove) as ImageView

                mediaRemoveView.visibility = View.GONE
            }

            fun displayMedia(media: Attachment) {
                val imageLoader = adapter.imageLoader
                val file = (media as FileAttachment).file ?: return
                imageLoader.displayImage(file.url, mediaPreviewView)
            }

            override fun onClick(v: View) {
                when (v.id) {
                    R.id.item_content -> {
                        adapter.notifyMediaClicked(layoutPosition, v)
                    }
                }
            }
        }

        private fun notifyMediaClicked(position: Int, v: View) {
            listener!!.onMediaClick(attachments!!.toTypedArray(),
                    attachments!![position], v)
        }

        companion object {
            private val VIEW_TYPE_BASIC_ATTACHMENT = 1
        }

    }
}
