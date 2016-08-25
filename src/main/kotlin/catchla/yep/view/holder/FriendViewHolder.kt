/*
 * Copyright (c) 2015. Catch Inc,
 */

package catchla.yep.view.holder

import android.support.v7.widget.RecyclerView
import android.text.TextPaint
import android.text.style.CharacterStyle
import android.view.View
import catchla.yep.adapter.iface.IBaseRecyclerViewAdapter
import catchla.yep.model.Friendship
import catchla.yep.model.User
import catchla.yep.util.highlightedString
import kotlinx.android.synthetic.main.list_item_friend.view.*

/**
 * Created by mariotaku on 15/4/29.
 */
class FriendViewHolder(
        itemView: View,
        val adapter: IBaseRecyclerViewAdapter,
        val listener: ((Int, RecyclerView.ViewHolder) -> Unit)?,
        displayUsername: Boolean = false,
        val displayLastSeen: Boolean = true,
        val displayBadge: Boolean = true
) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

    var dataPosition: Int = RecyclerView.NO_POSITION

    private val profileImageView by lazy { itemView.profileImage }
    private val nameView by lazy { itemView.name }
    private val usernameView by lazy { itemView.username }
    private val timeView by lazy { itemView.updateTime }
    private val descriptionView by lazy { itemView.description }
    private val badgeView by lazy { itemView.badge }

    init {
        itemView.setOnClickListener(this)
        usernameView.visibility = if (displayUsername) View.VISIBLE else View.GONE
        timeView.visibility = if (displayLastSeen) View.VISIBLE else View.GONE
        badgeView.visibility = if (displayBadge) View.VISIBLE else View.GONE
    }

    override fun onClick(v: View) {
        listener?.invoke(adapterPosition, this@FriendViewHolder)
    }

    fun displayUser(user: User, highlight: String? = null) {
        val imageLoader = adapter.imageLoader
        if (user.avatarThumbUrl != profileImageView.tag || profileImageView.drawable == null) {
            imageLoader.displayProfileImage(user.avatarThumbUrl, profileImageView)
        }
        profileImageView.tag = user.avatarThumbUrl
        nameView.text = user.nickname?.highlightedString(highlight)
        usernameView.text = user.displayUsername?.highlightedString(highlight)
        descriptionView.text = user.introduction?.highlightedString(highlight)
        if (displayBadge && user.badge != null) {
            badgeView.setImageResource(user.badge!!.icon)
            badgeView.visibility = View.VISIBLE
        } else {
            badgeView.visibility = View.GONE
        }
        if (displayLastSeen && user.updatedAt != null) {
            timeView.visibility = View.VISIBLE
            timeView.time = user.updatedAt.time
        } else {
            timeView.visibility = View.GONE
        }
    }

    fun displayFriendship(friendship: Friendship, highlight: String? = null) {
        displayUser(friendship.friend, highlight)
    }

    object HighlightSpan : CharacterStyle() {
        override fun updateDrawState(textPaint: TextPaint) {
            textPaint.color = textPaint.linkColor
        }

    }
}
