package catchla.yep.activity

import android.os.Bundle
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import catchla.yep.R
import catchla.yep.extension.account
import catchla.yep.util.YepAPIFactory
import kotlinx.android.synthetic.main.activity_feedback.*
import nl.komponents.kovenant.task
import nl.komponents.kovenant.ui.alwaysUi
import nl.komponents.kovenant.ui.successUi

/**
 * Created by mariotaku on 15/10/10.
 */
class FeedbackActivity : SwipeBackContentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feedback)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_feedback, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.send -> {
                val text = editFeedback.text
                if (TextUtils.isEmpty(text)) return true
                sendFeedback(text.toString())
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun sendFeedback(content: String) {
        task {
            val yepAPI = YepAPIFactory.getInstance(this, account)
            val deviceInfo = resources.configuration.toString()
            yepAPI.postFeedback(content, deviceInfo)
        }.successUi {
            finish()
        }.alwaysUi {

        }

    }
}
