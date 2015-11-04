package catchla.yep.loader;

import android.accounts.Account;
import android.content.Context;

import org.mariotaku.sqliteqb.library.Expression;
import org.mariotaku.sqliteqb.library.OrderBy;

import catchla.yep.model.Conversation;
import catchla.yep.model.Message;
import catchla.yep.provider.YepDataStore.Conversations;
import catchla.yep.util.Utils;

/**
 * Created by mariotaku on 14-8-5.
 */
public class ConversationsLoader extends ObjectCursorLoader<Conversation> {

    public ConversationsLoader(Context context, Account account, boolean loadCirclesOnly) {
        super(context, Conversation.Indices.class, Conversations.CONTENT_URI, Conversations.COLUMNS,
                null, null, new OrderBy(Conversations.UPDATED_AT, false).getSQL());
        final Expression where;
        final String[] whereArgs;
        if (loadCirclesOnly) {
            where = Expression.and(Expression.equalsArgs(Conversations.ACCOUNT_ID),
                    Expression.equalsArgs(Conversations.RECIPIENT_TYPE));
            whereArgs = new String[]{Utils.getAccountId(context, account),
                    Message.RecipientType.CIRCLE};
        } else {
            where = Expression.and(Expression.equalsArgs(Conversations.ACCOUNT_ID));
            whereArgs = new String[]{Utils.getAccountId(context, account)};
        }
        setSelection(where.getSQL());
        setSelectionArgs(whereArgs);
    }

}
