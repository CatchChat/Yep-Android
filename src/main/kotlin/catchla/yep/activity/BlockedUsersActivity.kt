package catchla.yep.activity

import android.os.Bundle
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction

import catchla.yep.R
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
        ft.replace(R.id.main_content, BlockedUsersFragment())
        ft.commit()
    }
}
