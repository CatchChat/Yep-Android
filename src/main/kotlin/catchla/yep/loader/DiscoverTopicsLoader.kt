package catchla.yep.loader

import android.accounts.Account
import android.content.Context
import catchla.yep.Constants
import catchla.yep.model.Paging
import catchla.yep.model.ResponseList
import catchla.yep.model.Topic
import catchla.yep.model.YepException
import catchla.yep.util.YepAPI
import java.util.*

/**
 * Created by mariotaku on 15/5/27.
 */
class DiscoverTopicsLoader(
        context: Context,
        account: Account,
        private val userId: String?,
        private val paging: Paging,
        @Topic.SortOrder
        private val sortBy: String,
        readCache: Boolean,
        writeCache: Boolean,
        oldData: List<Topic>?
) : CachedYepListLoader<Topic>(context, account, Topic::class.java, oldData, readCache, writeCache), Constants {

    override val cacheFileName: String
        get() = "discover_topics_cache_" + account.name + "_sort_by_" + sortBy

    @Throws(YepException::class)
    override fun requestData(yep: YepAPI, oldData: List<Topic>?): List<Topic> {
        val list = ArrayList<Topic>()
        if (oldData != null) {
            list.addAll(oldData)
        }
        val topics: ResponseList<Topic>
        if (userId != null) {
            topics = yep.getTopics(userId, paging)
        } else {
            topics = yep.getDiscoverTopics(sortBy, paging)
        }
        for (topic in topics) {
            list.remove(topic)
            list.add(topic)
        }
        return list
    }


}
