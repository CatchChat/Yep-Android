package catchla.yep.provider


import android.content.ContentProvider
import android.content.ContentResolver
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import android.os.ParcelFileDescriptor
import catchla.yep.Constants
import catchla.yep.util.dagger.GeneralComponentHelper
import com.j256.simplemagic.ContentInfoUtil
import okio.ByteString
import org.mariotaku.mediaviewer.library.FileCache
import java.io.FileNotFoundException
import java.io.IOException
import javax.inject.Inject

/**
 * Created by mariotaku on 16/1/1.
 */
class CacheProvider : ContentProvider() {
    @Inject
    lateinit var simpleDiskCache: FileCache
    lateinit var contentInfoUtil: ContentInfoUtil

    override fun onCreate(): Boolean {
        contentInfoUtil = ContentInfoUtil()
        GeneralComponentHelper.build(context!!).inject(this)
        return true
    }

    override fun query(uri: Uri, projection: Array<String>?, selection: String?, selectionArgs: Array<String>?, sortOrder: String?): Cursor? {
        throw UnsupportedOperationException()
    }

    override fun getType(uri: Uri): String? {
        try {
            val file = simpleDiskCache.get(getCacheKey(uri)) ?: return null
            return contentInfoUtil.findMatch(file).mimeType
        } catch (e: IOException) {
            return null
        }

    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        throw UnsupportedOperationException()
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        throw UnsupportedOperationException()
    }

    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<String>?): Int {
        throw UnsupportedOperationException()
    }

    @Throws(FileNotFoundException::class)
    override fun openFile(uri: Uri, mode: String): ParcelFileDescriptor? {
        try {
            val file = simpleDiskCache.get(getCacheKey(uri)) ?: throw FileNotFoundException()
            val modeBits = modeToMode(mode)
            if (modeBits != ParcelFileDescriptor.MODE_READ_ONLY)
                throw IllegalArgumentException("Cache can't be opened for write")
            return ParcelFileDescriptor.open(file, modeBits)
        } catch (e: IOException) {
            throw FileNotFoundException()
        }

    }

    companion object {

        fun getCacheUri(key: String): Uri {
            return Uri.Builder().scheme(ContentResolver.SCHEME_CONTENT).authority(Constants.AUTHORITY_YEP_CACHE).appendPath(ByteString.encodeUtf8(key).base64Url()).build()
        }

        fun getCacheKey(uri: Uri): String {
            if (ContentResolver.SCHEME_CONTENT != uri.scheme)
                throw IllegalArgumentException(uri.toString())
            if (Constants.AUTHORITY_YEP_CACHE != uri.authority)
                throw IllegalArgumentException(uri.toString())
            return ByteString.decodeBase64(uri.lastPathSegment).utf8()
        }

        /**
         * Copied from ContentResolver.java
         */
        private fun modeToMode(mode: String): Int {
            val modeBits: Int
            if ("r" == mode) {
                modeBits = ParcelFileDescriptor.MODE_READ_ONLY
            } else if ("w" == mode || "wt" == mode) {
                modeBits = ParcelFileDescriptor.MODE_WRITE_ONLY or ParcelFileDescriptor.MODE_CREATE or ParcelFileDescriptor.MODE_TRUNCATE
            } else if ("wa" == mode) {
                modeBits = ParcelFileDescriptor.MODE_WRITE_ONLY or ParcelFileDescriptor.MODE_CREATE or ParcelFileDescriptor.MODE_APPEND
            } else if ("rw" == mode) {
                modeBits = ParcelFileDescriptor.MODE_READ_WRITE or ParcelFileDescriptor.MODE_CREATE
            } else if ("rwt" == mode) {
                modeBits = ParcelFileDescriptor.MODE_READ_WRITE or ParcelFileDescriptor.MODE_CREATE or ParcelFileDescriptor.MODE_TRUNCATE
            } else {
                throw IllegalArgumentException("Invalid mode: " + mode)
            }
            return modeBits
        }
    }

}
