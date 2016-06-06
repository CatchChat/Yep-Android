package catchla.yep.loader

import android.accounts.Account
import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
import android.database.MergeCursor
import catchla.yep.model.Conversation
import catchla.yep.model.ConversationCursorIndices
import catchla.yep.model.ConversationTableInfo
import catchla.yep.model.Message
import catchla.yep.provider.YepDataStore.Conversations
import catchla.yep.util.Utils
import org.mariotaku.sqliteqb.library.Expression
import org.mariotaku.sqliteqb.library.OrderBy
import java.util.*

/**
 * Created by mariotaku on 14-8-5.
 */
class ConversationsLoader(context: Context, account: Account, recipientType: String?, private val displayCircleEntry: Boolean) : ObjectCursorLoader<Conversation>(context, ConversationCursorIndices::class.java, Conversations.CONTENT_URI, ConversationTableInfo.COLUMNS, null, null, OrderBy(Conversations.UPDATED_AT, false).sql) {

    private val accountId: String

    init {
        accountId = Utils.getAccountId(context, account)
        val whereConditions = ArrayList<Expression>()
        val whereArgs = ArrayList<String>()
        whereConditions.add(Expression.equalsArgs(Conversations.ACCOUNT_ID))
        whereConditions.add(Expression.notEqualsArgs(Conversations.CONVERSATION_ID))
        whereArgs.add(accountId)
        whereArgs.add(Conversation.generateId(Message.RecipientType.USER, accountId))
        if (recipientType != null) {
            whereConditions.add(Expression.equalsArgs(Conversations.RECIPIENT_TYPE))
            whereArgs.add(recipientType)
        }
        selection = Expression.and(*whereConditions.toTypedArray()).sql
        selectionArgs = whereArgs.toTypedArray()
    }

    override fun query(): Cursor {
        if (!displayCircleEntry) return super.query()
        val original = super.query()
        val where = Expression.and(Expression.equalsArgs(Conversations.ACCOUNT_ID),
                Expression.equalsArgs(Conversations.RECIPIENT_TYPE))
        val whereArgs = arrayOf(accountId, Message.RecipientType.CIRCLE)
        @SuppressLint("Recycle") val chatEntry = context.contentResolver.query(mUri, mProjection,
                where.sql, whereArgs, mSortOrder)
        return MergeCursor(arrayOf(chatEntry, original))
    }
}
