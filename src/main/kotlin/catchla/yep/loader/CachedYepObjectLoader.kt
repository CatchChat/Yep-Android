package catchla.yep.loader

import android.accounts.Account
import android.content.Context
import com.bluelinelabs.logansquare.LoganSquare
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

/**
 * Created by mariotaku on 15/6/3.
 */
abstract class CachedYepObjectLoader<T>(
         context: Context,
        account: Account,
        private val objectClass: Class<T>,
         readCache: Boolean,
         writeCache: Boolean) : CachedYepLoader<T>(context, account, null, readCache, writeCache) {

    @Throws(IOException::class)
    override fun serialize(data: T, os: OutputStream) {
        LoganSquare.serialize(data, os)
    }

    @Throws(IOException::class)
    override fun deserialize(st: InputStream): T? {
        return LoganSquare.parse<T>(st, objectClass)
    }
}
