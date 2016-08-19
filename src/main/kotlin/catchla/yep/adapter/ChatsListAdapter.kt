/*
 * Copyright (c) 2015. Catch Inc,
 */

package catchla.yep.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import catchla.yep.R
import catchla.yep.adapter.iface.IItemCountsAdapter
import catchla.yep.model.Conversation
import catchla.yep.model.Message
import catchla.yep.view.holder.ChatEntryViewHolder
import catchla.yep.view.holder.TopicSearchBoxViewHolder

/**
 * Created by mariotaku on 15/4/29.
 */
class ChatsListAdapter(context: Context) : LoadMoreSupportAdapter<RecyclerView.ViewHolder>(context),
        IItemCountsAdapter {

    private val inflater: LayoutInflater
    private var conversations: List<Conversation>? = null
    var itemClickListener: ((position: Int, holder: RecyclerView.ViewHolder) -> Unit)? = null
    private var circleLength: Int = 0

    var showSearchBox: Boolean = false
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override val itemCounts = IntArray(3)


    init {
        inflater = LayoutInflater.from(context)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            ITEM_VIEW_TYPE_CIRCLES_ENTRY -> {
                val view = inflater.inflate(R.layout.list_item_circles_chat_entry, parent, false)
                return ChatEntryViewHolder(view, this, itemClickListener)
            }
            ITEM_VIEW_TYPE_CHAT_ENTRY -> {
                val view = inflater.inflate(R.layout.list_item_chat_entry, parent, false)
                return ChatEntryViewHolder(view, this, itemClickListener)
            }
            ITEM_VIEW_TYPE_SEARCH_BOX -> {
                val view = inflater.inflate(R.layout.list_item_search_box, parent, false)
                return TopicSearchBoxViewHolder(view, context.getString(R.string.search), null)
            }
        }
        throw UnsupportedOperationException("Unknown viewType " + viewType)
    }

    override fun getItemViewType(position: Int): Int {
        when (getItemCountIndex(position)) {
            0 -> return ITEM_VIEW_TYPE_SEARCH_BOX
            1 -> return ITEM_VIEW_TYPE_CIRCLES_ENTRY
            2 -> return ITEM_VIEW_TYPE_CHAT_ENTRY
        }
        throw AssertionError()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            ITEM_VIEW_TYPE_CHAT_ENTRY -> {
                val chatEntryViewHolder = holder as ChatEntryViewHolder
                chatEntryViewHolder.displayConversation(getConversation(position))
            }
            ITEM_VIEW_TYPE_CIRCLES_ENTRY -> {
                val chatEntryViewHolder = holder as ChatEntryViewHolder
                chatEntryViewHolder.displayCirclesEntry(getConversation(position))
            }
        }
    }

    override fun getItemCount(): Int {
        if (conversations == null) return 0
        itemCounts[0] = if (showSearchBox) 1 else 0
        itemCounts[1] = if (circleLength > 0) 1 else 0
        itemCounts[2] = conversations!!.size - circleLength
        return itemCounts.sum()
    }

    fun setConversations(data: List<Conversation>?, circlesOnly: Boolean) {
        conversations = data
        circleLength = 0
        if (data != null && !circlesOnly) {
            circleLength = data.count { Message.RecipientType.CIRCLE == it.recipientType }
        }
        notifyDataSetChanged()
    }

    fun getConversation(position: Int): Conversation {
        when (getItemCountIndex(position)) {
            0 -> throw IllegalArgumentException()
            1 -> return getRawConversation(0)
            2 -> return getRawConversation(position - getItemStartPosition(2) + circleLength)
        }
        throw AssertionError()
    }

    fun getRawConversation(position: Int): Conversation {
        return conversations!![position]
    }

    companion object {

        val ITEM_VIEW_TYPE_CHAT_ENTRY = 1
        val ITEM_VIEW_TYPE_CIRCLES_ENTRY = 2

        val ITEM_VIEW_TYPE_SEARCH_BOX = 10
    }
}
