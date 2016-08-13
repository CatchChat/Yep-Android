/*
 * Copyright (c) 2015. Catch Inc,
 */

package catchla.yep.view.holder

import android.support.v7.widget.RecyclerView
import android.text.format.DateUtils
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import catchla.yep.R
import catchla.yep.adapter.BaseRecyclerViewAdapter
import catchla.yep.model.Friendship
import catchla.yep.model.User

/**
 * Created by mariotaku on 15/4/29.
 */
class FriendViewHolder(
        itemView: View,
        val adapter: BaseRecyclerViewAdapter<RecyclerView.ViewHolder>,
        val listener: ((Int, RecyclerView.ViewHolder) -> Unit)?
) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

    private val profileImageView: ImageView
    private val nameView: TextView
    private val timeView: TextView
    private val descriptionView: TextView
    private val badgeView: ImageView

    init {
        profileImageView = itemView.findViewById(R.id.profileImage) as ImageView
        nameView = itemView.findViewById(R.id.name) as TextView
        timeView = itemView.findViewById(R.id.update_time) as TextView
        descriptionView = itemView.findViewById(R.id.description) as TextView
        badgeView = itemView.findViewById(R.id.badge) as ImageView
        itemView.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        listener?.invoke(adapterPosition, this@FriendViewHolder)
    }

    fun displaySample(profileImage: Int, name: String, time: String, message: String) {
        profileImageView.setImageResource(profileImage)
        nameView.text = name
        timeView.text = time
        descriptionView.text = message
    }

    fun displayUser(user: User) {
        val imageLoader = adapter.imageLoader
        if (user.avatarThumbUrl != profileImageView.tag || profileImageView.drawable == null) {
            imageLoader.displayProfileImage(user.avatarThumbUrl, profileImageView)
        }
        profileImageView.tag = user.avatarThumbUrl
        nameView.text = user.nickname
        //        timeView.setText();
        descriptionView.text = user.introduction
        badgeView.setImageResource(user.badge?.icon ?: 0)
        if (user.updatedAt != null) {
            timeView.visibility = View.VISIBLE
            timeView.text = adapter.context.getString(R.string.last_seen_time,
                    DateUtils.getRelativeTimeSpanString(user.updatedAt.time))
        } else {
            timeView.visibility = View.GONE
        }
    }

    fun displayFriendship(friendship: Friendship) {
        displayUser(friendship.friend)
    }
}
