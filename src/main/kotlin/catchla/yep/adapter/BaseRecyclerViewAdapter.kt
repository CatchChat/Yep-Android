package catchla.yep.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import catchla.yep.adapter.iface.IBaseRecyclerViewAdapter
import catchla.yep.util.ImageLoaderWrapper
import catchla.yep.util.MessageAudioPlayer
import catchla.yep.util.dagger.GeneralComponentHelper
import com.squareup.otto.Bus
import javax.inject.Inject

/**
 * Created by mariotaku on 15/10/13.
 */
abstract class BaseRecyclerViewAdapter<VH : RecyclerView.ViewHolder>(
        val context: Context
) : RecyclerView.Adapter<VH>(), IBaseRecyclerViewAdapter {

    @Inject
    lateinit override var imageLoader: ImageLoaderWrapper
    @Inject
    lateinit var bus: Bus
    @Inject
    lateinit var audioPlayer: MessageAudioPlayer

    init {
        //noinspection unchecked
        GeneralComponentHelper.build(context).inject(this as BaseRecyclerViewAdapter<RecyclerView.ViewHolder>)
    }
}
