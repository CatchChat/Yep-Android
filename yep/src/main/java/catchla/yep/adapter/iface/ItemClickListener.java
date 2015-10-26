package catchla.yep.adapter.iface;

import android.support.v7.widget.RecyclerView;

import catchla.yep.view.holder.TopicViewHolder;

/**
 * Created by mariotaku on 15/5/29.
 */
public interface ItemClickListener {

    void onItemClick(int position, RecyclerView.ViewHolder holder);

}
