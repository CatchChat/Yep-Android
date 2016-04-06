package catchla.yep.loader;

import android.accounts.Account;
import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.MergeCursor;

import org.mariotaku.sqliteqb.library.Expression;
import org.mariotaku.sqliteqb.library.OrderBy;

import java.util.ArrayList;
import java.util.List;

import catchla.yep.model.Conversation;
import catchla.yep.model.ConversationCursorIndices;
import catchla.yep.model.ConversationTableInfo;
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
        super(context, ConversationCursorIndices.class, Conversations.CONTENT_URI, ConversationTableInfo.COLUMNS,
                null, null, new OrderBy(Conversations.UPDATED_AT, false).getSQL());
        mDisplayCircleEntry = displayCircleEntry;
        mAccountId = Utils.getAccountId(context, account);
        List<Expression> whereConditions = new ArrayList<>();
        List<String> whereArgs = new ArrayList<>();
        whereConditions.add(Expression.equalsArgs(Conversations.ACCOUNT_ID));
        whereConditions.add(Expression.notEqualsArgs(Conversations.CONVERSATION_ID));
        whereArgs.add(mAccountId);
        whereArgs.add(Conversation.generateId(Message.RecipientType.USER, mAccountId));
        if (recipientType != null) {
            whereConditions.add(Expression.equalsArgs(Conversations.RECIPIENT_TYPE));
            whereArgs.add(recipientType);
        }
        setSelection(Expression.and(whereConditions.toArray(new Expression[whereConditions.size()])).getSQL());
        setSelectionArgs(whereArgs.toArray(new String[whereArgs.size()]));
    }

    @Override
    protected Cursor query() {
        if (!mDisplayCircleEntry) return super.query();
        final Cursor original = super.query();
        final Expression where = Expression.and(Expression.equalsArgs(Conversations.ACCOUNT_ID),
                Expression.equalsArgs(Conversations.RECIPIENT_TYPE));
        final String[] whereArgs = new String[]{mAccountId, Message.RecipientType.CIRCLE};
        @SuppressLint("Recycle") final Cursor chatEntry = getContext().getContentResolver().query(mUri, mProjection,
                where.getSQL(), whereArgs, mSortOrder);
        return new MergeCursor(new Cursor[]{chatEntry, original});
    }
}
