package catchla.yep.view.holder

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import catchla.yep.adapter.TopicsAdapter
import catchla.yep.model.User
import catchla.yep.util.ImageLoaderWrapper
import kotlinx.android.synthetic.main.list_item_skill_topic_related_users.view.*

class SkillTopicRelatedUsersViewHolder(
        val topicsAdapter: TopicsAdapter,
        itemView: View,
        val context: Context,
        val imageLoader: ImageLoaderWrapper,
        val clickListener: TopicsAdapter.TopicClickListener?
) : RecyclerView.ViewHolder(itemView) {
    private val profileImages: Array<ImageView>

    init {
        profileImages = arrayOf(itemView.profileImage0, itemView.profileImage1, itemView.profileImage2)
        itemView.setOnClickListener {
            clickListener?.onRelatedUsersClick(layoutPosition, this)
        }
    }

    fun display(users: List<User>) {
        profileImages.forEachIndexed { index, view ->
            if (index < users.size) {
                val image = users[index].avatarThumbUrl
                if (image != null) {
                    imageLoader.displayProfileImage(image, view)
                } else {
                    view.setImageDrawable(null)
                }
                view.visibility = View.VISIBLE
            } else {
                view.visibility = View.GONE
            }
        }
    }

}