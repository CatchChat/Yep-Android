package catchla.yep.loader

import android.accounts.Account
import android.content.Context
import catchla.yep.Constants
import catchla.yep.model.DiscoverQuery
import catchla.yep.model.Paging
import catchla.yep.model.User
import catchla.yep.model.YepException
import catchla.yep.util.YepAPI
import java.util.*

/**
 * Created by mariotaku on 15/5/27.
 */
class DiscoverUsersLoader(
        context: Context, account: Account,
        private val query: DiscoverQuery,
        oldData: List<User>,
        private val paging: Paging,
        readCache: Boolean,
        writeCache: Boolean
) : CachedYepListLoader<User>(context, account, User::class.java, oldData, readCache, writeCache), Constants {

    override val cacheFileName: String
        get() = "discover_cache_${account.name}"

    @Throws(YepException::class)
    override fun requestData(yep: YepAPI, oldData: List<User>?): List<User> {
        val list = ArrayList<User>()
        if (oldData != null) {
            list.addAll(oldData)
        }
        for (topic in yep.getDiscover(query, paging)) {
            list.remove(topic)
            list.add(topic)
        }
        return list
    }


}
