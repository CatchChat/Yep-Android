package catchla.yep.util

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import com.nostra13.universalimageloader.core.download.BaseImageDownloader
import org.apache.commons.lang3.math.NumberUtils
import org.mariotaku.mediaviewer.library.MediaDownloader
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream

/**
 * Created by mariotaku on 15/10/15.
 */
class YepImageDownloader(context: Context, private val downloader: MediaDownloader) : BaseImageDownloader(context) {

    @Throws(IOException::class)
    override fun getStreamFromNetwork(imageUri: String, extra: Any): InputStream {
        return downloader.get(imageUri, extra).stream
    }

    @Throws(IOException::class)
    override fun getStreamFromOtherSource(imageUri: String, extra: Any?): InputStream {
        val uri = Uri.parse(imageUri)
        if ("media-thumb" == uri.scheme) {
            val id = NumberUtils.toLong(uri.host, -1)
            if (id == -1L) throw FileNotFoundException(imageUri)
            val projection = arrayOf(MediaStore.Images.Thumbnails.DATA)
            val cursor = MediaStore.Images.Thumbnails.queryMiniThumbnail(context.contentResolver, id,
                    MediaStore.Images.Thumbnails.MINI_KIND, projection) ?: throw FileNotFoundException(imageUri)
            try {
                if (cursor.moveToFirst()) {
                    return getStreamFromFile("file://" + cursor.getString(0), extra)
                }
            } finally {
                cursor.close()
            }
        }
        return super.getStreamFromOtherSource(imageUri, extra)
    }
}
