package catchla.yep.service

import android.accounts.Account
import android.app.Service
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.RemoteException
import android.util.Log
import catchla.yep.BuildConfig
import catchla.yep.Constants
import catchla.yep.IFayeService
import catchla.yep.model.*
import catchla.yep.model.message.LastSeenMessage
import catchla.yep.provider.YepDataStore.Conversations
import catchla.yep.provider.YepDataStore.Messages
import catchla.yep.util.JsonSerializer
import catchla.yep.util.Utils
import catchla.yep.util.YepAPIFactory
import catchla.yep.util.dagger.GeneralComponentHelper
import com.bluelinelabs.logansquare.LoganSquare
import com.bluelinelabs.logansquare.annotation.JsonField
import com.bluelinelabs.logansquare.annotation.JsonObject
import com.bluelinelabs.logansquare.typeconverters.TypeConverter
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.squareup.otto.Bus
import okhttp3.Request
import okhttp3.ws.WebSocketCall
import org.mariotaku.okfaye.Extension
import org.mariotaku.okfaye.Faye
import org.mariotaku.sqliteqb.library.Expression
import java.io.IOException
import java.lang.ref.WeakReference
import java.util.*
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import javax.inject.Inject

class FayeService : Service(), Constants {

    @Inject
    lateinit var mBus: Bus
    lateinit var mHandler: Handler
    internal var mExecutor: Executor = Executors.newSingleThreadExecutor()
    internal var mTimer = Timer(true)

    internal var mInstantStateTask = WeakHashMap<String, TimerTask>()
    private var mFayeClient: Faye? = null

    private fun updateLastReedLastSeen(cr: ContentResolver,
                                       accountId: String, conversationId: String,
                                       lastRead: Long, lastSeen: Long) {
        if (lastRead < 0 && lastSeen < 0) return
        val lastReadValues = ContentValues()
        if (lastRead >= 0) {
            val markReadValues = ContentValues()
            markReadValues.put(Messages.STATE, Messages.MessageState.READ)
            val markReadWhere = Expression.and(
                    Expression.equalsArgs(Messages.CONVERSATION_ID),
                    Expression.equalsArgs(Messages.STATE),
                    Expression.lesserEqualsArgs(Messages.CREATED_AT)).sql
            val markReadWhereArgs = arrayOf(conversationId, Messages.MessageState.UNREAD, lastRead.toString())
            cr.update(Messages.CONTENT_URI, markReadValues,
                    markReadWhere, markReadWhereArgs)

            lastReadValues.put(Conversations.LAST_READ_AT, lastRead)
        }

        if (lastSeen >= 0) {
            lastReadValues.put(Conversations.LAST_SEEN_AT, lastSeen)
        }

        val lastSeenWhere = Expression.and(
                Expression.equalsArgs(Conversations.ACCOUNT_ID),
                Expression.equalsArgs(Conversations.CONVERSATION_ID)).sql
        val lastSeenWhereArgs = arrayOf(accountId, conversationId)
        cr.update(Conversations.CONTENT_URI, lastReadValues,
                lastSeenWhere, lastSeenWhereArgs)
    }

    override fun onCreate() {
        super.onCreate()
        mHandler = Handler(Looper.getMainLooper())
        GeneralComponentHelper.build(this).inject(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent == null) return Service.START_NOT_STICKY
        if (mFayeClient != null) {
            return Service.START_STICKY
        }
        val account = intent.getParcelableExtra<Account>(Constants.EXTRA_ACCOUNT)
        val client = YepAPIFactory.getOkHttpClient(this)
        val builder = Request.Builder()
        builder.url(BuildConfig.API_ENDPOINT_FAYE)
        val authToken = YepAPIFactory.getAuthToken(this, account)
        val accountId = Utils.getAccountId(this, account)

        mFayeClient = Faye.create(client, WebSocketCall.create(client, builder.build()))
        val extension = YepFayeExtension()
        extension.version = "v1"
        extension.accessToken = authToken
        mFayeClient!!.setExtension(extension)
        mFayeClient!!.setErrorListener { e, code, message ->
            if (e != null) {
                Log.w(Constants.LOGTAG, String.format(Locale.ROOT, "%d: %s", code, message), e)
            } else {
                Log.w(Constants.LOGTAG, String.format(Locale.ROOT, "%d: %s", code, message))
            }
        }
        val userChannel = String.format(Locale.ROOT, "/v1/users/%s/messages", accountId)
        mFayeClient!!.subscribe(userChannel) { json ->
            Log.d(Constants.LOGTAG, json.toString())

            consumeMessage(json, MessageType::class.java) { parsed ->
                when (parsed.messageType) {
                    "message" -> {
                        consumeMessage(json, ImMessage::class.java) { msg ->
                            val response = ConversationsResponse()
                            val sender = msg.message!!.sender
                            if (sender != null) {
                                response.users = listOf(sender)
                            }
                            val circle = msg.message!!.circle
                            if (circle != null) {
                                response.circles = listOf(circle)
                            }
                            response.messages = listOf(msg.message)
                            MessageService.insertConversations(this@FayeService, response, accountId)
                        }
                    }
                    "mark_as_read" -> {
                        consumeMessage(json, MarkAsRead::class.java) { msg ->
                            val markAsRead = msg.markAsRead
                            val cr = contentResolver
                            val recipientId = markAsRead!!.recipientId
                            val recipientType = markAsRead.recipientType
                            val lastRead = markAsRead.lastReadAt.time
                            val lastSeen = System.currentTimeMillis()
                            val conversationId = Conversation.generateId(recipientType,
                                    recipientId)

                            updateLastReedLastSeen(cr, accountId, conversationId,
                                    lastRead, lastSeen)
                            postMessage(LastSeenMessage(accountId, conversationId,
                                    lastSeen))
                        }
                    }
                    "instant_state" -> {
                        consumeMessage(json, InstantState::class.java) { msg ->
                            val instantState = msg.instantState
                            val user = instantState!!.user
                            postMessage(instantState)

                            val recipientType = instantState.recipientType
                            val recipientId = user.id
                            val conversationId = Conversation.generateId(recipientType,
                                    recipientId)
                            val taskKey = accountId + conversationId
                            if (!mInstantStateTask.containsKey(taskKey)) {
                                val task = object : TimerTask() {
                                    override fun run() {
                                        val cr = contentResolver
                                        val lastSeen = System.currentTimeMillis()
                                        updateLastReedLastSeen(cr, accountId, conversationId,
                                                -1, lastSeen)
                                        postMessage(LastSeenMessage(accountId, conversationId,
                                                lastSeen))
                                        mInstantStateTask.remove(taskKey)
                                    }
                                }
                                mInstantStateTask.put(taskKey, task)
                                mTimer.schedule(task, 1000L)
                            }
                        }
                    }
                }
            }
        }
        return Service.START_REDELIVER_INTENT
    }

    private fun <T> consumeMessage(json: String?, type: Class<T>,
                                   consumer: (parsed: T) -> Unit) {
        val parsed = JsonSerializer.parse(json, type) ?: return
        consumer(parsed)
    }

    override fun onDestroy() {
        if (mFayeClient != null) {
            mExecutor.execute { mFayeClient!!.disconnect() }
        }
        super.onDestroy()
    }

    override fun onBind(intent: Intent): IBinder? {
        return IFayeServiceBinder(this)
    }

    private fun postMessage(obj: Any) {
        mHandler.post { mBus.post(obj) }
    }

    private fun sendMessage(messageType: String, channel: String, message: Any): Boolean {
        if (mFayeClient == null) return false
        mExecutor.execute {
            val fayeSend = FayeSend()
            fayeSend.messageType = messageType
            fayeSend.message = message
            mFayeClient!!.publish(channel, fayeSend) { response -> Log.d(Constants.LOGTAG, "Instant message returns " + response) }
        }
        return true
    }

    private class IFayeServiceBinder internal constructor(service: FayeService) : IFayeService.Stub() {

        private val mReference: WeakReference<FayeService>

        init {
            mReference = WeakReference(service)
        }

        @Throws(RemoteException::class)
        override fun instantState(conversation: Conversation, type: String): Boolean {
            val service = mReference.get()
            val message = InstantStateMessage.create(type)
            message.recipientId = conversation.recipientId
            message.recipientType = conversation.recipientType
            return service.sendMessage("instant_state", "/messages", message)
        }
    }

    @JsonObject
    internal class ImMessage {
        @JsonField(name = arrayOf("message"))
        var message: Message? = null

        override fun toString(): String {
            return "ImMessage{message=$message}"
        }
    }

    @JsonObject
    internal class MarkAsRead {
        @JsonField(name = arrayOf("message"))
        var markAsRead: MarkAsReadMessage? = null

        override fun toString(): String {
            return "MarkAsRead{markAsRead=$markAsRead}"
        }
    }

    @JsonObject
    internal class InstantState {
        @JsonField(name = arrayOf("message"))
        var instantState: InstantStateMessage? = null

        override fun toString(): String {
            return "InstantState{instantState=$instantState}"
        }
    }

    @JsonObject
    internal class FayeSend {
        @JsonField(name = arrayOf("message_type"))
        var messageType: String? = null

        @JsonField(name = arrayOf("message"), typeConverter = MessageSerializer::class)
        var message: Any? = null

        override fun toString(): String {
            return "FayeSend{messageType='$messageType', message=$message}"
        }
    }

    /**
     * Created by mariotaku on 16/6/1.
     */
    @JsonObject
    internal class YepFayeExtension : Extension() {
        @JsonField(name = arrayOf("version"))
        lateinit var version: String
        @JsonField(name = arrayOf("access_token"))
        lateinit var accessToken: String

    }

    internal class MessageSerializer : TypeConverter<Any> {

        @Throws(IOException::class)
        override fun parse(jsonParser: JsonParser): Any? {
            return null
        }

        @Throws(IOException::class)
        override fun serialize(obj: Any?, fieldName: String, writeFieldNameForObject: Boolean,
                               jsonGenerator: JsonGenerator) {
            if (obj == null) return
            if (writeFieldNameForObject) {
                jsonGenerator.writeFieldName(fieldName)
            }
            //noinspection unchecked
            LoganSquare.mapperFor(obj.javaClass).serialize(obj, jsonGenerator, true)
        }
    }
}
