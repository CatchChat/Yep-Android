package catchla.yep.view.holder

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import catchla.yep.R
import catchla.yep.fragment.ChatListFragment
import catchla.yep.model.Message
import catchla.yep.provider.YepDataStore
import catchla.yep.util.support.view.ViewOutlineProviderCompat
import catchla.yep.util.view.ViewSupport
import org.mariotaku.messagebubbleview.library.MessageBubbleView

open class MessageViewHolder(
        itemView: View,
        outgoing: Boolean,
        protected val adapter: ChatListFragment.ChatAdapter
) : RecyclerView.ViewHolder(itemView) {

    protected val profileImageView by lazy { itemView.findViewById(R.id.profileImage) as ImageView? }
    protected val profileImageViewOutgoing by lazy { itemView.findViewById(R.id.profileImageOutgoing) as ImageView? }
    protected val messageBubbleView by lazy { itemView.findViewById(R.id.messageBubble) as MessageBubbleView }
    protected val stateView by lazy { itemView.findViewById(R.id.state) as ImageView? }
    protected val textContentView by lazy { itemView.findViewById(R.id.textContent) as TextView }

    init {
        stateView?.setOnClickListener { adapter.notifyStateClicked(layoutPosition) }
        messageBubbleView.isOutlineEnabled = true
        ViewSupport.setOutlineProvider(messageBubbleView, ViewOutlineProviderCompat.BACKGROUND)
        ViewSupport.setClipToOutline(messageBubbleView, true)
    }

    open fun displayMessage(message: Message) {
        textContentView.text = message.textContent
        textContentView.visibility = if (textContentView.length() > 0) View.VISIBLE else View.GONE
        profileImageView?.let {
            val sender = message.sender
            if (sender.avatarThumbUrl != it.tag || it.drawable == null) {
                adapter.imageLoader.displayProfileImage(sender.avatarThumbUrl, it)
            }
            it.tag = sender.avatarThumbUrl
        }
        profileImageViewOutgoing?.let {
            val sender = message.sender
            if (sender.avatarThumbUrl != it.tag || it.drawable == null) {
                adapter.imageLoader.displayProfileImage(sender.avatarThumbUrl, it)
            }
            it.tag = sender.avatarThumbUrl
        }
        when (message.state) {
            YepDataStore.Messages.MessageState.READ -> {
                stateView?.setImageDrawable(null)
            }
            YepDataStore.Messages.MessageState.FAILED -> {
                stateView?.setImageResource(R.drawable.ic_message_state_retry)
            }
            YepDataStore.Messages.MessageState.UNREAD -> {
                stateView?.setImageResource(R.drawable.ic_message_state_unread)
            }
            else -> {
                stateView?.setImageDrawable(null)
            }
        }
    }

    open fun onRecycled() {

    }
}