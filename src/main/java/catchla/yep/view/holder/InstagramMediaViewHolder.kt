package catchla.yep.view.holder

import android.graphics.Bitmap
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import catchla.yep.R
import catchla.yep.adapter.InstagramMediaAdapter
import catchla.yep.model.InstagramImage
import catchla.yep.model.InstagramMedia
import com.nostra13.universalimageloader.core.assist.FailReason
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener

/**
 * Created by mariotaku on 15/6/3.
 */
class InstagramMediaViewHolder(
        itemView: View,
        private val adapter: InstagramMediaAdapter
) : RecyclerView.ViewHolder(itemView), ImageLoadingListener {
    private val imageView: ImageView
    private val imageProgress: View

    init {
        imageView = itemView.findViewById(R.id.image_view) as ImageView
        imageProgress = itemView.findViewById(R.id.image_progress)
    }

    fun displayShot(shot: InstagramMedia) {
        val image = getBestImage(shot.images)
        val imageLoader = adapter.imageLoader
        if (image != null) {
            imageLoader.displayImage(image.url, imageView, this, null)
        } else {
            imageLoader.cancelDisplayTask(imageView)
        }
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

    companion object {

        fun getBestImage(images: List<InstagramImage>?): InstagramImage? {
            if (images == null) return null
            for (image in images) {
                if ("low_resolution" == image.resolution) return image
            }
            return null
        }
    }
}
