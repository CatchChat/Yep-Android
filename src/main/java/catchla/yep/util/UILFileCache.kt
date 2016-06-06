package catchla.yep.util

import android.net.Uri
import catchla.yep.provider.CacheProvider
import com.nostra13.universalimageloader.cache.disc.DiskCache
import org.mariotaku.mediaviewer.library.FileCache
import java.io.File
import java.io.IOException
import java.io.InputStream

/**
 * Created by mariotaku on 16/1/20.
 */
class UILFileCache(private val cache: DiskCache) : FileCache {

    override fun get(key: String): File? {
        return cache.get(key)
    }

    override fun remove(key: String) {
        cache.remove(key)
    }

    @Throws(IOException::class)
    override fun save(key: String, st: InputStream, metadata: ByteArray?, listener: FileCache.CopyListener?) {
        cache.save(key, st) { current, total -> listener == null || listener.onCopied(current) }
    }

    override fun toUri(key: String): Uri {
        return CacheProvider.getCacheUri(key)
    }

    override fun fromUri(uri: Uri): String {
        return CacheProvider.getCacheKey(uri)
    }
}
