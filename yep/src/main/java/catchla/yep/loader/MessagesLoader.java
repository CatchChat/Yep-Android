package catchla.yep.loader;

import android.accounts.Account;
import android.content.Context;

import org.mariotaku.sqliteqb.library.Expression;
import org.mariotaku.sqliteqb.library.OrderBy;

import catchla.yep.model.Conversation;
import catchla.yep.model.Message;
import catchla.yep.provider.YepDataStore.Messages;

/**
 * Created by mariotaku on 15/5/29.
 */
public class MessagesLoader extends ObjectCursorLoader<Message> {
    private final Conversation mConversation;

    public MessagesLoader(final Context context, final Account account, final Conversation conversation) {
        super(context, Message.Indices.class, Messages.CONTENT_URI, Messages.COLUMNS, null, null, null);
        mConversation = conversation;
        setSelection(Expression.equalsArgs(Messages.CONVERSATION_ID).getSQL());
        setSelectionArgs(new String[]{conversation.getId()});
        setSortOrder(new OrderBy(Messages.CREATED_AT, false).getSQL());
    }


}
