package catchla.yep.fragment

import android.content.Context
import android.os.Bundle
import android.support.v4.app.LoaderManager
import android.support.v4.content.Loader

import catchla.yep.adapter.UsersAdapter
import catchla.yep.loader.BlockedUsersLoader
import catchla.yep.model.User
import catchla.yep.util.Utils

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

    override fun isRefreshing(): Boolean {
        return false
    }

    override fun onCreateAdapter(context: Context): UsersAdapter {
        return UsersAdapter(context)
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<List<User>?> {
        return BlockedUsersLoader(activity, Utils.getCurrentAccount(activity)!!)
    }

    override fun onLoadFinished(loader: Loader<List<User>?>, data: List<User>?) {
        adapter.users = data
        showContent()
        isRefreshing = false
    }

    override fun onLoaderReset(loader: Loader<List<User>?>) {
        adapter.users = null
    }
}
