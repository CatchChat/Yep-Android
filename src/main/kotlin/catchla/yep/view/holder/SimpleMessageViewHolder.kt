package catchla.yep.view.holder

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import catchla.yep.adapter.iface.IBaseRecyclerViewAdapter
import catchla.yep.model.Message
import catchla.yep.util.Utils
import catchla.yep.util.highlightedString
import catchla.yep.view.ShortTimeView
import com.pkmmte.view.CircularImageView
import kotlinx.android.synthetic.main.list_item_message_simple.view.*

/**
 * Created by mariotaku on 16/8/25.
 */
class SimpleMessageViewHolder(
        itemView: View,
        val adapter: IBaseRecyclerViewAdapter,
        listener: ((Int, SimpleMessageViewHolder) -> Unit)?
) : RecyclerView.ViewHolder(itemView) {

    val nameView: TextView by lazy { itemView.name }
    val messageView: TextView by lazy { itemView.message }
    val timeView: ShortTimeView by lazy { itemView.time }
    val profileImageView: CircularImageView by lazy { itemView.profileImage }

    init {
        itemView.setOnClickListener {
            listener?.invoke(layoutPosition, this)
        }
    }

    fun display(message: Message, highlight: String? = null) {
        adapter.imageLoader.displayProfileImage(message.sender.avatarThumbUrl, profileImageView)
        nameView.text = Utils.getDisplayName(message.sender)
        messageView.text = message.textContent?.highlightedString(highlight)
        if (message.createdAt != null) {
            timeView.time = message.createdAt.time
            timeView.visibility = View.VISIBLE
        } else {
            timeView.visibility = View.GONE
        }

    }

}