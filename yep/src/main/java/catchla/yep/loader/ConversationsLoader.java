package catchla.yep.loader;

import android.accounts.Account;
import android.content.Context;

import org.mariotaku.sqliteqb.library.Expression;
import org.mariotaku.sqliteqb.library.OrderBy;

import catchla.yep.model.Conversation;
import catchla.yep.provider.YepDataStore.Conversations;
import catchla.yep.util.Utils;

/**
 * Created by mariotaku on 14-8-5.
 */
public class ConversationsLoader extends ObjectCursorLoader<Conversation> {

    public ConversationsLoader(Context context, Account account) {
        super(context, Conversation.Indices.class, Conversations.CONTENT_URI, Conversations.COLUMNS,
                null, null, new OrderBy(Conversations.UPDATED_AT, false).getSQL());
        setSelection(Expression.equalsArgs(Conversations.ACCOUNT_ID).getSQL());
        setSelectionArgs(new String[]{Utils.getAccountId(context, account)});
    }

}
