package catchla.yep.view.holder

import android.support.v4.view.ViewCompat
import android.support.v7.widget.RecyclerView
import android.view.View
import catchla.yep.util.view.ViewSupport
import kotlinx.android.synthetic.main.list_item_search_box.view.*

/**
 * Created by mariotaku on 16/8/18.
 */
class TopicSearchBoxViewHolder(
        itemView: View,
        searchHint: String,
        val clickListener: (() -> Unit)?
) : RecyclerView.ViewHolder(itemView) {
    init {
        ViewSupport.setClipToOutline(itemView, true)
        itemView.searchHint.text = searchHint
    }
}