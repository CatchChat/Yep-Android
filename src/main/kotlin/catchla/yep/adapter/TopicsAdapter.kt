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
import catchla.yep.adapter.iface.IItemCountsAdapter
import catchla.yep.adapter.iface.ILoadMoreSupportAdapter
import catchla.yep.adapter.iface.ILoadMoreSupportAdapter.*
import catchla.yep.adapter.iface.ItemClickListener
import catchla.yep.model.Attachment
import catchla.yep.model.Topic
import catchla.yep.model.User
import catchla.yep.view.holder.*
import org.mariotaku.ktextension.nullOrEmpty

/**
 * Created by mariotaku on 15/4/29.
 */
class TopicsAdapter(context: Context) : LoadMoreSupportAdapter<RecyclerView.ViewHolder>(context),
        IItemCountsAdapter {

    private val inflater: LayoutInflater

    var showSkillLabel: Boolean = true

    var clickListener: TopicClickListener? = null
        set

    var relatedUsers: List<User>? = null
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var showSearchBox: Boolean = false
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override val itemCounts = IntArray(5)

    var topics: List<Topic>? = null
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    init {
        inflater = LayoutInflater.from(context)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            ITEM_VIEW_TYPE_MEDIA_GALLERY -> {
                val view = inflater.inflate(R.layout.list_item_topic, parent, false)
                inflater.inflate(R.layout.layout_topic_attachment_gallery, view as ViewGroup)
                return GalleryTopicViewHolder(this, view, context, imageLoader, clickListener)
            }
            ITEM_VIEW_TYPE_GITHUB -> {
                val view = inflater.inflate(R.layout.list_item_topic, parent, false)
                inflater.inflate(R.layout.layout_topic_attachment_github, view as ViewGroup)
                return GithubTopicViewHolder(this, view, context, imageLoader, clickListener)
            }
            ITEM_VIEW_TYPE_DRIBBBLE -> {
                val view = inflater.inflate(R.layout.list_item_topic, parent, false)
                inflater.inflate(R.layout.layout_topic_attachment_dribbble, view as ViewGroup)
                return DribbbleTopicViewHolder(this, view, context, imageLoader, clickListener)
            }
            ITEM_VIEW_TYPE_LOCATION -> {
                val view = inflater.inflate(R.layout.list_item_topic, parent, false)
                inflater.inflate(R.layout.layout_topic_attachment_location, view as ViewGroup)
                return LocationTopicViewHolder(this, view, context, imageLoader, clickListener)
            }
            ITEM_VIEW_TYPE_BASIC -> {
                val view = inflater.inflate(R.layout.list_item_topic, parent, false)
                inflater.inflate(R.layout.layout_topic_attachment_null, view as ViewGroup)
                return TopicViewHolder(this, view, context, imageLoader, clickListener)
            }
            ITEM_VIEW_TYPE_SINGLE_IMAGE -> {
                val view = inflater.inflate(R.layout.list_item_topic, parent, false)
                inflater.inflate(R.layout.layout_topic_attachment_image, view as ViewGroup)
                return SingleImageTopicViewHolder(this, view, context, imageLoader, clickListener)
            }
            ITEM_VIEW_TYPE_WEB_PAGE -> {
                val view = inflater.inflate(R.layout.list_item_topic, parent, false)
                inflater.inflate(R.layout.layout_topic_attachment_webpage, view as ViewGroup)
                return WebPageTopicViewHolder(this, view, context, imageLoader, clickListener)
            }
            ITEM_VIEW_TYPE_RELATED_USERS -> {
                val view = inflater.inflate(R.layout.list_item_skill_topic_related_users, parent, false)
                return SkillTopicRelatedUsersViewHolder(this, view, context, imageLoader, clickListener)
            }
            ITEM_VIEW_TYPE_SEARCH_BOX -> {
                val view = inflater.inflate(R.layout.list_item_search_box, parent, false)
                return TopicSearchBoxViewHolder(view, context.getString(R.string.search_topics), null)
            }
            ITEM_VIEW_TYPE_LOAD_INDICATOR -> {
                val view = inflater.inflate(R.layout.card_item_load_indicator, parent, false)
                return LoadIndicatorViewHolder(view)
            }
        }
        throw UnsupportedOperationException("Unknown viewType " + viewType)
    }

    override fun getItemViewType(position: Int): Int {
        when (getItemCountIndex(position)) {
            0, 4 -> return ITEM_VIEW_TYPE_LOAD_INDICATOR
            1 -> return ITEM_VIEW_TYPE_SEARCH_BOX
            2 -> return ITEM_VIEW_TYPE_RELATED_USERS
            3 -> {
                val topic = getTopic(position)
                val kind = topic.kind
                when (kind) {
                    Topic.Kind.IMAGE -> if (topic.attachments.size > 1) {
                        return ITEM_VIEW_TYPE_MEDIA_GALLERY
                    } else {
                        return ITEM_VIEW_TYPE_SINGLE_IMAGE
                    }
                    Topic.Kind.GITHUB -> return ITEM_VIEW_TYPE_GITHUB
                    Topic.Kind.DRIBBBLE -> return ITEM_VIEW_TYPE_DRIBBBLE
                    Topic.Kind.LOCATION -> return ITEM_VIEW_TYPE_LOCATION
                    Topic.Kind.WEB_PAGE -> return ITEM_VIEW_TYPE_WEB_PAGE
                    else -> return ITEM_VIEW_TYPE_BASIC
                }
            }
        }
        throw AssertionError()
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            ITEM_VIEW_TYPE_LOAD_INDICATOR -> {
            }
            ITEM_VIEW_TYPE_BASIC, ITEM_VIEW_TYPE_MEDIA_GALLERY, ITEM_VIEW_TYPE_GITHUB,
            ITEM_VIEW_TYPE_DRIBBBLE, ITEM_VIEW_TYPE_LOCATION, ITEM_VIEW_TYPE_SINGLE_IMAGE,
            ITEM_VIEW_TYPE_WEB_PAGE -> {
                val topicViewHolder = holder as TopicViewHolder
                topicViewHolder.displayTopic(getTopic(position))
            }
            ITEM_VIEW_TYPE_RELATED_USERS -> {
                (holder as SkillTopicRelatedUsersViewHolder).display(relatedUsers!!)
            }
        }
    }

    override fun getItemCount(): Int {
        val position = loadMoreIndicatorPosition
        itemCounts[0] = if (position and IndicatorPosition.START != 0) 1 else 0
        itemCounts[1] = if (showSearchBox) 1 else 0
        itemCounts[2] = if (!topics.nullOrEmpty() && !relatedUsers.nullOrEmpty()) 1 else 0
        itemCounts[3] = topicsCount
        itemCounts[4] = if (position and IndicatorPosition.END != 0) 1 else 0
        return itemCounts.sum()
    }
    val topicsCount: Int
        get() = topics?.size ?: 0

    fun getTopic(position: Int): Topic {
        return topics!![position - itemCounts[0] - itemCounts[1]]
    }

    interface TopicClickListener : ItemClickListener {
        fun onRelatedUsersClick(position: Int, holder: SkillTopicRelatedUsersViewHolder)

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
        private val ITEM_VIEW_TYPE_WEB_PAGE = 7

        private val ITEM_VIEW_TYPE_SEARCH_BOX = 10
        private val ITEM_VIEW_TYPE_RELATED_USERS = 11
    }

}

