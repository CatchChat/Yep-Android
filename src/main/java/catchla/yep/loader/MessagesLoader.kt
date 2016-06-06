package catchla.yep.loader

import android.content.Context
import catchla.yep.model.Conversation
import catchla.yep.model.Message
import catchla.yep.model.MessageCursorIndices
import catchla.yep.model.MessageTableInfo
import catchla.yep.provider.YepDataStore.Messages
import org.mariotaku.sqliteqb.library.Expression
import org.mariotaku.sqliteqb.library.OrderBy

/**
 * Created by mariotaku on 15/5/29.
 */
class MessagesLoader(context: Context, private val conversation: Conversation) : ObjectCursorLoader<Message>(context, MessageCursorIndices::class.java, Messages.CONTENT_URI, MessageTableInfo.COLUMNS, null, null, null) {

    init {
        selection = Expression.and(Expression.equalsArgs(Messages.ACCOUNT_ID),
                Expression.equalsArgs(Messages.CONVERSATION_ID)).sql
        selectionArgs = arrayOf(conversation.accountId, conversation.id)
        sortOrder = OrderBy(Messages.CREATED_AT, false).sql
    }


}
