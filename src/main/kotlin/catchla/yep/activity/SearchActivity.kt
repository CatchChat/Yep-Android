package catchla.yep.activity

import android.os.Bundle
import catchla.yep.Constants
import catchla.yep.R
import catchla.yep.extension.account
import catchla.yep.fragment.SearchUsersFragment

/**
 * Created by mariotaku on 15/9/2.
 */
class SearchActivity : SwipeBackContentActivity(), Constants {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fragment_content)

        val fm = supportFragmentManager
        val f = SearchUsersFragment()
        val args = Bundle()
        args.putParcelable(Constants.EXTRA_ACCOUNT, account)
        args.putString(Constants.EXTRA_QUERY, intent.getStringExtra(Constants.EXTRA_QUERY))
        f.arguments = args
        fm.beginTransaction().replace(R.id.mainContent, f).commit()
    }
}
