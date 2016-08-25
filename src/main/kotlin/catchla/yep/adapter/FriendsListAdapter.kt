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
import catchla.yep.adapter.iface.ILoadMoreSupportAdapter.ITEM_VIEW_TYPE_LOAD_INDICATOR
import catchla.yep.adapter.iface.ILoadMoreSupportAdapter.IndicatorPosition
import catchla.yep.model.Friendship
import catchla.yep.view.holder.FriendViewHolder
import catchla.yep.view.holder.LoadIndicatorViewHolder
import catchla.yep.view.holder.TopicSearchBoxViewHolder

/**
 * Created by mariotaku on 15/4/29.
 */
class FriendsListAdapter(context: Context) : LoadMoreSupportAdapter<RecyclerView.ViewHolder>(context),
        IItemCountsAdapter {
    private val inflater: LayoutInflater

    var friendships: List<Friendship>? = null
        set(value) {
            field = value
            notifyDataSetChanged()
        }


    var showSearchBox: Boolean = true
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override val itemCounts = IntArray(4)

    var itemClickListener: ((Int, RecyclerView.ViewHolder) -> Unit)? = null
    var searchBoxClickListener: ((TopicSearchBoxViewHolder) -> Unit)? = null

    val friendshipsCount: Int
        get() = friendships?.size ?: 0

    init {
        inflater = LayoutInflater.from(context)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            ITEM_VIEW_TYPE_LOAD_INDICATOR -> {
                val view = inflater.inflate(R.layout.card_item_load_indicator, parent, false)
                return LoadIndicatorViewHolder(view)
            }
            ITEM_VIEW_TYPE_SEARCH_BOX -> {
                val view = inflater.inflate(R.layout.list_item_search_box, parent, false)
                return TopicSearchBoxViewHolder(view, context.getString(R.string.search_friends), searchBoxClickListener)
            }
            ITEM_VIEW_TYPE_FRIEND -> {
                val view = inflater.inflate(R.layout.list_item_friend, parent, false)
                return FriendViewHolder(view, this, listener = itemClickListener)
            }
        }
        throw AssertionError()
    }

    override fun getItemViewType(position: Int): Int {
        when (getItemCountIndex(position)) {
            0, 3 -> return ITEM_VIEW_TYPE_LOAD_INDICATOR
            1 -> return ITEM_VIEW_TYPE_SEARCH_BOX
            2 -> return ITEM_VIEW_TYPE_FRIEND
        }
        throw AssertionError()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            ITEM_VIEW_TYPE_FRIEND -> {
                val chatEntryViewHolder = holder as FriendViewHolder
                chatEntryViewHolder.displayFriendship(getFriendship(position))
            }
        }
    }

    override fun getItemCount(): Int {
        val position = loadMoreIndicatorPosition
        itemCounts[0] = if (position and IndicatorPosition.START != 0) 1 else 0
        itemCounts[1] = if (showSearchBox) 1 else 0
        itemCounts[2] = friendshipsCount
        itemCounts[3] = if (position and IndicatorPosition.END != 0) 1 else 0
        return itemCounts.sum()
    }

    fun getFriendship(position: Int): Friendship {
        return friendships!![position - getItemStartPosition(2)]
    }


    companion object {


        private val ITEM_VIEW_TYPE_FRIEND = 1

        private val ITEM_VIEW_TYPE_SEARCH_BOX = 10
    }
}
