package catchla.yep.loader;

import android.accounts.Account;
import android.content.Context;
import android.database.Cursor;
import android.database.MergeCursor;

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

    private final String mAccountId;
    private final boolean mDisplayCircleEntry;

    public ConversationsLoader(Context context, Account account, String recipientType, boolean displayCircleEntry) {
        super(context, Conversation.Indices.class, Conversations.CONTENT_URI, Conversations.COLUMNS,
                null, null, new OrderBy(Conversations.UPDATED_AT, false).getSQL());
        mDisplayCircleEntry = displayCircleEntry;
        final Expression where;
        final String[] whereArgs;
        mAccountId = Utils.getAccountId(context, account);
        if (recipientType != null) {
            where = Expression.and(Expression.equalsArgs(Conversations.ACCOUNT_ID),
                    Expression.equalsArgs(Conversations.RECIPIENT_TYPE));
            whereArgs = new String[]{mAccountId, recipientType};
        } else {
            where = Expression.and(Expression.equalsArgs(Conversations.ACCOUNT_ID));
            whereArgs = new String[]{mAccountId};
        }
        setSelection(where.getSQL());
        setSelectionArgs(whereArgs);
    }

    @Override
    protected Cursor query() {
        if (!mDisplayCircleEntry) return super.query();
        final Cursor original = super.query();
        final Expression where = Expression.and(Expression.equalsArgs(Conversations.ACCOUNT_ID),
                Expression.equalsArgs(Conversations.RECIPIENT_TYPE));
        final String[] whereArgs = new String[]{mAccountId, Message.RecipientType.CIRCLE};
        final Cursor chatEntry = getContext().getContentResolver().query(mUri, mProjection,
                where.getSQL(), whereArgs, mSortOrder);
        return new MergeCursor(new Cursor[]{chatEntry, original});
    }
}
