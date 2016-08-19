package catchla.yep.view.holder

import android.support.v7.widget.RecyclerView
import android.view.View
import catchla.yep.adapter.TopicsAdapter

/**
 * Created by mariotaku on 16/8/18.
 */
class TopicSearchBoxViewHolder(
        val topicsAdapter: TopicsAdapter,
        itemView: View,
        val clickListener: TopicsAdapter.TopicClickListener?) : RecyclerView.ViewHolder(itemView) {

}