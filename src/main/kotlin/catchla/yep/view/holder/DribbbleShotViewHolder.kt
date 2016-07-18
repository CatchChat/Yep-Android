package catchla.yep.view.holder

import android.graphics.Bitmap
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView

import com.nostra13.universalimageloader.core.assist.FailReason
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener

import catchla.yep.R
import catchla.yep.adapter.DribbbleShotsAdapter
import catchla.yep.model.DribbbleShot
import catchla.yep.model.DribbbleShotImage
import catchla.yep.util.ImageLoaderWrapper

/**
 * Created by mariotaku on 15/6/3.
 */
class DribbbleShotViewHolder(private val adapter: DribbbleShotsAdapter, itemView: View) : RecyclerView.ViewHolder(itemView), ImageLoadingListener {

    private val imageView: ImageView
    private val imageProgress: View

    init {
        imageView = itemView.findViewById(R.id.image_view) as ImageView
        imageProgress = itemView.findViewById(R.id.image_progress)
    }

    fun displayShot(shot: DribbbleShot) {
        val image = getBestImage(shot.images)
        val imageLoader = adapter.imageLoader
        if (image != null) {
            imageLoader.displayImage(image.url, imageView, this, null)
        } else {
            imageLoader.cancelDisplayTask(imageView)
        }
    }

    private fun getBestImage(images: List<DribbbleShotImage>?): DribbbleShotImage? {
        if (images == null) return null
        for (image in images) {
            if ("normal" == image.resolution) return image
        }
        return null
    }

    override fun onLoadingStarted(imageUri: String, view: View) {
        imageProgress.visibility = View.VISIBLE
    }

    override fun onLoadingFailed(imageUri: String, view: View, failReason: FailReason) {
        imageProgress.visibility = View.GONE
    }

    override fun onLoadingComplete(imageUri: String, view: View, loadedImage: Bitmap) {
        imageProgress.visibility = View.GONE
    }

    override fun onLoadingCancelled(imageUri: String, view: View) {
        imageProgress.visibility = View.GONE
    }
}
