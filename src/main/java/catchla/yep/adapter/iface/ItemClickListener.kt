package catchla.yep.adapter.iface

import android.support.v7.widget.RecyclerView

import catchla.yep.model.Skill
import catchla.yep.view.holder.FriendGridViewHolder
import catchla.yep.view.holder.TopicViewHolder

/**
 * Created by mariotaku on 15/5/29.
 */
interface ItemClickListener {

    fun onItemClick(position: Int, holder: RecyclerView.ViewHolder)

}
