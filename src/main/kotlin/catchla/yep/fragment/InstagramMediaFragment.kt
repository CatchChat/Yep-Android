package catchla.yep.fragment

import android.accounts.Account
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.LoaderManager
import android.support.v4.content.Loader
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import catchla.yep.Constants
import catchla.yep.R
import catchla.yep.adapter.InstagramMediaAdapter
import catchla.yep.loader.InstagramMediaLoader
import catchla.yep.model.InstagramMediaList
import catchla.yep.model.User
import kotlinx.android.synthetic.main.fragment_recycler_view.*

/**
 * Created by mariotaku on 15/6/3.
 */
class InstagramMediaFragment : Fragment(), Constants, LoaderManager.LoaderCallbacks<InstagramMediaList?> {

    private lateinit var adapter: InstagramMediaAdapter

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<InstagramMediaList?> {
        val fragmentArgs = arguments
        val user = fragmentArgs.getParcelable<User>(Constants.EXTRA_USER)
        val userId = user!!.id
        return InstagramMediaLoader(activity, account, userId, false, false)
    }

    private val account: Account
        get() = arguments.getParcelable<Account>(Constants.EXTRA_ACCOUNT)


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        adapter = InstagramMediaAdapter(activity)

        val layoutManager = GridLayoutManager(activity, 2)
        layoutManager.orientation = GridLayoutManager.VERTICAL
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter

        loaderManager.initLoader(0, null, this)
        showProgress()
    }

    override fun onLoadFinished(loader: Loader<InstagramMediaList?>, data: InstagramMediaList?) {
        adapter.shots = data?.media
        showContent()
    }

    override fun onLoaderReset(loader: Loader<InstagramMediaList?>) {
        adapter.shots = null
    }

    private fun showContent() {
        recyclerView.visibility = View.VISIBLE
        loadProgress.visibility = View.GONE
    }

    private fun showProgress() {
        recyclerView.visibility = View.GONE
        loadProgress.visibility = View.VISIBLE
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_recycler_view, container, false)
    }

}
