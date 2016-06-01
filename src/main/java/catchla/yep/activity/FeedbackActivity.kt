package catchla.yep.activity

import android.os.Bundle
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import catchla.yep.R
import catchla.yep.model.TaskResponse
import catchla.yep.model.YepException
import catchla.yep.util.YepAPIFactory
import org.mariotaku.abstask.library.AbstractTask
import org.mariotaku.abstask.library.TaskStarter

/**
 * Created by mariotaku on 15/10/10.
 */
class FeedbackActivity : SwipeBackContentActivity() {

    private lateinit var mEditFeedback: EditText

    override fun onContentChanged() {
        super.onContentChanged()
        mEditFeedback = findViewById(R.id.edit_feedback) as EditText
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feedback)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.send -> {
                val text = mEditFeedback.text
                if (TextUtils.isEmpty(text)) return true
                sendFeedback(text.toString())
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun sendFeedback(content: String) {
        val task = object : AbstractTask<String, TaskResponse<Boolean>, FeedbackActivity>() {

            public override fun doLongOperation(params: String): TaskResponse<Boolean> {
                val yepAPI = YepAPIFactory.getInstance(this@FeedbackActivity, account)
                val deviceInfo = resources.configuration.toString()
                try {
                    yepAPI.postFeedback(params, deviceInfo)
                    return TaskResponse.getInstance(true)
                } catch (e: YepException) {
                    return TaskResponse.getInstance<Boolean>(e)
                }

            }

            public override fun afterExecute(handler: FeedbackActivity?, result: TaskResponse<Boolean>?) {
                handler!!.finish()
            }
        }
        task.setParams(content)
        task.setResultHandler(this)
        TaskStarter.execute(task)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_feedback, menu)
        return true
    }
}
