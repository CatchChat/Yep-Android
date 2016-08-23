package catchla.yep.activity

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.RemoteException
import android.support.v4.app.Fragment
import android.text.TextUtils
import android.text.format.DateUtils.getRelativeTimeSpanString
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import catchla.yep.Constants.*
import catchla.yep.R
import catchla.yep.annotation.PathRecipientType
import catchla.yep.extension.Bundle
import catchla.yep.extension.account
import catchla.yep.extension.set
import catchla.yep.extension.subtitle
import catchla.yep.fragment.ChatListFragment
import catchla.yep.fragment.ConversationChatListFragment
import catchla.yep.model.Conversation
import catchla.yep.model.InstantStateMessage
import catchla.yep.model.LastReadResponse
import catchla.yep.model.Message
import catchla.yep.model.message.LastSeenMessage
import catchla.yep.provider.YepDataStore.Conversations
import catchla.yep.provider.YepDataStore.Messages
import catchla.yep.util.Utils
import catchla.yep.util.YepAPIFactory
import com.squareup.otto.Subscribe
import nl.komponents.kovenant.task
import org.mariotaku.sqliteqb.library.Expression
import org.mariotaku.sqliteqb.library.OrderBy
import java.util.*

/**
 * Created by mariotaku on 16/8/23.
 */
class ChatActivity : AbsChatActivity() {

    private val handler by lazy { Handler(Looper.getMainLooper()) }

    override var conversation: Conversation
        get () = intent.getParcelableExtra<Conversation>(EXTRA_CONVERSATION)
        set(value) {
            intent.putExtra(EXTRA_CONVERSATION, value)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        title = Utils.getConversationName(conversation)

        if (savedInstanceState == null) {
            markAsRead(conversation)
        }
        displayLastSeen()
    }

    override fun onDestroy() {
        handler.removeCallbacksAndMessages(null)
        super.onDestroy()
    }

    override fun onStart() {
        super.onStart()
        bus.register(this)
    }

    override fun onStop() {
        bus.unregister(this)
        super.onStop()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_chat, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.profile -> {
                val intent = Intent(this, UserActivity::class.java)
                intent.putExtra(EXTRA_ACCOUNT, account)
                intent.putExtra(EXTRA_USER, conversation.user)
                startActivity(intent)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onTypingText() {
        try {
            fayeService?.instantState(conversation, "typing")
        } catch (e: RemoteException) {
            Log.w(LOGTAG, e)
        }
    }

    override fun instantiateChatListFragment(): ChatListFragment {
        val args = Bundle {
            this[EXTRA_CONVERSATION] = conversation
            this[EXTRA_ACCOUNT] = account
        }
        return Fragment.instantiate(this, ConversationChatListFragment::class.java.name, args) as ChatListFragment
    }

    private fun markAsRead(conversation: Conversation) {
        task {
            val projection = arrayOf(Messages.MESSAGE_ID, Messages.TEXT_CONTENT)
            val incomingWhere = Expression.and(
                    Expression.equalsArgs(Messages.ACCOUNT_ID),
                    Expression.equalsArgs(Messages.CONVERSATION_ID),
                    Expression.equalsArgs(Messages.RECIPIENT_ID))
            val whereArgs = arrayOf(conversation.accountId, conversation.id, conversation.accountId)
            val orderBy = OrderBy(Messages.CREATED_AT, false)
            val cr = contentResolver
            val incoming = cr.query(Messages.CONTENT_URI, projection,
                    incomingWhere.sql, whereArgs, orderBy.sql)!!
            val lastId: String?
            try {
                lastId = if (incoming.moveToFirst()) incoming.getString(0) else null
            } finally {
                incoming.close()
            }
            val yep = YepAPIFactory.getInstance(this, account)
            val lastReadAt = lastId?.let {
                val recipientType = conversation.recipientType
                val recipientId = conversation.recipientId
                val lastReadResponse: LastReadResponse
                when (recipientType) {
                    Message.RecipientType.CIRCLE -> {
                        lastReadResponse = yep.getSentLastRead(PathRecipientType.CIRCLES, recipientId)
                        yep.batchMarkAsRead(PathRecipientType.CIRCLES, recipientId, it)
                    }
                    Message.RecipientType.USER -> {
                        lastReadResponse = yep.getSentLastRead(PathRecipientType.USERS, recipientId)
                        yep.batchMarkAsRead(PathRecipientType.USERS, recipientId, it)
                    }
                    else -> {
                        throw UnsupportedOperationException(recipientType)
                    }
                }
                lastReadResponse.lastReadAt
            }
            if (lastReadAt != null) {
                val markAsReadValues = ContentValues()
                markAsReadValues.put(Messages.STATE, Messages.MessageState.READ)
                val outgoingWhere = Expression.and(
                        Expression.equalsArgs(Messages.ACCOUNT_ID),
                        Expression.equalsArgs(Messages.CONVERSATION_ID),
                        Expression.equalsArgs(Messages.STATE),
                        Expression.lesserEqualsArgs(Messages.CREATED_AT),
                        Expression.isNotArgs(Messages.RECIPIENT_ID))
                val outgoingWhereArgs = arrayOf(conversation.accountId, conversation.id,
                        Messages.MessageState.UNREAD, lastReadAt.time.toString(), conversation.accountId)
                cr.update(Messages.CONTENT_URI, markAsReadValues, outgoingWhere.sql,
                        outgoingWhereArgs)

                val conversationWhere = Expression.and(
                        Expression.equalsArgs(Conversations.ACCOUNT_ID),
                        Expression.equalsArgs(Conversations.CONVERSATION_ID))

                val conversationWhereArgs = arrayOf(conversation.accountId, conversation.id)
                val lastSeenValues = ContentValues()
                lastSeenValues.put(Conversations.LAST_SEEN_AT, lastReadAt.time)
                cr.update(Conversations.CONTENT_URI, lastSeenValues, conversationWhere.sql,
                        conversationWhereArgs)
            }
        }
    }

    private fun displayLastSeen() {
        val lastSeenAt = conversation.lastSeenAt
        if (lastSeenAt != null) {
            subtitle = (getRelativeTimeSpanString(lastSeenAt.time))
        } else {
            subtitle = null
        }
    }

    @Subscribe
    fun onReceivedInstantStateMessage(message: InstantStateMessage) {
        if (Message.RecipientType.USER == conversation.recipientType) {
            if (TextUtils.equals(conversation.user.id, message.user.id)) {
                subtitle = getString(R.string.typing)
                handler.postDelayed({ displayLastSeen() }, 2000)
            }
        }
    }

    @Subscribe
    fun onReceivedLastSeenMessage(message: LastSeenMessage) {
        if (!message.isConversation(conversation)) return
        conversation.lastSeenAt = Date(message.lastSeen)
        this.conversation = conversation
        displayLastSeen()
    }
}