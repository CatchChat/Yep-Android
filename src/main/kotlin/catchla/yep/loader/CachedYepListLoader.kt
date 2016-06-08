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
abstract class CachedYepListLoader<E>(
        context: Context, account: Account,
        private val objectClass: Class<E>,
        oldData: List<E>?,
        readCache: Boolean,
        writeCache: Boolean
) : CachedYepLoader<List<E>>(context, account, oldData, readCache, writeCache) {

    @Throws(IOException::class)
    override fun serialize(data: List<E>, os: OutputStream) {
        LoganSquare.serialize(data, os, objectClass)
    }

    @Throws(IOException::class)
    override fun deserialize(st: InputStream): List<E>? {
        return LoganSquare.parseList(st, objectClass)
    }
}
