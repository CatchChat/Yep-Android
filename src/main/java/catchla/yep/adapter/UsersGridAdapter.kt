/*
 * Copyright (c) 2015. Catch Inc,
 */

package catchla.yep.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import catchla.yep.R
import catchla.yep.adapter.iface.ItemClickListener
import catchla.yep.model.Skill
import catchla.yep.view.holder.FriendGridViewHolder

/**
 * Created by mariotaku on 15/4/29.
 */
class UsersGridAdapter(context: Context) : UsersAdapter(context) {

    var skillClickListener: ((Int, Skill, FriendGridViewHolder) -> Unit)? = null

    override fun onCreateFriendViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val view = inflater.inflate(R.layout.grid_item_friend, parent, false)
        return FriendGridViewHolder(view, this, itemClickListener, skillClickListener)
    }

    override fun bindFriendViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as FriendGridViewHolder).displayUser(getUser(position))
    }


}
