/*
 * Copyright (c) 2015. Catch Inc,
 */

package catchla.yep.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import catchla.yep.R
import catchla.yep.adapter.iface.ILoadMoreSupportAdapter
import catchla.yep.adapter.iface.ItemClickListener
import catchla.yep.model.Friendship
import catchla.yep.view.holder.FriendViewHolder

/**
 * Created by mariotaku on 15/4/29.
 */
class FriendsListAdapter(context: Context) : LoadMoreSupportAdapter<RecyclerView.ViewHolder>(context) {
    private val mInflater: LayoutInflater

    var friendships: List<Friendship>? = null
        set(value) {
            field = value
            notifyDataSetChanged()
        }
    var itemClickListener: ItemClickListener? = null
        set
    private val mInternalItemClickListener = ItemClickListener { position, holder ->
        if (itemClickListener != null) {
            itemClickListener!!.onItemClick(position, holder)
        }
    }

    init {
        mInflater = LayoutInflater.from(context)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = mInflater.inflate(R.layout.list_item_friend, parent, false)
        return FriendViewHolder(view, this, mInternalItemClickListener)
    }

    override fun getItemViewType(position: Int): Int {
        return ITEM_VIEW_TYPE_CHAT_ENTRY
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            ITEM_VIEW_TYPE_CHAT_ENTRY -> {
                val chatEntryViewHolder = holder as FriendViewHolder
                chatEntryViewHolder.displayFriendship(friendships!![position])
            }
        }
    }

    val friendshipsCount: Int
        get() {
            if (friendships == null) return 0
            return friendships!!.size
        }

    override fun getItemCount(): Int {
        val position = loadMoreIndicatorPosition
        var count = 0
        if (position and ILoadMoreSupportAdapter.IndicatorPosition.START != 0) {
            count += 1
        }
        count += friendshipsCount
        if (position and ILoadMoreSupportAdapter.IndicatorPosition.END != 0) {
            count += 1
        }
        return count
    }

    fun getFriendship(position: Int): Friendship? {
        if (friendships == null) return null
        return friendships!![position]
    }


    companion object {


        private val ITEM_VIEW_TYPE_CHAT_ENTRY = 1
    }
}
