/*
 * Copyright (c) 2015. Catch Inc,
 */

package catchla.yep.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import catchla.yep.R
import catchla.yep.adapter.iface.ItemClickListener
import catchla.yep.model.Conversation
import catchla.yep.model.Message
import catchla.yep.view.holder.ChatEntryViewHolder

/**
 * Created by mariotaku on 15/4/29.
 */
class ChatsListAdapter(context: Context) : LoadMoreSupportAdapter<RecyclerView.ViewHolder>(context) {

    private val inflater: LayoutInflater
    private var conversations: List<Conversation>? = null
    private var itemClickListener: ItemClickListener? = null
    private var circleLength: Int = 0

    init {
        inflater = LayoutInflater.from(context)
    }

    fun setItemClickListener(mItemClickListener: ItemClickListener) {
        this.itemClickListener = mItemClickListener
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
        }
        throw UnsupportedOperationException("Unknown viewType " + viewType)
    }

    override fun getItemViewType(position: Int): Int {
        if (circleLength > 0 && position == 0) return ITEM_VIEW_TYPE_CIRCLES_ENTRY
        return ITEM_VIEW_TYPE_CHAT_ENTRY

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            ITEM_VIEW_TYPE_CHAT_ENTRY -> {
                val chatEntryViewHolder = holder as ChatEntryViewHolder
                chatEntryViewHolder.displayConversation(getConversation(position))
            }
            ITEM_VIEW_TYPE_CIRCLES_ENTRY -> {
                val chatEntryViewHolder = holder as ChatEntryViewHolder
                chatEntryViewHolder.displayCirclesEntry(getConversation(0))
            }
        }
    }

    override fun getItemCount(): Int {
        if (conversations == null) return 0
        if (circleLength == 0) return conversations!!.size
        return conversations!!.size - circleLength + 1
    }

    fun setConversations(data: List<Conversation>?, circlesOnly: Boolean) {
        conversations = data
        circleLength = 0
        if (data != null && !circlesOnly) {
            var i = 0
            val dataSize = data.size
            while (i < dataSize) {
                val conversation = data[i]
                if (Message.RecipientType.CIRCLE == conversation.recipientType) {
                    circleLength = i + 1
                } else {
                    break
                }
                i++
            }
        }
        notifyDataSetChanged()
    }

    fun getConversation(position: Int): Conversation {
        if (circleLength == 0) return getRawConversation(position)
        if (position == 0) return getRawConversation(0)
        return getRawConversation(position + circleLength - 1)
    }

    fun getRawConversation(position: Int): Conversation {
        return conversations!![position]
    }

    companion object {

        val ITEM_VIEW_TYPE_CHAT_ENTRY = 1
        val ITEM_VIEW_TYPE_CIRCLES_ENTRY = 2
    }
}
