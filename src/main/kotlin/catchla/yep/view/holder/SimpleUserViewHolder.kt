package catchla.yep.view.holder

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import catchla.yep.R
import catchla.yep.adapter.iface.IBaseRecyclerViewAdapter
import catchla.yep.model.User
import catchla.yep.util.highlightedString
import catchla.yep.view.ShortTimeView
import com.pkmmte.view.CircularImageView
import kotlinx.android.synthetic.main.list_item_user_simple.view.*
import org.oshkimaadziig.george.androidutils.SpanFormatter

/**
 * Created by mariotaku on 16/8/25.
 */
class SimpleUserViewHolder(
        itemView: View,
        val adapter: IBaseRecyclerViewAdapter,
        val displayLastSeen: Boolean = false,
        listener: ((Int, SimpleUserViewHolder) -> Unit)?
) : RecyclerView.ViewHolder(itemView) {

    var dataPosition: Int = RecyclerView.NO_POSITION

    val nameView: TextView by lazy { itemView.name }
    val usernameView: TextView by lazy { itemView.username }
    val summaryView: TextView by lazy { itemView.summary }
    val lastSeenView: ShortTimeView by lazy { itemView.lastSeen }
    val profileImageView: CircularImageView by lazy { itemView.profileImage }

    init {
        itemView.setOnClickListener {
            listener?.invoke(layoutPosition, this)
        }
    }

    fun display(user: User, highlight: String? = null) {
        adapter.imageLoader.displayProfileImage(user.avatarThumbUrl, profileImageView)
        nameView.text = user.nickname?.highlightedString(highlight)
        usernameView.text = user.displayUsername?.highlightedString(highlight)
        if (highlight != null && user.contactName?.contains(highlight, false) ?: false) {
            summaryView.text = SpanFormatter.format(itemView.context.getString(R.string.name_in_contacts),
                    user.contactName?.highlightedString(highlight))
            summaryView.visibility = View.VISIBLE
        } else {
            summaryView.visibility = View.GONE
        }
        if (displayLastSeen && user.createdAt != null) {
            lastSeenView.time = user.createdAt.time
            lastSeenView.visibility = View.VISIBLE
        } else {
            lastSeenView.visibility = View.GONE
        }

    }

}