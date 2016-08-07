package catchla.yep.service

import android.accounts.Account
import android.app.Service
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.support.v4.util.SimpleArrayMap
import android.text.TextUtils
import android.util.Log
import catchla.yep.BuildConfig
import catchla.yep.Constants
import catchla.yep.message.FriendshipsRefreshedEvent
import catchla.yep.message.MessageRefreshedEvent
import catchla.yep.model.*
import catchla.yep.provider.YepDataStore.*
import catchla.yep.util.Utils
import catchla.yep.util.YepAPIFactory
import catchla.yep.util.dagger.GeneralComponentHelper
import com.squareup.otto.Bus
import org.mariotaku.abstask.library.AbstractTask
import org.mariotaku.abstask.library.TaskStarter
import org.mariotaku.ktextension.bulkInsertSliced
import org.mariotaku.sqliteqb.library.Expression
import java.util.*
import javax.inject.Inject

/**
 * Created by mariotaku on 15/5/29.
 */
class MessageService : Service(), Constants {

    @Inject
    lateinit var bus: Bus

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        GeneralComponentHelper.build(this).inject(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent == null) return Service.START_NOT_STICKY
        val action = intent.action ?: return Service.START_NOT_STICKY
        when (action) {
            ACTION_REFRESH_FRIENDSHIPS -> {
                val account = intent.getParcelableExtra<Account>(Constants.EXTRA_ACCOUNT)
                refreshFriendships(account)
            }
            ACTION_REFRESH_MESSAGES -> {
                refreshCircles()
                refreshMessages()
            }
            ACTION_REFRESH_USER_INFO -> {
                val account = intent.getParcelableExtra<Account>(Constants.EXTRA_ACCOUNT)
                refreshUserInfo(account)
            }
        }
        return Service.START_STICKY
    }

    private fun refreshUserInfo(account: Account?) {
        if (account == null) return

        val task = object : AbstractTask<Account, TaskResponse<User>, MessageService>() {

            public override fun doLongOperation(account: Account): TaskResponse<User> {
                val yep = YepAPIFactory.getInstance(application, account)
                try {
                    return TaskResponse(yep.getUser())
                } catch (e: YepException) {
                    return TaskResponse(exception = e)
                }

            }

            public override fun afterExecute(handler: MessageService?, result: TaskResponse<User>) {
                if (result.data != null) {
                    Utils.saveUserInfo(handler!!, account, result.data)
                }
            }
        }
        TaskStarter.execute(task)
    }

    private fun refreshFriendships(account: Account?) {
        if (account == null) return
        val task = object : AbstractTask<Account, TaskResponse<Boolean>, MessageService>() {
            public override fun doLongOperation(account: Account): TaskResponse<Boolean> {
                val yep = YepAPIFactory.getInstance(application, account)
                try {
                    var friendships: ResponseList<Friendship>
                    var page = 1
                    val paging = Paging()
                    val values = ArrayList<ContentValues>()
                    val accountId = Utils.getAccountId(application, account)
                    do {
                        friendships = yep.getFriendships(paging)
                        if (friendships.isEmpty()) break
                        for (friendship in friendships) {
                            friendship.accountId = accountId
                            values.add(FriendshipValuesCreator.create(friendship))
                        }
                        paging.page(++page)
                    } while (friendships.count >= friendships.perPage)

                    val cr = contentResolver
                    cr.delete(Friendships.CONTENT_URI, null, null)
                    cr.bulkInsertSliced(Friendships.CONTENT_URI, values)
                    return TaskResponse(true)
                } catch (e: YepException) {
                    Log.w(Constants.LOGTAG, e)
                    return TaskResponse(exception = e)
                } finally {
                }
            }

            public override fun afterExecute(response: TaskResponse<Boolean>?) {
                bus.post(FriendshipsRefreshedEvent())
            }

            public override fun afterExecute(messageService: MessageService?, response: TaskResponse<Boolean>?) {
                afterExecute(response)
            }
        }
        task.setParams(account)
        task.setResultHandler(this)
        TaskStarter.execute(task)
    }

    private fun refreshMessages() {
        val account = Utils.getCurrentAccount(this) ?: return
        val accountUser = Utils.getAccountUser(this, account) ?: return
        val task = object : AbstractTask<Account, TaskResponse<Boolean>, MessageService>() {
            public override fun doLongOperation(account: Account): TaskResponse<Boolean> {
                val yep = YepAPIFactory.getInstance(application, account)
                try {
                    val paging = Paging()
                    paging.perPage(30)
                    val conversations = yep.getConversations(paging)
                    insertConversations(this@MessageService, conversations, accountUser.id)
                    System.identityHashCode(conversations)
                    return TaskResponse(true)
                } catch (e: YepException) {
                    Log.w(Constants.LOGTAG, e)
                    return TaskResponse<Boolean>(exception = e)
                } catch (e: Throwable) {
                    Log.wtf(Constants.LOGTAG, e)
                    return TaskResponse<Boolean>(exception = e)
                } finally {
                }
            }

            public override fun afterExecute(response: TaskResponse<Boolean>?) {
                bus.post(MessageRefreshedEvent())
            }

            public override fun afterExecute(messageService: MessageService?, response: TaskResponse<Boolean>?) {
                afterExecute(response)
            }
        }
        task.setParams(account)
        task.setResultHandler(this)
        TaskStarter.execute(task)
    }

    private fun refreshCircles() {
        val account = Utils.getCurrentAccount(this) ?: return
        val accountUser = Utils.getAccountUser(this, account) ?: return
        val task = object : AbstractTask<Account, TaskResponse<Boolean>, MessageService>() {
            public override fun doLongOperation(account: Account): TaskResponse<Boolean> {
                val yep = YepAPIFactory.getInstance(application, account)
                try {
                    val paging = Paging()
                    val circles = yep.getCircles(paging)
                    val accountId = accountUser.id
                    insertCircles(this@MessageService, circles, accountId)
                    return TaskResponse(true)
                } catch (e: YepException) {
                    Log.w(Constants.LOGTAG, e)
                    return TaskResponse(exception = e)
                } finally {
                }
            }

            public override fun afterExecute(response: TaskResponse<Boolean>?) {
                bus.post(MessageRefreshedEvent())
            }

            public override fun afterExecute(messageService: MessageService?, response: TaskResponse<Boolean>?) {
                afterExecute(response)
            }
        }
        task.setParams(account)
        task.setResultHandler(this)
        TaskStarter.execute(task)
    }

    companion object {

        @JvmField val ACTION_PREFIX = BuildConfig.APPLICATION_ID + "."
        @JvmField val ACTION_REFRESH_MESSAGES = ACTION_PREFIX + "REFRESH_MESSAGES"
        @JvmField val ACTION_REFRESH_USER_INFO = ACTION_PREFIX + "REFRESH_USER_INFO"
        @JvmField val ACTION_REFRESH_FRIENDSHIPS = ACTION_PREFIX + "REFRESH_FRIENDSHIPS"

        fun insertCircles(context: Context, circles: Collection<Circle>, accountId: String) {
            val cr = context.contentResolver
            cr.delete(Circles.CONTENT_URI, null, null)
            val contentValues = ArrayList<ContentValues>()
            for (circle in circles) {
                circle.accountId = accountId
                contentValues.add(CircleValuesCreator.create(circle))
            }
            cr.bulkInsertSliced(Circles.CONTENT_URI, contentValues)
        }

        fun insertConversations(context: Context, conversations: ConversationsResponse,
                                accountId: String) {
            val conversationsMap = HashMap<String, Conversation>()
            val cr = context.contentResolver
            val messages = conversations.messages
            val users = HashMap<String, User>()
            for (user in conversations.users) {
                users.put(user.id, user)
            }
            val messageValues = arrayOfNulls<ContentValues>(messages.size)
            val randomIds = SimpleArrayMap<String, ContentValues>()
            run {
                var i = 0
                val j = messages.size
                while (i < j) {
                    val message = messages[i]
                    val recipientType = message.recipientType
                    val conversationId = Conversation.generateId(message, accountId)
                    message.accountId = accountId
                    message.conversationId = conversationId

                    val values = MessageValuesCreator.create(message)
                    messageValues[i] = values
                    val randomId = message.randomId
                    if (randomId != null) {
                        randomIds.put(randomId, values)
                    }

                    var conversation: Conversation? = conversationsMap[conversationId]
                    val newConversation = conversation == null
                    if (conversation == null) {
                        conversation = Conversation.query(cr, accountId, conversationId)
                        if (conversation == null) {
                            conversation = Conversation()
                            conversation.accountId = accountId
                            conversation.id = conversationId
                        }
                    }
                    val createdAt = message.createdAt
                    if (newConversation || greaterThen(createdAt, conversation.updatedAt)) {
                        conversation.textContent = message.textContent
                        val sender = message.sender
                        if (Message.RecipientType.USER == recipientType) {
                            // Outgoing
                            if (!TextUtils.equals(accountId, sender.id)) {
                                conversation.user = sender
                            } else {
                                val user = users[message.recipientId]
                                if (user != null) {
                                    conversation.user = user
                                }
                            }
                        } else {
                            conversation.user = sender
                        }
                        conversation.sender = sender
                        conversation.circle = getMessageCircle(context, message, conversations, accountId)
                        conversation.updatedAt = createdAt
                        conversation.recipientType = recipientType
                        conversation.mediaType = message.mediaType
                    }
                    if (newConversation && conversation.user != null) {
                        conversationsMap.put(conversationId, conversation)
                    }
                    i++
                }
            }

            val conversationValues = ArrayList<ContentValues>()
            for (conversation in conversationsMap.values) {
                conversationValues.add(ConversationValuesCreator.create(conversation))
            }
            cr.bulkInsertSliced(Conversations.CONTENT_URI, conversationValues)

            val updateSentSelection = Expression.and(Expression.equalsArgs(Messages.ACCOUNT_ID),
                    Expression.equalsArgs(Messages.RANDOM_ID)).sql
            val updateSentSelectionArgs = arrayOf<String?>(accountId, null)
            var i = 0
            val j = randomIds.size()
            while (i < j) {
                updateSentSelectionArgs[i] = randomIds.keyAt(i)
                cr.update(Messages.CONTENT_URI, randomIds.valueAt(i), updateSentSelection,
                        updateSentSelectionArgs)
                i++
            }
            cr.bulkInsertSliced(Messages.CONTENT_URI, messageValues)
        }

        private fun greaterThen(createdAt: Date?, updatedAt: Date?): Boolean {
            if (updatedAt == null) return createdAt != null
            return createdAt != null && createdAt.compareTo(updatedAt) > 0
        }

        private fun getMessageCircle(context: Context, message: Message,
                                     conversations: ConversationsResponse?,
                                     accountId: String): Circle? {
            if (Message.RecipientType.CIRCLE != message.recipientType) return null
            var circle: Circle? = message.circle
            // First try find in conversations
            if (conversations != null) {
                val circles = conversations.circles
                if (circles != null) {
                    for (item in circles) {
                        if (TextUtils.equals(item.id, message.recipientId)) {
                            return item
                        }
                    }
                }
            }
            // Then try to load from database
            val circleId: String
            if (circle == null) {
                circleId = message.recipientId
            } else if (circle.topic == null) {
                circleId = circle.id
            } else {
                return circle
            }
            val where = Expression.and(Expression.equalsArgs(Circles.ACCOUNT_ID),
                    Expression.equalsArgs(Circles.CIRCLE_ID)).sql
            val whereArgs = arrayOf(accountId, circleId)
            val c = context.contentResolver.query(Circles.CONTENT_URI,
                    CircleTableInfo.COLUMNS, where, whereArgs, null)
            try {
                if (c != null && c.moveToFirst()) {
                    val ci = CircleCursorIndices(c)
                    circle = ci.newObject(c)
                }
            } finally {
                Utils.closeSilently(c)
            }
            return circle
        }
    }
}
