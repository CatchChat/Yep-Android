package catchla.yep.util.dagger

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import catchla.yep.BuildConfig
import catchla.yep.Constants
import catchla.yep.util.*
import com.nostra13.universalimageloader.cache.disc.DiskCache
import com.nostra13.universalimageloader.cache.disc.impl.ext.LruDiskCache
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator
import com.nostra13.universalimageloader.core.DisplayImageOptions
import com.nostra13.universalimageloader.core.ImageLoader
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration
import com.nostra13.universalimageloader.core.assist.QueueProcessingType
import com.nostra13.universalimageloader.core.download.ImageDownloader
import com.nostra13.universalimageloader.utils.L
import com.squareup.otto.Bus
import com.squareup.otto.ThreadEnforcer
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import org.mariotaku.kpreferences.KPreferences
import org.mariotaku.mediaviewer.library.FileCache
import org.mariotaku.mediaviewer.library.MediaDownloader
import java.io.IOException
import javax.inject.Singleton

/**
 * Created by mariotaku on 15/10/8.
 */
@Module
class ApplicationModule internal constructor(private val application: Application) : Constants {
    @Provides
    @Singleton
    fun diskCache(): DiskCache {
        return createDiskCache()
    }

    @Provides
    @Singleton
    fun imageDownloader(downloader: MediaDownloader): ImageDownloader {
        return YepImageDownloader(application, downloader)
    }

    @Provides
    @Singleton
    fun fileCache(cache: DiskCache): FileCache {
        return UILFileCache(cache)
    }

    @Provides
    @Singleton
    fun mediaDownloader(client: OkHttpClient): MediaDownloader {
        return OkMediaDownloader(client)
    }

    @Provides
    @Singleton
    fun sharedPreferences(): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(application)
    }

    @Provides
    @Singleton
    fun kPreferences(sharedPreferences: SharedPreferences): KPreferences {
        return KPreferences(sharedPreferences)
    }

    @Provides
    @Singleton
    fun getImageLoaderWrapper(imageLoader: ImageLoader): ImageLoaderWrapper {
        val defaultDisplayImageOptions = createDefaultDisplayImageOptions()
        return ImageLoaderWrapper(application, imageLoader, defaultDisplayImageOptions)
    }

    @Provides
    @Singleton
    fun getImageLoader(imageDownloader: ImageDownloader, diskCache: DiskCache): ImageLoader {
        return createImageLoader(imageDownloader, diskCache, createDefaultDisplayImageOptions())
    }

    @Provides
    @Singleton
    fun bus(): Bus {
        return Bus(ThreadEnforcer.MAIN)
    }

    @Provides
    @Singleton
    fun okHttpClient(): OkHttpClient {
        return YepAPIFactory.getOkHttpClient(application)
    }

    @Provides
    @Singleton
    fun busHandler(bus: Bus): BusHandler {
        return BusHandler(bus)
    }

    @Provides
    @Singleton
    fun messageAudioPlayer(bus: Bus, diskCache: DiskCache): MessageAudioPlayer {
        return MessageAudioPlayer(bus, diskCache)
    }

    private fun createDiskCache(): DiskCache {
        try {
            return LruDiskCache(application.cacheDir, Md5FileNameGenerator(), 256 * 1024 * 1024.toLong())
        } catch (e: IOException) {
            throw RuntimeException(e)
        }

    }

    private fun createDefaultDisplayImageOptions(): DisplayImageOptions {
        val builder = DisplayImageOptions.Builder()
        builder.cacheOnDisk(true)
        builder.cacheInMemory(true)
        builder.resetViewBeforeLoading(true)
        return builder.build()
    }

    private fun createImageLoader(downloader: ImageDownloader, cache: DiskCache, defaultDisplayImageOptions: DisplayImageOptions): ImageLoader {
        val loader = ImageLoader.getInstance()
        val cb = ImageLoaderConfiguration.Builder(application)
        cb.threadPriority(Thread.NORM_PRIORITY - 2)
        cb.diskCache(cache)
        cb.defaultDisplayImageOptions(defaultDisplayImageOptions)
        cb.imageDownloader(downloader)
        cb.denyCacheImageMultipleSizesInMemory()
        cb.tasksProcessingOrder(QueueProcessingType.LIFO)
        L.writeDebugLogs(BuildConfig.DEBUG)
        loader.init(cb.build())
        return loader
    }

    companion object {

        private var sInstance: ApplicationModule? = null

        fun get(context: Context): ApplicationModule {
            if (sInstance != null) return sInstance!!
            sInstance = ApplicationModule(context.applicationContext as Application)
            return sInstance!!
        }
    }
}
