package catchla.yep.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import catchla.yep.R
import catchla.yep.model.InstagramMedia
import catchla.yep.view.holder.InstagramMediaViewHolder

/**
 * Created by mariotaku on 15/6/4.
 */
class InstagramMediaAdapter(context: Context) : LoadMoreSupportAdapter<RecyclerView.ViewHolder>(context) {
    private val inflater: LayoutInflater
    var shots: List<InstagramMedia>? = null
        set(value) {
            field = value
            notifyDataSetChanged()
        }


    init {
        inflater = LayoutInflater.from(context)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = inflater.inflate(R.layout.grid_item_gallery_provider_type, parent, false)
        return InstagramMediaViewHolder(view, this)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as InstagramMediaViewHolder).displayShot(shots!![position])
    }

    override fun getItemCount(): Int {
        return shots?.size ?: 0
    }

}
