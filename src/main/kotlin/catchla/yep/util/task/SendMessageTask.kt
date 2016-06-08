package catchla.yep.util.task

import android.accounts.Account
import android.content.ContentValues
import android.content.Context
import android.util.Log
import catchla.yep.Constants
import catchla.yep.model.*
import catchla.yep.provider.YepDataStore.Conversations
import catchla.yep.provider.YepDataStore.Messages
import catchla.yep.util.JsonSerializer
import catchla.yep.util.Utils
import catchla.yep.util.YepAPI
import catchla.yep.util.YepAPIFactory
import org.mariotaku.abstask.library.AbstractTask
import org.mariotaku.ktextension.toLong
import org.mariotaku.sqliteqb.library.Expression

/**
 * Created by mariotaku on 15/9/12.
 */
abstract class SendMessageTask<H>(val context: Context, val account: Account) : AbstractTask<NewMessage, TaskResponse<Message>, H>(), Constants {
    private val accountUser: User
    protected abstract val mediaType: String

    init {
        this.accountUser = Utils.getAccountUser(context, account)
    }

    public override fun doLongOperation(newMessage: NewMessage): TaskResponse<Message> {
        val yep = YepAPIFactory.getInstance(context, account)
        val draftId = newMessage.randomId()
        try {
            newMessage.mediaType(mediaType)
            saveUnsentMessage(newMessage)
            val attachment = uploadAttachment(yep, newMessage)
            if (attachment != null) {
                newMessage.attachmentId(attachment.id)
            }
            val message = yep.createMessage(newMessage.recipientType(),
                    newMessage.recipientId(), newMessage)
            updateSentMessage(draftId, message)
            return TaskResponse(message)
        } catch (e: YepException) {
            Log.w(Constants.LOGTAG, e)
            val cr = context.contentResolver
            val values = ContentValues()
            values.put(Messages.STATE, Messages.MessageState.FAILED)
            val where = Expression.equalsArgs(Messages.RANDOM_ID).sql
            val whereArgs = arrayOf(draftId)
            cr.update(Messages.CONTENT_URI, values, where, whereArgs)
            return TaskResponse(exception = e)
        } catch (t: Throwable) {
            Log.wtf(Constants.LOGTAG, t)
            throw RuntimeException(t)
        }

    }

    @Throws(YepException::class)
    protected open fun uploadAttachment(yep: YepAPI, newMessage: NewMessage): FileAttachment? {
        return null
    }

    protected abstract fun getLocalMetadata(newMessage: NewMessage): Array<Message.LocalMetadata>?

    private fun saveUnsentMessage(newMessage: NewMessage): Long {
        val cr = context.contentResolver
        newMessage.localMetadata(getLocalMetadata(newMessage))
        val values = newMessage.toDraftValues()
        //TODO update conversation entry
        val inserted = cr.insert(Messages.CONTENT_URI, values)
        val cursor = cr.query(Conversations.CONTENT_URI, ConversationTableInfo.COLUMNS,
                Expression.equalsArgs(Conversations.CONVERSATION_ID).sql,
                arrayOf(newMessage.conversationId()), null)
        val accountId = accountUser.id
        assert(cursor != null)
        if (cursor!!.moveToFirst()) {
            // Conversation entry already exists, so just update latest info
            val entryValues = ContentValues()
            entryValues.put(Conversations.MEDIA_TYPE, newMessage.mediaType())
            entryValues.put(Conversations.UPDATED_AT, newMessage.createdAt())
            entryValues.put(Conversations.TEXT_CONTENT, newMessage.textContent())
            cr.update(Conversations.CONTENT_URI, entryValues, Expression.and(Expression.equalsArgs(Conversations.ACCOUNT_ID),
                    Expression.equalsArgs(Conversations.CONVERSATION_ID)).sql,
                    arrayOf(accountId, newMessage.conversationId()))
        } else {
            // Insert new conversation entry
            val conversation = newMessage.toConversation()
            cr.insert(Conversations.CONTENT_URI, ConversationValuesCreator.create(conversation))
        }
        cursor.close()
        assert(inserted != null)
        return inserted!!.lastPathSegment.toLong(-1)
    }

    private fun updateSentMessage(draftId: String, message: Message) {
        val cr = context.contentResolver
        val values = ContentValues()
        values.put(Messages.ATTACHMENTS, JsonSerializer.serialize(message.attachments,
                Attachment::class.java))
        values.put(Messages.STATE, Messages.MessageState.UNREAD)
        values.put(Messages.MESSAGE_ID, message.id)
        values.put(Messages.CREATED_AT, message.createdAt.time)
        values.put(Messages.LATITUDE, message.latitude)
        values.put(Messages.LONGITUDE, message.longitude)
        val where = Expression.equalsArgs(Messages.RANDOM_ID).sql
        val whereArgs = arrayOf(draftId)
        cr.update(Messages.CONTENT_URI, values, where, whereArgs)
    }

}
