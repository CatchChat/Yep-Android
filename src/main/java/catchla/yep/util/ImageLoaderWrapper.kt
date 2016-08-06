package catchla.yep.util

import android.content.Context
import android.widget.ImageView
import catchla.yep.R
import com.nostra13.universalimageloader.core.DisplayImageOptions
import com.nostra13.universalimageloader.core.ImageLoader
import com.nostra13.universalimageloader.core.assist.ImageScaleType
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener

/**
 * Created by mariotaku on 15/10/13.
 */
class ImageLoaderWrapper(
        private val context: Context,
        private val imageLoader: ImageLoader,
        defaultDisplayImageOptions: DisplayImageOptions
) {
    private val profileImageOption: DisplayImageOptions
    private val providerPreviewOption: DisplayImageOptions

    init {
        profileImageOption = buildOption {
            it.cloneFrom(defaultDisplayImageOptions)
            it.imageScaleType(ImageScaleType.IN_SAMPLE_INT)
            it.showImageOnLoading(R.drawable.ic_profile_image_default)
            it.showImageForEmptyUri(R.drawable.ic_profile_image_default)
            it.showImageOnFail(R.drawable.ic_profile_image_default)
            it.resetViewBeforeLoading(true)
        }
        providerPreviewOption = buildOption {
            it.cloneFrom(defaultDisplayImageOptions)
            it.imageScaleType(ImageScaleType.IN_SAMPLE_INT)
            it.resetViewBeforeLoading(true)
            it.displayer(RoundedBitmapDisplayer(context.resources.getDimensionPixelOffset(R.dimen.element_spacing_small)))
        }

    }

    fun displayProfileImage(uri: String?, view: ImageView) {
        imageLoader.displayImage(uri, view, profileImageOption)
    }

    fun displayProfileImage(uri: String?, view: ImageView, listener: ImageLoadingListener) {
        imageLoader.displayImage(uri, view, profileImageOption, listener)
    }

    fun cancelDisplayTask(imageView: ImageView) {
        imageLoader.cancelDisplayTask(imageView)
    }

    fun displaySkillBannerImage(uri: String?, view: ImageView) {
        imageLoader.displayImage(uri, view)
    }

    fun displayImage(uri: String?, view: ImageView) {
        imageLoader.displayImage(uri, view)
    }

    fun displayProviderPreviewImage(uri: String?, view: ImageView) {
        displayImage(uri, view, providerPreviewOption)
    }

    fun displayImage(uri: String?, view: ImageView, options: DisplayImageOptions) {
        imageLoader.displayImage(uri, view, options)
    }

    fun displayImage(uri: String?, view: ImageView,
                     loadingListener: ImageLoadingListener?,
                     progressListener: ImageLoadingProgressListener?) {
        imageLoader.displayImage(uri, view, null, loadingListener, progressListener)
    }

    private fun buildOption(action: (builder: DisplayImageOptions.Builder) -> Unit): DisplayImageOptions {
        val builder = DisplayImageOptions.Builder()
        action(builder)
        return builder.build()
    }
}
