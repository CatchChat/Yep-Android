package catchla.yep.fragment

import android.accounts.Account
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.LoaderManager
import android.support.v4.content.Loader
import catchla.yep.Constants
import catchla.yep.activity.UserActivity
import catchla.yep.adapter.UsersAdapter
import catchla.yep.loader.SearchUsersLoader
import catchla.yep.model.User

/**
 * Created by mariotaku on 15/8/25.
 */
class SearchUsersFragment : AbsContentListRecyclerViewFragment<UsersAdapter>(), LoaderManager.LoaderCallbacks<List<User>?> {

    override fun onRefresh() {
        super.onRefresh()
        val loaderArgs = arguments
        loaderManager.restartLoader(0, loaderArgs, this)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        adapter.itemClickListener = { position, holder ->
            val user = adapter.getUser(position)
            val intent = Intent(activity, UserActivity::class.java)
            intent.putExtra(Constants.EXTRA_ACCOUNT, account)
            intent.putExtra(Constants.EXTRA_USER, user)
            startActivity(intent)
        }
        val loaderArgs = arguments
        loaderManager.initLoader(0, loaderArgs, this)
    }

    private val account: Account
        get() = arguments.getParcelable<Account>(Constants.EXTRA_ACCOUNT)

    override fun isRefreshing(): Boolean {
        return false
    }

    override fun onCreateAdapter(context: Context): UsersAdapter {
        return UsersAdapter(context)
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<List<User>?> {
        val query = args!!.getString(Constants.EXTRA_QUERY)
        return SearchUsersLoader(activity, account, query)
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
