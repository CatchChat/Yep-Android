/*
 * Copyright (c) 2015. Catch Inc,
 */

package catchla.yep.activity

import android.content.*
import android.os.*
import android.support.annotation.WorkerThread
import android.support.v4.app.Fragment
import android.text.TextUtils
import android.text.format.DateUtils
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import catchla.yep.Constants
import catchla.yep.IFayeService
import catchla.yep.R
import catchla.yep.annotation.PathRecipientType
import catchla.yep.fragment.ChatInputBarFragment
import catchla.yep.fragment.ChatListFragment
import catchla.yep.fragment.ConversationChatListFragment
import catchla.yep.model.*
import catchla.yep.model.Message
import catchla.yep.model.message.LastSeenMessage
import catchla.yep.provider.YepDataStore.Conversations
import catchla.yep.provider.YepDataStore.Messages
import catchla.yep.service.FayeService
import catchla.yep.util.ThemeUtils
import catchla.yep.util.Utils
import catchla.yep.util.YepAPIFactory
import com.squareup.otto.Subscribe
import kotlinx.android.synthetic.main.activity_chat.*
import org.mariotaku.abstask.library.AbstractTask
import org.mariotaku.abstask.library.TaskStarter
import org.mariotaku.sqliteqb.library.Expression
import org.mariotaku.sqliteqb.library.OrderBy
import java.util.*

/**
 * Created by mariotaku on 15/4/30.
 */
class ChatActivity : SwipeBackContentActivity(), Constants, ChatInputBarFragment.Listener, ServiceConnection {

    private val handler by lazy { Handler(Looper.getMainLooper()) }
    private var fayeService: IFayeService? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        val actionBar = supportActionBar!!
        actionBar.setDisplayHomeAsUpEnabled(true)

        val primaryColor = ThemeUtils.getColorFromAttribute(this, R.attr.colorPrimary, 0)
        actionBar.setBackgroundDrawable(ThemeUtils.getActionBarBackground(primaryColor, true))

        mainContent?.setStatusBarColorDarken(primaryColor)

        val conversation = conversation!!
        title = Utils.getConversationName(conversation)

        val fragmentArgs = Bundle()

        val circle = conversation.circle

        fragmentArgs.putParcelable(Constants.EXTRA_CONVERSATION, conversation)
        fragmentArgs.putParcelable(Constants.EXTRA_ACCOUNT, account)
        if (circle != null) {
            fragmentArgs.putParcelable(Constants.EXTRA_TOPIC, circle.topic)
        }
        val chatListFragment = Fragment.instantiate(this,
                ConversationChatListFragment::class.java.name, fragmentArgs)
        val chatInputBarFragment = Fragment.instantiate(this,
                ChatInputBarFragment::class.java.name, fragmentArgs) as ChatInputBarFragment

        chatInputBarFragment.listener = this

        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.listContainer, chatListFragment)
        ft.replace(R.id.inputPanel, chatInputBarFragment)
        ft.commit()

        if (savedInstanceState == null) {
            markAsRead(conversation)
        }
        title = Utils.getConversationName(conversation)
        displayLastSeen()
    }

    private fun displayLastSeen() {
        val conversation = conversation ?: return
        val lastSeenAt = conversation.lastSeenAt
        if (lastSeenAt != null) {
            setSubtitle(DateUtils.getRelativeTimeSpanString(lastSeenAt.time))
        } else {
            setSubtitle(null)
        }
    }

    private fun setSubtitle(subtitle: CharSequence?) {
        val actionBar = supportActionBar ?: return
        actionBar.subtitle = subtitle
    }

    private val conversation: Conversation?
        get() = intent.getParcelableExtra<Conversation>(Constants.EXTRA_CONVERSATION)

    private fun markAsRead(conversation: Conversation) {
        TaskStarter.execute(object : AbstractTask<Any, Any, Any>() {
            public override fun doLongOperation(param: Any?): Any? {
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
                val lastId: String
                try {
                    if (!incoming.moveToFirst()) return null
                    lastId = incoming.getString(0)
                } finally {
                    incoming.close()
                }
                val yep = YepAPIFactory.getInstance(this@ChatActivity, Utils.getCurrentAccount(this@ChatActivity))
                try {
                    val recipientType = conversation.recipientType
                    val recipientId = conversation.recipientId
                    val lastReadResponse: LastReadResponse
                    when (recipientType) {
                        Message.RecipientType.CIRCLE -> {
                            lastReadResponse = yep.getSentLastRead(PathRecipientType.CIRCLES, recipientId)
                            yep.batchMarkAsRead(PathRecipientType.CIRCLES, recipientId, lastId)
                        }
                        Message.RecipientType.USER -> {
                            lastReadResponse = yep.getSentLastRead(PathRecipientType.USERS, recipientId)
                            yep.batchMarkAsRead(PathRecipientType.USERS, recipientId, lastId)
                        }
                        else -> {
                            throw UnsupportedOperationException(recipientType)
                        }
                    }
                    val lastReadAt = lastReadResponse.lastReadAt
                    if (lastReadAt != null) {
                        val markAsReadValues = ContentValues()
                        markAsReadValues.put(Messages.STATE, Messages.MessageState.READ)
                        val outgoingWhere = Expression.and(
                                Expression.equalsArgs(Messages.ACCOUNT_ID),
                                Expression.equalsArgs(Messages.CONVERSATION_ID),
                                Expression.equalsArgs(Messages.STATE),
                                Expression.lesserEqualsArgs(Messages.CREATED_AT),
                                Expression.isNotArgs(Messages.RECIPIENT_ID))
                        val outgoingWhereArgs = arrayOf(conversation.accountId, conversation.id, Messages.MessageState.UNREAD, lastReadAt.time.toString(), conversation.accountId)
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
                } catch (e: YepException) {
                    Log.w(Constants.LOGTAG, e)
                } catch (t: Throwable) {
                    Log.wtf(Constants.LOGTAG, t)
                }

                return null
            }
        })
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.profile -> {
                val intent = Intent(this, UserActivity::class.java)
                intent.putExtra(Constants.EXTRA_ACCOUNT, account)
                intent.putExtra(Constants.EXTRA_USER, conversation!!.user)
                startActivity(intent)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_chat, menu)
        return true
    }

    override fun onRecordStarted() {
        voiceWaveContainer.visibility = View.VISIBLE
        voiceWaveView.startRecording()
    }

    @WorkerThread
    override fun postSetAmplitude(amplitude: Int) {
        runOnUiThread { voiceWaveView.amplitude = amplitude }
    }

    override fun onRecordStopped() {
        voiceWaveContainer.visibility = View.GONE
        voiceWaveView.stopRecording()
    }

    override fun onMessageSentFinished(result: TaskResponse<Message>) {

    }

    @Subscribe
    fun onReceivedInstantStateMessage(message: InstantStateMessage) {
        val conversation = conversation
        if (Message.RecipientType.USER == conversation!!.recipientType) {
            if (TextUtils.equals(conversation.user.id, message.user.id)) {
                setSubtitle(getString(R.string.typing))
                handler.postDelayed({ displayLastSeen() }, 2000)
            }
        }
    }

    @Subscribe
    fun onReceivedLastSeenMessage(message: LastSeenMessage) {
        val conversation = conversation
        if (conversation == null || !message.isConversation(conversation)) return
        conversation.lastSeenAt = Date(message.lastSeen)
        intent.putExtra(Constants.EXTRA_CONVERSATION, conversation)
        displayLastSeen()
    }

    override fun onDestroy() {
        handler.removeCallbacksAndMessages(null)
        super.onDestroy()
    }

    override fun onStart() {
        super.onStart()
        bindService(Intent(this, FayeService::class.java), this, Context.BIND_AUTO_CREATE)
        bus.register(this)
    }

    override fun onStop() {
        bus.unregister(this)
        unbindService(this)
        super.onStop()
    }

    override fun onMessageSentStarted(newMessage: NewMessage) {
        val chatListFragment = supportFragmentManager.findFragmentById(R.id.listContainer) as ChatListFragment
        chatListFragment.scrollToStart()
        chatListFragment.jumpToLast = true
    }

    override fun onTypingText() {
        try {
            fayeService?.instantState(conversation, "typing")
        } catch (e: RemoteException) {
            Log.w(Constants.LOGTAG, e)
        }

    }

    override fun onServiceConnected(name: ComponentName, service: IBinder) {
        fayeService = IFayeService.Stub.asInterface(service)
    }

    override fun onServiceDisconnected(name: ComponentName) {
        fayeService = null
    }
}
