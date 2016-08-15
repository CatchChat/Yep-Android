package catchla.yep.activity

import android.os.Bundle
import android.support.v4.app.Fragment
import catchla.yep.Constants.EXTRA_ACCOUNT
import catchla.yep.R
import catchla.yep.extension.account
import catchla.yep.fragment.BlockedUsersFragment

/**
 * Created by mariotaku on 15/10/10.
 */
class BlockedUsersActivity : SwipeBackContentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fragment_content)
        val fm = supportFragmentManager
        val ft = fm.beginTransaction()
        val fragmentArgs = Bundle()
        fragmentArgs.putParcelable(EXTRA_ACCOUNT, account)
        ft.replace(R.id.mainContent, Fragment.instantiate(this, BlockedUsersFragment::class.java.name, fragmentArgs))
        ft.commit()
    }
}
