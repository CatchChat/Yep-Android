/*
 * Copyright (c) 2015. Catch Inc,
 */

package catchla.yep.view.holder

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import catchla.yep.R
import catchla.yep.adapter.BaseRecyclerViewAdapter
import catchla.yep.model.Skill
import catchla.yep.model.User
import catchla.yep.util.Utils
import org.apache.commons.lang3.ObjectUtils
import org.apmem.tools.layouts.FlowLayout

/**
 * Created by mariotaku on 15/4/29.
 */
class FriendGridViewHolder(
        itemView: View,
        private val adapter: BaseRecyclerViewAdapter<RecyclerView.ViewHolder>,
        private val listener: ((Int, RecyclerView.ViewHolder) -> Unit)?,
        private val skillClickListener: ((Int, Skill, FriendGridViewHolder) -> Unit)?
) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

    private val profileImageView: ImageView
    private val nameView: TextView
    private val descriptionView: TextView
    private val userSkills: FlowLayout

    init {
        profileImageView = itemView.findViewById(R.id.profile_image) as ImageView
        nameView = itemView.findViewById(R.id.name) as TextView
        descriptionView = itemView.findViewById(R.id.description) as TextView
        userSkills = itemView.findViewById(R.id.user_skills) as FlowLayout
        itemView.findViewById(R.id.item_content).setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.item_content -> {
                listener?.invoke(adapterPosition, this)
            }
            R.id.skill_button -> {
                val skill = v.tag as Skill
                skillClickListener?.invoke(adapterPosition, skill, this)
            }
        }
    }

    fun displayUser(user: User) {
        val imageLoader = adapter.imageLoader
        val avatarUrl = user.avatarThumbUrl
        if (ObjectUtils.notEqual(avatarUrl, profileImageView.tag) || profileImageView.drawable == null) {
            imageLoader.displayProfileImage(avatarUrl, profileImageView)
        }
        profileImageView.tag = avatarUrl
        nameView.text = user.nickname
        descriptionView.text = user.introduction
        descriptionView.visibility = if (descriptionView.length() > 0) View.VISIBLE else View.GONE
        userSkills.removeAllViews()
        val skills = user.masterSkills
        val inflater = LayoutInflater.from(adapter.context)
        for (i in 0..Math.min(3, skills?.size ?: 0) - 1) {
            val skill = skills!![i]
            val textView = inflater.inflate(R.layout.layout_friend_grid_skill,
                    userSkills, false) as TextView
            textView.tag = skill
            textView.text = Utils.getDisplayName(skill)
            textView.setOnClickListener(this)
            userSkills.addView(textView)
        }
        userSkills.visibility = if (userSkills.childCount > 0) View.VISIBLE else View.GONE
    }

}
