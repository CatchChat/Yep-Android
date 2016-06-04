package catchla.yep.activity

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.Menu
import android.view.MenuItem
import catchla.yep.Constants
import catchla.yep.R
import catchla.yep.fragment.TopicChatListFragment
import catchla.yep.model.TaskResponse
import catchla.yep.model.Topic
import catchla.yep.model.UrlResponse
import catchla.yep.model.YepException
import catchla.yep.util.Utils
import catchla.yep.util.YepAPIFactory
import org.mariotaku.ktextension.setMenuGroupAvailability
import org.apache.commons.lang3.StringUtils
import org.mariotaku.abstask.library.AbstractTask
import org.mariotaku.abstask.library.TaskStarter

class TopicChatActivity : SwipeBackContentActivity(), Constants {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_topic_chat)

        val topic = topic
        displayTopic(topic)

        val ft = supportFragmentManager.beginTransaction()
        val args = Bundle()
        args.putParcelable(Constants.EXTRA_ACCOUNT, account)
        args.putParcelable(Constants.EXTRA_TOPIC, topic)
        ft.replace(R.id.chat_list, Fragment.instantiate(this, TopicChatListFragment::class.java.name, args))
        ft.commit()
    }

    private val topic: Topic
        get() = intent.getParcelableExtra<Topic>(Constants.EXTRA_TOPIC)

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_topic_chat, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.share -> {
                TaskStarter.execute(object : AbstractTask<Any, TaskResponse<UrlResponse>, TopicChatActivity>() {
                    public override fun doLongOperation(o: Any): TaskResponse<UrlResponse> {
                        val yep = YepAPIFactory.getInstance(this@TopicChatActivity, account)
                        try {
                            return TaskResponse.getInstance(yep.getCircleShareUrl(topic.circle.id))
                        } catch (e: YepException) {
                            return TaskResponse.getInstance<UrlResponse>(e)
                        }

                    }


                })
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
