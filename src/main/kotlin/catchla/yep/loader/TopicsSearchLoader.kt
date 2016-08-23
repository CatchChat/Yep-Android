package catchla.yep.loader

import android.accounts.Account
import android.content.Context
import catchla.yep.Constants
import catchla.yep.model.Paging
import catchla.yep.model.Topic
import catchla.yep.model.YepException
import catchla.yep.util.YepAPI
import java.util.*

/**
 * Created by mariotaku on 15/5/27.
 */
class TopicsSearchLoader(
        context: Context,
        account: Account,
        val query: String,
        val userId: String? = null,
        val skillId: String? = null,
        val recommended: Boolean? = null,
        val paging: Paging,
        oldData: List<Topic>?
) : CachedYepListLoader<Topic>(context, account, Topic::class.java, oldData, false, false), Constants {

    override val cacheFileName: String? = null

    @Throws(YepException::class)
    override fun requestData(yep: YepAPI, oldData: List<Topic>?): List<Topic> {
        val list = ArrayList<Topic>()
        if (oldData != null) {
            list.addAll(oldData)
        }
        val topics = yep.searchTopics(query, userId, skillId, recommended, paging)
        for (topic in topics) {
            list.remove(topic)
            list.add(topic)
        }
        return list
    }


}
