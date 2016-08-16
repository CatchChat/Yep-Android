package catchla.yep.fragment

import android.accounts.Account
import android.content.Context
import android.os.Bundle
import android.support.v4.app.LoaderManager
import android.support.v4.content.Loader
import catchla.yep.Constants
import catchla.yep.adapter.UsersAdapter
import catchla.yep.loader.BlockedUsersLoader
import catchla.yep.model.User

/**
 * Created by mariotaku on 15/10/10.
 */
class BlockedUsersFragment : AbsContentListRecyclerViewFragment<UsersAdapter>(), LoaderManager.LoaderCallbacks<List<User>?> {

    override fun onRefresh() {
        super.onRefresh()
        val loaderArgs = arguments
        loaderManager.restartLoader(0, loaderArgs, this)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val loaderArgs = arguments
        loaderManager.initLoader(0, loaderArgs, this)
    }

    override var refreshing: Boolean
        get() = false
        set(value) {
            super.refreshing = value
        }

    override fun onCreateAdapter(context: Context): UsersAdapter {
        return UsersAdapter(context)
    }

    val account: Account
        get() = arguments.getParcelable(Constants.EXTRA_ACCOUNT)

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<List<User>?> {
        return BlockedUsersLoader(activity, account)
    }

    override fun onLoadFinished(loader: Loader<List<User>?>, data: List<User>?) {
        adapter.users = data
        showContent()
        refreshing = false
    }

    override fun onLoaderReset(loader: Loader<List<User>?>) {
        adapter.users = null
    }
}
