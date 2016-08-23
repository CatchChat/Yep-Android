package catchla.yep.activity

import android.os.Bundle
import catchla.yep.Constants
import catchla.yep.Constants.EXTRA_ACCOUNT
import catchla.yep.Constants.EXTRA_QUERY
import catchla.yep.R
import catchla.yep.extension.Bundle
import catchla.yep.extension.account
import catchla.yep.fragment.SearchUsersFragment

/**
 * Created by mariotaku on 15/9/2.
 */
class SearchUsersActivity : SwipeBackContentActivity(), Constants {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fragment_content)

        val fm = supportFragmentManager
        val f = SearchUsersFragment()
        val args = Bundle {
            putParcelable(EXTRA_ACCOUNT, account)
            putString(EXTRA_QUERY, intent.getStringExtra(EXTRA_QUERY))
        }
        f.arguments = args
        fm.beginTransaction().replace(R.id.mainContent, f).commit()
    }
}
