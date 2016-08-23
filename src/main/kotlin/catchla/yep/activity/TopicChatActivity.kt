package catchla.yep.activity

import android.content.Intent
import android.support.v4.app.Fragment
import android.view.Menu
import android.view.MenuItem
import catchla.yep.Constants.EXTRA_ACCOUNT
import catchla.yep.Constants.EXTRA_TOPIC
import catchla.yep.R
import catchla.yep.annotation.ItemType
import catchla.yep.extension.Bundle
import catchla.yep.extension.account
import catchla.yep.extension.getUser
import catchla.yep.fragment.ChatListFragment
import catchla.yep.fragment.ReportTypeDialogFragment
import catchla.yep.fragment.TopicChatListFragment
import catchla.yep.model.Conversation
import catchla.yep.model.Message
import catchla.yep.model.Topic
import catchla.yep.util.Utils
import catchla.yep.util.YepAPIFactory
import nl.komponents.kovenant.task
import nl.komponents.kovenant.ui.alwaysUi
import nl.komponents.kovenant.ui.failUi
import nl.komponents.kovenant.ui.successUi
import org.apache.commons.lang3.StringUtils
import org.mariotaku.ktextension.setMenuGroupAvailability

class TopicChatActivity : AbsChatActivity() {

    override val conversation: Conversation by lazy {
        val topic = topic
        val obj = Conversation()
        val accountUser = account.getUser(this)
        obj.accountId = accountUser.id
        obj.circle = topic.circle
        obj.recipientType = Message.RecipientType.CIRCLE
        obj.id = Conversation.generateId(Message.RecipientType.CIRCLE, topic.circle.id)
        return@lazy obj
    }

    private val topic: Topic by lazy {
        intent.getParcelableExtra<Topic>(EXTRA_TOPIC)
    }

    override fun instantiateChatListFragment(): ChatListFragment {
        val args = Bundle {
            putParcelable(EXTRA_ACCOUNT, account)
            putParcelable(EXTRA_TOPIC, topic)
        }
        return Fragment.instantiate(this, TopicChatListFragment::class.java.name, args) as ChatListFragment
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_topic_chat, menu)
        return true
    }

    override fun onTypingText() {

    }

    override fun onMessageSentFinished(result: Message) {
        val f = chatListFragment as TopicChatListFragment
        f.addMessage(result)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.share -> {
                task {
                    val yep = YepAPIFactory.getInstance(this@TopicChatActivity, account)
                    yep.getCircleShareUrl(topic.circle.id)
                }.successUi {
                    val intent = Intent(Intent.ACTION_SEND)
                    intent.putExtra(Intent.EXTRA_TEXT, it.url)
                    intent.type = "text/plain"
                    startActivity(Intent.createChooser(intent, getString(R.string.share)))
                }.failUi {

                }.alwaysUi {
                    // TODO dismiss dialog
                    executeAfterFragmentResumed {

                    }
                }
                return true
            }
            R.id.report_topic -> {
                ReportTypeDialogFragment.show(supportFragmentManager, account, topic.id, ItemType.TOPIC)
                return true
            }
            R.id.notifications -> {
                // TODO update notification settings

                return true
            }
            R.id.subscribe -> {
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        val isMyTopic = StringUtils.equals(topic.user.id, Utils.getAccountId(this, account))
        menu.setMenuGroupAvailability(R.id.group_menu_my_topic, isMyTopic)
        return super.onPrepareOptionsMenu(menu)
    }

}
