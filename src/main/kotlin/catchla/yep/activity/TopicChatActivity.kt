package catchla.yep.activity

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.Menu
import android.view.MenuItem
import catchla.yep.Constants
import catchla.yep.Constants.EXTRA_ACCOUNT
import catchla.yep.Constants.EXTRA_TOPIC
import catchla.yep.R
import catchla.yep.annotation.ItemType
import catchla.yep.extension.account
import catchla.yep.extension.Bundle
import catchla.yep.fragment.ReportTypeDialogFragment
import catchla.yep.fragment.TopicChatListFragment
import catchla.yep.model.Topic
import catchla.yep.util.Utils
import catchla.yep.util.YepAPIFactory
import nl.komponents.kovenant.task
import nl.komponents.kovenant.ui.alwaysUi
import nl.komponents.kovenant.ui.failUi
import nl.komponents.kovenant.ui.successUi
import org.apache.commons.lang3.StringUtils
import org.mariotaku.ktextension.setMenuGroupAvailability

class TopicChatActivity : SwipeBackContentActivity(), Constants {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_topic_chat)
        displayTopic(topic)

        val ft = supportFragmentManager.beginTransaction()
        val args = Bundle {
            putParcelable(EXTRA_ACCOUNT, account)
            putParcelable(EXTRA_TOPIC, topic)
        }
        ft.replace(R.id.chatList, Fragment.instantiate(this, TopicChatListFragment::class.java.name, args))
        ft.commit()
    }

    private val topic: Topic
        get() = intent.getParcelableExtra<Topic>(EXTRA_TOPIC)

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_topic_chat, menu)
        return true
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

    private fun displayTopic(topic: Topic) {
    }

    override fun onContentChanged() {
        super.onContentChanged()
    }
}
