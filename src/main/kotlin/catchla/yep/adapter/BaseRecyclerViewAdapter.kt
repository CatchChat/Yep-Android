package catchla.yep.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import catchla.yep.adapter.iface.IBaseRecyclerViewAdapter
import catchla.yep.util.ImageLoaderWrapper
import catchla.yep.util.dagger.GeneralComponentHelper
import javax.inject.Inject

/**
 * Created by mariotaku on 15/10/13.
 */
abstract class BaseRecyclerViewAdapter<VH : RecyclerView.ViewHolder>(
        val context: Context
) : RecyclerView.Adapter<VH>(), IBaseRecyclerViewAdapter {

    @Inject
    lateinit override var imageLoader: ImageLoaderWrapper

    init {
        //noinspection unchecked
        GeneralComponentHelper.build(context).inject(this as BaseRecyclerViewAdapter<RecyclerView.ViewHolder>)
    }
}
