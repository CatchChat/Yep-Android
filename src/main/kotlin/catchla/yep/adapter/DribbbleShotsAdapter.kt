package catchla.yep.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup

import catchla.yep.R
import catchla.yep.model.DribbbleShot
import catchla.yep.view.holder.DribbbleShotViewHolder

/**
 * Created by mariotaku on 15/6/4.
 */
class DribbbleShotsAdapter(context: Context) : LoadMoreSupportAdapter<RecyclerView.ViewHolder>(context) {
    private val inflater: LayoutInflater
    var shots: List<DribbbleShot>? = null
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    init {
        inflater = LayoutInflater.from(context)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return DribbbleShotViewHolder(this, inflater.inflate(R.layout.grid_item_gallery_provider_type, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as DribbbleShotViewHolder).displayShot(shots!![position])
    }

    override fun getItemCount(): Int {
        if (shots == null) return 0
        return shots!!.size
    }


}
