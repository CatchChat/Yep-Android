package catchla.yep.activity

import android.content.Context
import android.content.Intent
import android.net.Uri

import org.mariotaku.pickncrop.library.ImagePickerActivity

class ThemedImagePickerActivity : ImagePickerActivity() {

    class ThemedIntentBuilder(private val context: Context) {
        private val intentBuilder: ImagePickerActivity.IntentBuilder

        init {
            this.intentBuilder = ImagePickerActivity.IntentBuilder(context)
            intentBuilder.cropImageActivityClass(ThemedImageCropperActivity::class.java)
            //            intentBuilder.streamDownloaderClass(RestFuNetworkStreamDownloader.class);
        }

        fun takePhoto(): ThemedIntentBuilder {
            intentBuilder.takePhoto()
            return this
        }

        fun getImage(uri: Uri): ThemedIntentBuilder {
            intentBuilder.getImage(uri)
            return this
        }

        fun build(): Intent {
            val intent = intentBuilder.build()
            intent.setClass(context, ThemedImagePickerActivity::class.java)
            return intent
        }

        fun pickImage(): ThemedIntentBuilder {
            intentBuilder.pickImage()
            return this
        }

        fun addEntry(name: String, value: String, result: Int): ThemedIntentBuilder {
            intentBuilder.addEntry(name, value, result)
            return this
        }

        fun maximumSize(w: Int, h: Int): ThemedIntentBuilder {
            intentBuilder.maximumSize(w, h)
            return this
        }

        fun aspectRatio(x: Int, y: Int): ThemedIntentBuilder {
            intentBuilder.aspectRatio(x, y)
            return this
        }
    }

    companion object {

        //    @Override
        //    public void setTheme(final int resid) {
        //        super.setTheme(ThemeUtils.getNoDisplayThemeResource(this));
        //    }

        fun withThemed(context: Context): ThemedIntentBuilder {
            return ThemedIntentBuilder(context)
        }
    }


}
