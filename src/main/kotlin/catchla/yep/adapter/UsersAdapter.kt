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
import catchla.yep.model.User
import catchla.yep.view.holder.FriendViewHolder
import catchla.yep.view.holder.LoadIndicatorViewHolder

/**
 * Created by mariotaku on 15/4/29.
 */
open class UsersAdapter(context: Context) : LoadMoreSupportAdapter<RecyclerView.ViewHolder>(context) {

    protected val inflater: LayoutInflater
    var itemClickListener: ((position: Int, holder: RecyclerView.ViewHolder) -> Unit)? = null
        get
        set
    var users: List<User>? = null
        get
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    init {
        inflater = LayoutInflater.from(context)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            ITEM_VIEW_TYPE_USER_ITEM -> {
                return onCreateFriendViewHolder(parent)
            }
            ILoadMoreSupportAdapter.ITEM_VIEW_TYPE_LOAD_INDICATOR -> {
                val view = inflater.inflate(R.layout.card_item_load_indicator, parent, false)
                return LoadIndicatorViewHolder(view)
            }
        }
        throw UnsupportedOperationException("Unknown viewType " + viewType)
    }

    protected open fun onCreateFriendViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val view = inflater.inflate(R.layout.list_item_friend, parent, false)
        return FriendViewHolder(view, this, listener = itemClickListener)
    }

    override fun getItemViewType(position: Int): Int {
        if (position == usersCount) return ILoadMoreSupportAdapter.ITEM_VIEW_TYPE_LOAD_INDICATOR
        return ITEM_VIEW_TYPE_USER_ITEM
    }

    val usersCount: Int
        get() {
            if (users == null) return 0
            return users!!.size
        }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            ITEM_VIEW_TYPE_USER_ITEM -> {
                bindFriendViewHolder(holder, position)
            }
            ILoadMoreSupportAdapter.ITEM_VIEW_TYPE_LOAD_INDICATOR -> {
            }
        }
    }

    protected open fun bindFriendViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as FriendViewHolder).displayUser(getUser(position))
    }

    fun getUser(position: Int): User {
        return users!![position]
    }

    override fun getItemCount(): Int {
        val position = loadMoreIndicatorPosition
        var count = 0
        if (position and ILoadMoreSupportAdapter.IndicatorPosition.START != 0) {
            count += 1
        }
        count += usersCount
        if (position and ILoadMoreSupportAdapter.IndicatorPosition.END != 0) {
            count += 1
        }
        return count
    }


    companion object {
        private val ITEM_VIEW_TYPE_USER_ITEM = 1
    }
}
