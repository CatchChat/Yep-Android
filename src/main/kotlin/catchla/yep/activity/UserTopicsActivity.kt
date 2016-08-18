package catchla.yep.activity

import android.os.Bundle
import catchla.yep.Constants
import catchla.yep.R
import catchla.yep.extension.account
import catchla.yep.extension.Bundle
import catchla.yep.fragment.TopicsListFragment
import catchla.yep.model.User

/**
 * Created by mariotaku on 15/9/2.
 */
class UserTopicsActivity : SwipeBackContentActivity(), Constants {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fragment_content)

        val fm = supportFragmentManager
        val f = TopicsListFragment()
        f.arguments = Bundle {
            putParcelable(Constants.EXTRA_ACCOUNT, account)
            putString(Constants.EXTRA_USER_ID, user.id)
        }
        fm.beginTransaction().replace(R.id.mainContent, f).commit()
    }

    private val user: User
        get() = intent.getParcelableExtra<User>(Constants.EXTRA_USER)
}
