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
import nl.komponents.kovenant.Promise
import nl.komponents.kovenant.task
import org.mariotaku.ktextension.toLong
import org.mariotaku.sqliteqb.library.Expression

fun sendMessagePromise(context: Context, account: Account, newMessage: NewMessage, delegate: SendMessageDelegate): Promise<Message, Exception> {
    return task {
        val accountUser = Utils.getAccountUser(context, account)
        fun saveUnsentMessage(newMessage: NewMessage): Long {
            val cr = context.contentResolver
            newMessage.localMetadata(delegate.getLocalMetadata(newMessage))
            val values = newMessage.toDraftValues()
            //TODO update conversation entry
            val inserted = cr.insert(Messages.CONTENT_URI, values)!!
            val cursor = cr.query(Conversations.CONTENT_URI, ConversationTableInfo.COLUMNS,
                    Expression.equalsArgs(Conversations.CONVERSATION_ID).sql,
                    arrayOf(newMessage.conversationId()), null)!!
            val accountId = accountUser.id
            if (cursor.moveToFirst()) {
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
            return inserted.lastPathSegment.toLong(-1)
        }

        fun updateSentMessage(draftId: String, message: Message) {
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

        val yep = YepAPIFactory.getInstance(context, account)
        val draftId = newMessage.randomId()
        try {
            newMessage.mediaType(delegate.mediaType)
            saveUnsentMessage(newMessage)
            val attachment = delegate.uploadAttachment(yep, newMessage)
            if (attachment != null) {
                newMessage.attachmentId(attachment.id)
            }
            val message = yep.createMessage(newMessage.recipientType(),
                    newMessage.recipientId(), newMessage)
            updateSentMessage(draftId, message)
            return@task message
        } catch (e: YepException) {
            Log.w(Constants.LOGTAG, e)
            val cr = context.contentResolver
            val values = ContentValues()
            values.put(Messages.STATE, Messages.MessageState.FAILED)
            val where = Expression.equalsArgs(Messages.RANDOM_ID).sql
            val whereArgs = arrayOf(draftId)
            cr.update(Messages.CONTENT_URI, values, where, whereArgs)
            throw e
        }


    }
}

interface SendMessageDelegate {
    val mediaType: String
    @Throws(YepException::class)
    fun uploadAttachment(yep: YepAPI, newMessage: NewMessage): FileAttachment? {
        return null
    }

    fun getLocalMetadata(newMessage: NewMessage): Array<Message.LocalMetadata>? {
        return null
    }

}