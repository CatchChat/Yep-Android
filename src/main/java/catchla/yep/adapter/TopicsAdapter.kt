/*
 * Copyright (c) 2015. Catch Inc,
 */

package catchla.yep.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import catchla.yep.R
import catchla.yep.adapter.iface.ILoadMoreSupportAdapter
import catchla.yep.adapter.iface.ItemClickListener
import catchla.yep.model.Attachment
import catchla.yep.model.Topic
import catchla.yep.view.holder.*

/**
 * Created by mariotaku on 15/4/29.
 */
class TopicsAdapter(context: Context) : LoadMoreSupportAdapter<RecyclerView.ViewHolder>(context) {

    private val mInflater: LayoutInflater

    var clickListener: TopicClickListener? = null
        set

    var topics: List<Topic>? = null
        set (value) {
            topics = value
            notifyDataSetChanged()
        }

    init {
        mInflater = LayoutInflater.from(context)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            ITEM_VIEW_TYPE_MEDIA_GALLERY -> {
                val view = mInflater.inflate(R.layout.list_item_topic, parent, false)
                mInflater.inflate(R.layout.layout_topic_attachment_gallery, view as ViewGroup)
                return GalleryTopicViewHolder(this, view, context, imageLoader, clickListener)
            }
            ITEM_VIEW_TYPE_GITHUB -> {
                val view = mInflater.inflate(R.layout.list_item_topic, parent, false)
                mInflater.inflate(R.layout.layout_topic_attachment_github, view as ViewGroup)
                return GithubTopicViewHolder(this, view, context, imageLoader, clickListener)
            }
            ITEM_VIEW_TYPE_DRIBBBLE -> {
                val view = mInflater.inflate(R.layout.list_item_topic, parent, false)
                mInflater.inflate(R.layout.layout_topic_attachment_dribbble, view as ViewGroup)
                return DribbbleTopicViewHolder(this, view, context, imageLoader, clickListener)
            }
            ITEM_VIEW_TYPE_LOCATION -> {
                val view = mInflater.inflate(R.layout.list_item_topic, parent, false)
                mInflater.inflate(R.layout.layout_topic_attachment_location, view as ViewGroup)
                return LocationTopicViewHolder(this, view, context, imageLoader, clickListener)
            }
            ITEM_VIEW_TYPE_BASIC -> {
                val view = mInflater.inflate(R.layout.list_item_topic, parent, false)
                mInflater.inflate(R.layout.layout_topic_attachment_null, view as ViewGroup)
                return TopicViewHolder(this, view, context, imageLoader, clickListener)
            }
            ITEM_VIEW_TYPE_SINGLE_IMAGE -> {
                val view = mInflater.inflate(R.layout.list_item_topic, parent, false)
                mInflater.inflate(R.layout.layout_topic_attachment_image, view as ViewGroup)
                return SingleImageTopicViewHolder(this, view, context, imageLoader, clickListener)
            }
            ILoadMoreSupportAdapter.ITEM_VIEW_TYPE_LOAD_INDICATOR -> {
                val view = mInflater.inflate(R.layout.card_item_load_indicator, parent, false)
                return LoadIndicatorViewHolder(view)
            }
        }
        throw UnsupportedOperationException("Unknown viewType " + viewType)
    }

    override fun getItemViewType(position: Int): Int {
        if (position == topicsCount) return ILoadMoreSupportAdapter.ITEM_VIEW_TYPE_LOAD_INDICATOR
        val topic = getTopic(position)
        val kind = topic.kind
        if (Topic.Kind.IMAGE == kind) {
            if (topic.attachments.size > 1) {
                return ITEM_VIEW_TYPE_MEDIA_GALLERY
            } else {
                return ITEM_VIEW_TYPE_SINGLE_IMAGE
            }
        } else if (Topic.Kind.GITHUB == kind) {
            return ITEM_VIEW_TYPE_GITHUB
        } else if (Topic.Kind.DRIBBBLE == kind) {
            return ITEM_VIEW_TYPE_DRIBBBLE
        } else if (Topic.Kind.LOCATION == kind) {
            return ITEM_VIEW_TYPE_LOCATION
        }
        return ITEM_VIEW_TYPE_BASIC
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            ILoadMoreSupportAdapter.ITEM_VIEW_TYPE_LOAD_INDICATOR -> {
            }
            ITEM_VIEW_TYPE_BASIC, ITEM_VIEW_TYPE_MEDIA_GALLERY, ITEM_VIEW_TYPE_GITHUB, ITEM_VIEW_TYPE_DRIBBBLE, ITEM_VIEW_TYPE_LOCATION, ITEM_VIEW_TYPE_SINGLE_IMAGE -> {
                val topicViewHolder = holder as TopicViewHolder
                topicViewHolder.displayTopic(topics!![position])
            }
        }
    }

    override fun getItemCount(): Int {
        val position = loadMoreIndicatorPosition
        var count = 0
        if (position and ILoadMoreSupportAdapter.IndicatorPosition.START != 0) {
            count += 1
        }
        count += topicsCount
        if (position and ILoadMoreSupportAdapter.IndicatorPosition.END != 0) {
            count += 1
        }
        return count
    }

    val topicsCount: Int
        get() {
            if (topics == null) return 0
            return topics!!.size
        }


    fun getTopic(position: Int): Topic {
        return topics!![position]
    }

    interface TopicClickListener : ItemClickListener {
        fun onSkillClick(position: Int, holder: TopicViewHolder)

        fun onUserClick(position: Int, holder: TopicViewHolder)

        fun onMediaClick(attachments: Array<Attachment>, attachment: Attachment, clickedView: View)
    }

    companion object {
        private val ITEM_VIEW_TYPE_BASIC = 1
        private val ITEM_VIEW_TYPE_MEDIA_GALLERY = 2
        private val ITEM_VIEW_TYPE_GITHUB = 3
        private val ITEM_VIEW_TYPE_DRIBBBLE = 4
        private val ITEM_VIEW_TYPE_LOCATION = 5
        private val ITEM_VIEW_TYPE_SINGLE_IMAGE = 6
    }
}
