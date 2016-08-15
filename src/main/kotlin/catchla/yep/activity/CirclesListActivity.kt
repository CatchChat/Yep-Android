package catchla.yep.activity

import android.os.Bundle

import catchla.yep.Constants
import catchla.yep.R
import catchla.yep.extension.account
import catchla.yep.fragment.ConversationsListFragment
import catchla.yep.model.Message

/**
 * Created by mariotaku on 15/11/4.
 */
class CirclesListActivity : SwipeBackContentActivity(), Constants {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fragment_content)
        val args = Bundle()
        args.putParcelable(Constants.EXTRA_ACCOUNT, account)
        args.putString(ConversationsListFragment.EXTRA_RECIPIENT_TYPE, Message.RecipientType.CIRCLE)
        val f = ConversationsListFragment()
        f.arguments = args
        supportFragmentManager.beginTransaction().replace(R.id.mainContent, f).commit()
    }
}
