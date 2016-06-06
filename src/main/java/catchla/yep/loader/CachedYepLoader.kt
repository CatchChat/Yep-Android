package catchla.yep.loader

import android.accounts.Account
import android.content.Context
import android.support.v4.content.AsyncTaskLoader
import android.util.Log
import catchla.yep.BuildConfig
import catchla.yep.Constants
import catchla.yep.model.YepException
import catchla.yep.util.Utils
import catchla.yep.util.YepAPI
import catchla.yep.util.YepAPIFactory
import catchla.yep.util.dagger.GeneralComponentHelper
import org.mariotaku.mediaviewer.library.FileCache
import java.io.*
import javax.inject.Inject

/**
 * Created by mariotaku on 15/6/3.
 */
abstract class CachedYepLoader<T>(
        context: Context,
        protected val account: Account,
        private val oldData: T?,
        private val readCache: Boolean,
        private val writeCache: Boolean
) : AsyncTaskLoader<T>(context), Constants {

    @Inject
    internal lateinit var fileCache: FileCache
    var exception: YepException? = null
        private set

    init {
        //noinspection unchecked
        GeneralComponentHelper.build(context).inject(this as CachedYepLoader<Any>)
    }

    override fun loadInBackground(): T? {
        val yep = YepAPIFactory.getInstance(context, account)
        try {
            if (readCache) {
                var fis: FileInputStream? = null
                try {
                    val cacheFile = fileCache.get(cacheFileName)
                    if (cacheFile != null) {
                        fis = FileInputStream(cacheFile)
                        val cached = deserialize(fis)
                        if (cached != null) return cached
                    }
                } catch (e: IOException) {
                    // Ignore
                } finally {
                    Utils.closeSilently(fis)
                }
            }
            val data = requestData(yep, oldData)
            if (writeCache) {
                var pos: PipedOutputStream? = null
                var pis: PipedInputStream? = null
                try {
                    pos = PipedOutputStream()
                    pis = PipedInputStream(pos)
                    serializeThreaded(data, pos)
                    fileCache.save(cacheFileName, pis, null, null)
                } catch (e: IOException) {
                    // Ignore
                } finally {
                    Utils.closeSilently(pos)
                    Utils.closeSilently(pis)
                }
            }
            return data
        } catch (e: YepException) {
            if (BuildConfig.DEBUG) {
                Log.w(Constants.LOGTAG, e)
            }
            exception = e
            return null
        }

    }

    private fun serializeThreaded(data: T, pos: PipedOutputStream) {
        Thread(Runnable {
            try {
                serialize(data, pos)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }).start()
    }

    override fun onStartLoading() {
        forceLoad()
    }

    @Throws(IOException::class)
    protected abstract fun serialize(data: T, os: OutputStream)

    @Throws(IOException::class)
    protected abstract fun deserialize(st: InputStream): T?

    protected abstract val cacheFileName: String

    @Throws(YepException::class)
    protected abstract fun requestData(yep: YepAPI, oldData: T?): T

}
