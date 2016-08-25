package catchla.yep.view.holder

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import kotlinx.android.synthetic.main.list_item_search_section_header.view.*

/**
 * Created by mariotaku on 16/8/25.
 */
class SearchSectionHeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val sectionHeader: TextView by lazy { itemView.sectionHeader }

    fun display(title: CharSequence) {
        sectionHeader.text = title
    }
}