package catchla.yep.view.holder

import android.content.Context
import android.location.Location
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import catchla.yep.R
import catchla.yep.adapter.TopicsAdapter
import catchla.yep.model.Attachment
import catchla.yep.model.Topic
import catchla.yep.util.ImageLoaderWrapper
import catchla.yep.util.Utils
import catchla.yep.util.highlightedString
import catchla.yep.view.ShortTimeView
import org.apache.commons.lang3.ObjectUtils

/**
 * Created by mariotaku on 15/10/12.
 */
open class TopicViewHolder(
        protected val adapter: TopicsAdapter,
        itemView: View,
        val context: Context,
        val imageLoader: ImageLoaderWrapper,
        protected val listener: TopicsAdapter.TopicClickListener?
) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

    private val profileImageView: ImageView
    private val providerIcon: ImageView
    private val nameView: TextView
    private val textView: TextView
    private val timeView: ShortTimeView
    private val distanceView: TextView
    private val messagesCountView: TextView
    private val currentLocation: Location?
    private val tempLocation: Location
    private val skillButton: TextView

    init {
        if (listener != null) {
            itemView.setOnClickListener(this)
        }
        profileImageView = itemView.findViewById(R.id.profileImage) as ImageView
        providerIcon = itemView.findViewById(R.id.providerIcon) as ImageView
        nameView = itemView.findViewById(R.id.name) as TextView
        textView = itemView.findViewById(R.id.text) as TextView
        timeView = itemView.findViewById(R.id.time) as ShortTimeView
        distanceView = itemView.findViewById(R.id.distance) as TextView
        messagesCountView = itemView.findViewById(R.id.messagesCount) as TextView
        skillButton = itemView.findViewById(R.id.skillButton) as TextView
        currentLocation = Utils.getCachedLocation(context)
        tempLocation = Location("")

        profileImageView.setOnClickListener(this)
        nameView.setOnClickListener(this)
        skillButton.setOnClickListener(this)
    }

    open fun displayTopic(topic: Topic, highlight: String?) {
        val user = topic.user
        val avatarUrl = user.avatarThumbUrl
        if (ObjectUtils.notEqual(avatarUrl, profileImageView.tag)) {
            imageLoader.displayProfileImage(avatarUrl, profileImageView)
        }
        profileImageView.tag = avatarUrl
        nameView.text = Utils.getDisplayName(user)
        textView.text = topic.body?.highlightedString(highlight)
        val createdAt = topic.createdAt
        if (createdAt != null) {
            timeView.time = createdAt.time
        }
        val userLocation = topic.user.location
        if (currentLocation != null && userLocation != null) {
            distanceView.visibility = View.VISIBLE
            tempLocation.latitude = userLocation.latitude
            tempLocation.longitude = userLocation.longitude
            distanceView.text = Utils.getDistanceString(currentLocation.distanceTo(tempLocation))
        } else {
            distanceView.visibility = View.GONE
        }
        messagesCountView.text = if (topic.messageCount > 0) topic.messageCount.toString() else null
        val skill = topic.skill
        if (skill != null && adapter.showSkillLabel) {
            skillButton.text = Utils.getDisplayName(skill)
            skillButton.visibility = View.VISIBLE
        } else {
            skillButton.text = null
            skillButton.visibility = View.GONE
        }

        when (topic.kind) {
            Attachment.Kind.GITHUB -> {
                providerIcon.setImageResource(R.drawable.ic_provider_github)
                providerIcon.setColorFilter(ContextCompat.getColor(providerIcon.context,
                        R.color.color_github))
            }
            Attachment.Kind.DRIBBBLE -> {
                providerIcon.setImageResource(R.drawable.ic_provider_dribbble)
                providerIcon.setColorFilter(ContextCompat.getColor(providerIcon.context,
                        R.color.color_dribbble))
            }
            else -> providerIcon.setImageDrawable(null)
        }

    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.item_content -> {
                listener!!.onItemClick(layoutPosition, this)
            }
            R.id.skillButton -> {
                listener!!.onSkillClick(layoutPosition, this)
            }
            R.id.profileImage, R.id.name -> {
                run { listener!!.onUserClick(layoutPosition, this) }
            }
            R.id.attachmentView -> {
                onAttachmentClick()
            }
        }
    }

    open fun onRecycled() {

    }

    protected open fun onAttachmentClick() {

    }

    fun setReplyButtonVisible(visible: Boolean) {
        messagesCountView.visibility = if (visible) View.VISIBLE else View.INVISIBLE
    }

}
