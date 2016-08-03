package catchla.yep.fragment

import android.accounts.Account
import android.os.Bundle
import android.support.v4.content.Loader
import catchla.yep.Constants

import catchla.yep.loader.HistoricalMessagesLoader
import catchla.yep.model.Message
import catchla.yep.model.Paging
import catchla.yep.model.Topic
import catchla.yep.annotation.PathRecipientType
import catchla.yep.util.YepAPI

/**
 * Created by mariotaku on 15/12/10.
 */
class TopicChatListFragment : ChatListFragment() {
    override fun onLoadFinished(loader: Loader<List<Message>?>, data: List<Message>?) {
        super.onLoadFinished(loader, data)
        setRefreshEnabled(false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setRefreshEnabled(false)
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<List<Message>?> {
        val topic = topic
        val recipientId = topic.circle.id
        return HistoricalMessagesLoader(context, account,
                PathRecipientType.CIRCLES, recipientId, Paging(), false, false,
                adapter.data)
    }

    val topic: Topic
        get() = arguments.getParcelable<Topic>(Constants.EXTRA_TOPIC)

    val account: Account
        get() = arguments.getParcelable<Account>(Constants.EXTRA_ACCOUNT)
}
