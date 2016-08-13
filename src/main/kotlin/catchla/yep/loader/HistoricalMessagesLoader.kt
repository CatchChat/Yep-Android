package catchla.yep.loader

import android.accounts.Account
import android.content.Context
import catchla.yep.Constants
import catchla.yep.annotation.PathRecipientType
import catchla.yep.model.Message
import catchla.yep.model.Paging
import catchla.yep.model.YepException
import catchla.yep.util.YepAPI
import java.util.*

/**
 * Created by mariotaku on 15/5/27.
 */
class HistoricalMessagesLoader(
        context: Context,
        account: Account,
        @PathRecipientType private val recipientType: String,
        private val recipientId: String,
        private val paging: Paging,
        readCache: Boolean, writeCache: Boolean, oldData: List<Message>?
) : CachedYepListLoader<Message>(context, account, Message::class.java, oldData, readCache, writeCache), Constants {

    override val cacheFileName: String
        get() = "historical_messages_cache_${recipientType}_${recipientId}_${account.name}"

    @Throws(YepException::class)
    override fun requestData(yep: YepAPI, oldData: List<Message>?): List<Message> {
        val list = ArrayList<Message>()
        if (oldData != null) {
            list.addAll(oldData)
        }
        val topics = yep.getHistoricalMessages(recipientType, recipientId, paging)
        for (topic in topics) {
            list.remove(topic)
            list.add(topic)
        }
        list.sortByDescending { it.createdAt }
        return list
    }


}
