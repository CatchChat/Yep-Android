package catchla.yep.fragment

import android.os.Bundle
import android.support.v4.content.Loader
import catchla.yep.Constants

import catchla.yep.loader.MessagesLoader
import catchla.yep.model.Conversation
import catchla.yep.model.Message

/**
 * Created by mariotaku on 15/12/10.
 */
class ConversationChatListFragment : ChatListFragment() {
    override fun onCreateLoader(id: Int, args: Bundle?): Loader<List<Message>?> {
        return MessagesLoader(context, conversation)
    }

    val conversation: Conversation
        get() = arguments.getParcelable<Conversation>(Constants.EXTRA_CONVERSATION)
}
