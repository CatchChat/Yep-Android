package catchla.yep.loader

import android.accounts.Account
import android.content.Context
import catchla.yep.Constants
import catchla.yep.model.Paging
import catchla.yep.model.Topic
import catchla.yep.model.TopicSortOrder
import catchla.yep.model.YepException
import catchla.yep.util.YepAPI
import java.util.*

/**
 * Created by mariotaku on 15/5/27.
 */
class UserTopicsLoader(
        context: Context,
        account: Account,
        private val userId: String,
        private val paging: Paging,
        @TopicSortOrder
        private val sortBy: String?,
        readCache: Boolean,
        writeCache: Boolean,
        oldData: List<Topic>?
) : CachedYepListLoader<Topic>(context, account, Topic::class.java, oldData, readCache, writeCache), Constants {

    override val cacheFileName: String
        get() = "user_topics_cache_${account.name}_user_${userId}_sort_by_${sortBy}"

    @Throws(YepException::class)
    override fun requestData(yep: YepAPI, oldData: List<Topic>?): List<Topic> {
        val list = ArrayList<Topic>()
        if (oldData != null) {
            list.addAll(oldData)
        }
        val topics = yep.getTopics(userId, paging)
        for (topic in topics) {
            list.remove(topic)
            list.add(topic)
        }
        return list
    }


}
