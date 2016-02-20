package catchla.yep.loader;

import android.content.Context;

import org.mariotaku.sqliteqb.library.Expression;
import org.mariotaku.sqliteqb.library.OrderBy;

import catchla.yep.model.Conversation;
import catchla.yep.model.Message;
import catchla.yep.model.MessageCursorIndices;
import catchla.yep.provider.YepDataStore.Messages;

/**
 * Created by mariotaku on 15/5/29.
 */
public class MessagesLoader extends ObjectCursorLoader<Message> {
    private final Conversation mConversation;

    public MessagesLoader(final Context context, final Conversation conversation) {
        super(context, MessageCursorIndices.class, Messages.CONTENT_URI, Messages.COLUMNS, null, null, null);
        mConversation = conversation;
        setSelection(Expression.and(Expression.equalsArgs(Messages.ACCOUNT_ID),
                Expression.equalsArgs(Messages.CONVERSATION_ID)).getSQL());
        setSelectionArgs(new String[]{conversation.getAccountId(), conversation.getId()});
        setSortOrder(new OrderBy(Messages.CREATED_AT, false).getSQL());
    }


}
