/*
 * Twidere - Twitter client for Android
 *
 *  Copyright (C) 2012-2015 Mariotaku Lee <mariotaku.lee@gmail.com>
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package catchla.yep.loader

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.BitmapFactory.Options
import android.net.Uri
import android.os.Handler
import android.support.v4.content.AsyncTaskLoader
import catchla.yep.util.BitmapUtils
import catchla.yep.util.ImageValidator
import catchla.yep.util.dagger.GeneralComponentHelper
import com.nostra13.universalimageloader.cache.disc.DiskCache
import com.nostra13.universalimageloader.core.download.ImageDownloader
import com.nostra13.universalimageloader.utils.IoUtils
import java.io.File
import java.io.IOException
import javax.inject.Inject

class TileImageLoader(context: Context, private val listener: TileImageLoader.DownloadListener, private val mUri: Uri?) : AsyncTaskLoader<TileImageLoader.Result>(context) {
    private val mHandler: Handler
    @Inject
    internal lateinit var downloader: ImageDownloader
    @Inject
    internal lateinit var diskCache: DiskCache

    private val fallbackSize: Float

    init {
        mHandler = Handler()
        GeneralComponentHelper.build(context).inject(this)
        val res = context.resources
        val dm = res.displayMetrics
        fallbackSize = Math.max(dm.heightPixels, dm.widthPixels).toFloat()
    }

    override fun loadInBackground(): TileImageLoader.Result {
        if (mUri == null) {
            return Result.nullInstance()
        }
        val scheme = mUri.scheme
        if ("http" == scheme || "https" == scheme) {
            val url = mUri.toString() ?: return Result.nullInstance()
            var cacheFile: File? = diskCache.get(url)
            if (cacheFile != null) {
                val cachedValidity = ImageValidator.checkImageValidity(cacheFile)
                if (ImageValidator.isValid(cachedValidity)) {
                    // The file is corrupted, so we remove it from
                    // cache.
                    return decodeBitmapOnly(cacheFile, ImageValidator.isValidForRegionDecoder(cachedValidity))
                }
            }
            try {
                // from SD cache
                val `is` = downloader.getStream(url, null) ?: return Result.nullInstance()
                try {
                    val length = `is`.available().toLong()
                    mHandler.post(DownloadStartRunnable(this, listener, length))
                    diskCache.save(url, `is`) { current, total ->
                        mHandler.post(ProgressUpdateRunnable(listener, current.toLong()))
                        !isAbandoned
                    }
                    mHandler.post(DownloadFinishRunnable(this, listener))
                } finally {
                    IoUtils.closeSilently(`is`)
                }
                cacheFile = diskCache.get(url)
                val downloadedValidity = ImageValidator.checkImageValidity(cacheFile)
                if (ImageValidator.isValid(downloadedValidity)) {
                    // The file is corrupted, so we remove it from
                    // cache.
                    return decodeBitmapOnly(cacheFile,
                            ImageValidator.isValidForRegionDecoder(downloadedValidity))
                } else {
                    diskCache.remove(url)
                    throw IOException()
                }
            } catch (e: Exception) {
                mHandler.post(DownloadErrorRunnable(this, listener, e))
                return Result.getInstance(cacheFile!!, e)
            }

        } else if (ContentResolver.SCHEME_FILE == scheme) {
            val file = File(mUri.path)
            try {
                return decodeBitmapOnly(file,
                        ImageValidator.isValidForRegionDecoder(ImageValidator.checkImageValidity(file)))
            } catch (e: Exception) {
                return Result.getInstance(file, e)
            }

        }
        return Result.nullInstance()
    }

    protected fun decodeBitmapOnly(file: File, useDecoder: Boolean): Result {
        val path = file.absolutePath
        val o = BitmapFactory.Options()
        o.inJustDecodeBounds = true
        BitmapFactory.decodeFile(path, o)
        val width = o.outWidth
        val height = o.outHeight
        if (width <= 0 || height <= 0) return Result.getInstance(file, null)
        o.inJustDecodeBounds = false
        o.inSampleSize = BitmapUtils.computeSampleSize(fallbackSize / Math.max(width, height))
        val bitmap = BitmapFactory.decodeFile(path, o)
        return Result.getInstance(useDecoder, bitmap, o, ImageValidator.getOrientation(file.absolutePath), file)
    }

    override fun onStartLoading() {
        forceLoad()
    }


    interface DownloadListener {
        fun onDownloadError(t: Throwable)

        fun onDownloadFinished()

        fun onDownloadStart(total: Long)

        fun onProgressUpdate(downloaded: Long)
    }

    class Result(val useDecoder: Boolean, val bitmap: Bitmap?, val options: Options?, val orientation: Int,
                 val file: File?, val exception: Exception?) {

        fun hasData(): Boolean {
            return bitmap != null || useDecoder
        }

        companion object {

            fun getInstance(bitmap: Bitmap, options: Options, orientation: Int, file: File): Result {
                return Result(false, bitmap, options, orientation, file, null)
            }

            fun getInstance(useDecoder: Boolean, bitmap: Bitmap, options: Options,
                            orientation: Int, file: File): Result {
                return Result(useDecoder, bitmap, options, orientation, file, null)
            }

            fun getInstance(file: File, e: Exception?): Result {
                return Result(false, null, null, 0, file, e)
            }

            fun nullInstance(): Result {
                return Result(false, null, null, 0, null, null)
            }
        }
    }

    private class DownloadErrorRunnable internal constructor(private val loader: TileImageLoader, private val listener: DownloadListener?, private val t: Throwable) : Runnable {

        override fun run() {
            if (listener == null || loader.isAbandoned || loader.isReset) return
            listener.onDownloadError(t)
        }
    }

    private class DownloadFinishRunnable internal constructor(private val loader: TileImageLoader, private val listener: DownloadListener?) : Runnable {

        override fun run() {
            if (listener == null || loader.isAbandoned || loader.isReset) return
            listener.onDownloadFinished()
        }
    }

    private class DownloadStartRunnable internal constructor(private val loader: TileImageLoader, private val listener: DownloadListener?, private val total: Long) : Runnable {

        override fun run() {
            if (listener == null || loader.isAbandoned || loader.isReset) return
            listener.onDownloadStart(total)
        }
    }

    private class ProgressUpdateRunnable internal constructor(private val listener: DownloadListener?, private val current: Long) : Runnable {

        override fun run() {
            if (listener == null) return
            listener.onProgressUpdate(current)
        }
    }
}
