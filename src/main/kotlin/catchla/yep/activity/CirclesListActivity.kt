package catchla.yep.activity

import android.os.Bundle
import catchla.yep.Constants
import catchla.yep.Constants.EXTRA_ACCOUNT
import catchla.yep.Constants.EXTRA_SHOW_SEARCH_BOX
import catchla.yep.R
import catchla.yep.extension.Bundle
import catchla.yep.extension.account
import catchla.yep.extension.set
import catchla.yep.fragment.ConversationsListFragment
import catchla.yep.model.Message

/**
 * Created by mariotaku on 15/11/4.
 */
class CirclesListActivity : SwipeBackContentActivity(), Constants {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fragment_content)
        val args = Bundle {
            this[EXTRA_ACCOUNT] = account
            this[EXTRA_SHOW_SEARCH_BOX] = false
            this[ConversationsListFragment.EXTRA_RECIPIENT_TYPE] = Message.RecipientType.CIRCLE
        }
        val f = ConversationsListFragment()
        f.arguments = args
        supportFragmentManager.beginTransaction().replace(R.id.mainContent, f).commit()
    }
}
