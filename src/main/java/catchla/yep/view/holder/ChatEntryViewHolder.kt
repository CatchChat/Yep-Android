/*
 * Copyright (c) 2015. Catch Inc,
 */

package catchla.yep.view.holder

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView

import catchla.yep.R
import catchla.yep.adapter.ChatsListAdapter
import catchla.yep.adapter.iface.ItemClickListener
import catchla.yep.model.Conversation
import catchla.yep.model.Message
import catchla.yep.util.ImageLoaderWrapper
import catchla.yep.util.Utils
import catchla.yep.view.ShortTimeView

/**
 * Created by mariotaku on 15/4/29.
 */
class ChatEntryViewHolder(itemView: View, private val adapter: ChatsListAdapter, listener: ItemClickListener?) : RecyclerView.ViewHolder(itemView) {

    private val profileImageView: ImageView
    private val nameView: TextView
    private val timeView: ShortTimeView
    private val messageView: TextView

    init {
        profileImageView = itemView.findViewById(R.id.profile_image) as ImageView

        nameView = itemView.findViewById(R.id.name) as TextView
        timeView = itemView.findViewById(R.id.update_time) as ShortTimeView
        messageView = itemView.findViewById(R.id.message) as TextView
        itemView.setOnClickListener(View.OnClickListener {
            if (listener == null) return@OnClickListener
            listener.onItemClick(adapterPosition, this@ChatEntryViewHolder)
        })
    }

    fun displayConversation(conversation: Conversation) {
        if (!conversation.isValid) return
        nameView.text = Utils.getConversationName(conversation)
        val imageLoader = adapter.imageLoader
        imageLoader.displayProfileImage(Utils.getConversationAvatarUrl(conversation), profileImageView)
        messageView.text = getConversationSummary(adapter.context, conversation)
        timeView.setTime(Utils.getTime(conversation.updatedAt))
    }

    fun displayCirclesEntry(conversation: Conversation) {
        nameView.setText(R.string.circles)
        messageView.text = getConversationSummary(adapter.context, conversation)
        timeView.setTime(Utils.getTime(conversation.updatedAt))
    }

    private fun getConversationSummary(context: Context, conversation: Conversation): String {
        if (Message.MediaType.LOCATION.equals(conversation.mediaType, ignoreCase = true)) {
            return context.getString(R.string.location)
        } else if (Message.MediaType.IMAGE.equals(conversation.mediaType, ignoreCase = true)) {
            return context.getString(R.string.image)
        } else if (Message.MediaType.AUDIO.equals(conversation.mediaType, ignoreCase = true)) {
            return context.getString(R.string.audio)
        } else {
            return conversation.textContent
        }
    }
}
