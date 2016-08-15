package catchla.yep.loader

import android.accounts.Account
import android.content.Context
import catchla.yep.Constants
import catchla.yep.model.*
import catchla.yep.util.YepAPI
import java.util.*

/**
 * Created by mariotaku on 15/5/27.
 */
class DiscoverTopicsLoader(
        context: Context,
        account: Account,
        private val userId: String?,
        private val skillId: String?,
        private val paging: Paging,
        @TopicSortOrder
        private val sortBy: String?,
        readCache: Boolean,
        writeCache: Boolean,
        oldData: List<Topic>?
) : CachedYepListLoader<Topic>(context, account, Topic::class.java, oldData, readCache, writeCache), Constants {

    override val cacheFileName: String
        get() = "discover_topics_cache_${account.name}_sort_by_${sortBy}_skill_id_${skillId}_user_id_${skillId}"

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
            topics = yep.getDiscoverTopics(sortBy, paging, skillId)
        }
        for (topic in topics) {
            list.remove(topic)
            list.add(topic)
        }
        return list
    }


}
