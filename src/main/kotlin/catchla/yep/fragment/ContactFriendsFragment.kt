package catchla.yep.fragment

import android.accounts.Account
import android.content.Context
import android.os.Bundle
import android.support.v4.app.LoaderManager
import android.support.v4.content.Loader
import catchla.yep.Constants

import catchla.yep.adapter.UsersAdapter
import catchla.yep.loader.ContactFriendsLoader
import catchla.yep.model.TaskResponse
import catchla.yep.model.User

/**
 * Created by mariotaku on 15/8/25.
 */
class ContactFriendsFragment : AbsContentListRecyclerViewFragment<UsersAdapter>(), LoaderManager.LoaderCallbacks<TaskResponse<List<User>>> {

    override fun onRefresh() {
        super.onRefresh()
        loaderManager.restartLoader(0, null, this)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        loaderManager.initLoader(0, null, this)
    }

    override var refreshing: Boolean
        get() = false
        set(value) {
            super.refreshing = value
        }

    override fun onCreateAdapter(context: Context): UsersAdapter {
        return UsersAdapter(context)
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<TaskResponse<List<User>>> {
        return ContactFriendsLoader(activity, account)
    }

    private val account: Account
        get() = arguments.getParcelable<Account>(Constants.EXTRA_ACCOUNT)

    override fun onLoadFinished(loader: Loader<TaskResponse<List<User>>>, data: TaskResponse<List<User>>) {
        adapter.users = data.data
        showContent()
        refreshing = false
    }

    override fun onLoaderReset(loader: Loader<TaskResponse<List<User>>>) {
        adapter.users = null
    }
}
