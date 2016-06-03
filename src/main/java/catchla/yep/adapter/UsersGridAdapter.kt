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
import catchla.yep.model.Skill
import catchla.yep.view.holder.FriendGridViewHolder

/**
 * Created by mariotaku on 15/4/29.
 */
class UsersGridAdapter(context: Context) : UsersAdapter(context) {

    private val mInflater: LayoutInflater

    init {
        mInflater = LayoutInflater.from(context)
    }

    override fun onCreateFriendViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val view = mInflater.inflate(R.layout.grid_item_friend, parent, false)
        return FriendGridViewHolder(view, this, clickListener as UserGridItemClickListener)
    }

    override fun setItemClickListener(listener: ItemClickListener) {
        super.setItemClickListener(listener)
    }

    override fun bindFriendViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as FriendGridViewHolder).displayUser(getUser(position))
    }

    interface UserGridItemClickListener : ItemClickListener {

        fun onSkillClick(position: Int, skill: Skill, holder: FriendGridViewHolder)
    }

}
