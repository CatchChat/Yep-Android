package catchla.yep.util.content

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import catchla.yep.provider.YepDataStore.Conversations
import catchla.yep.provider.YepDataStore.Messages
import org.mariotaku.sqliteqb.library.*
import org.mariotaku.sqliteqb.library.Columns.Column

/**
 * Created by mariotaku on 16/8/25.
 */
fun createChatHistorySearchCursor(db: SQLiteDatabase, accountId: String, query: String): Cursor {
    val messagesTable = Table(Messages.TABLE_NAME)
    val conversationsTable = Table(Conversations.TABLE_NAME)

    val messageColumns = Columns(
            Column(conversationsTable, Conversations.ACCOUNT_ID, Conversations.ACCOUNT_ID),
            Column(conversationsTable, Conversations.CONVERSATION_ID, Conversations.CONVERSATION_ID),
            Column(messagesTable, Messages.TEXT_CONTENT, Conversations.TEXT_CONTENT),
            Column(conversationsTable, Conversations.USER, Conversations.USER),
            Column(conversationsTable, Conversations.CIRCLE, Conversations.CIRCLE),
            Column(messagesTable, Messages.CREATED_AT, Conversations.UPDATED_AT),
            Column(conversationsTable, Conversations.LAST_SEEN_AT, Conversations.LAST_SEEN_AT),
            Column(conversationsTable, Conversations.LAST_READ_AT, Conversations.LAST_READ_AT),
            Column(conversationsTable, Conversations.RECIPIENT_TYPE, Conversations.RECIPIENT_TYPE),
            Column(messagesTable, Messages.MEDIA_TYPE, Conversations.MEDIA_TYPE),
            Column(messagesTable, Conversations.SENDER, Conversations.SENDER)
    )

    // JOIN conversations ON messages.conversation_id = conversations.conversation_id
    val join = Join(false, Join.Operation.INNER, conversationsTable,
            Expression.equals(Column(messagesTable, Messages.CONVERSATION_ID),
                    Column(conversationsTable, Conversations.CONVERSATION_ID)))

    val whereQuery = Expression.and(
            Expression.equalsArgs(Column(conversationsTable, Conversations.ACCOUNT_ID)),
            Expression.likeRaw(Column(messagesTable, Messages.TEXT_CONTENT), "'%'||?||'%'")
    )
    val rawQuery = SQLQueryBuilder.select(true, messageColumns).from(messagesTable).join(join).where(whereQuery).buildSQL()
    val selectionArgs = arrayOf(accountId, query)
    return db.rawQuery(rawQuery, selectionArgs)
}