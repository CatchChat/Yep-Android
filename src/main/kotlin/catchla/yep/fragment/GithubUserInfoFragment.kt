package catchla.yep.fragment

import android.accounts.Account
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.LoaderManager
import android.support.v4.content.Loader
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import catchla.yep.Constants
import catchla.yep.Constants.EXTRA_ACCOUNT
import catchla.yep.Constants.EXTRA_USER
import catchla.yep.R
import catchla.yep.adapter.GithubUserAdapter
import catchla.yep.loader.GithubUserInfoLoader
import catchla.yep.model.GithubUserInfo
import catchla.yep.model.User
import kotlinx.android.synthetic.main.fragment_recycler_view.*

/**
 * Created by mariotaku on 15/6/4.
 */
class GithubUserInfoFragment : Fragment(), Constants, LoaderManager.LoaderCallbacks<GithubUserInfo?> {

    private lateinit var adapter: GithubUserAdapter

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<GithubUserInfo?> {
        val fragmentArgs = arguments
        val user = fragmentArgs.getParcelable<User>(EXTRA_USER)
        val userId = user!!.id
        return GithubUserInfoLoader(activity, account, userId, false, false)
    }

    private val account: Account
        get() = arguments.getParcelable<Account>(EXTRA_ACCOUNT)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        adapter = GithubUserAdapter(activity)
        val layoutManager = LinearLayoutManager(activity)
        layoutManager.orientation = GridLayoutManager.VERTICAL
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter

        loaderManager.initLoader(0, null, this)
        showProgress()
    }

    override fun onLoadFinished(loader: Loader<GithubUserInfo?>, data: GithubUserInfo?) {
        if (data != null) {
            adapter.info = data
        } else {
            adapter.info = null
        }
        showContent()
    }

    override fun onLoaderReset(loader: Loader<GithubUserInfo?>) {
        adapter.info = null
    }

    private fun showContent() {
        recyclerView.visibility = View.VISIBLE
        loadProgress.visibility = View.GONE
    }

    private fun showProgress() {
        recyclerView.visibility = View.GONE
        loadProgress.visibility = View.VISIBLE
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_recycler_view, container, false)
    }
}
