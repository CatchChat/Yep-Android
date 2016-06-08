package catchla.yep.fragment

import android.accounts.Account
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.LoaderManager
import android.support.v4.content.Loader
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import catchla.yep.Constants
import catchla.yep.R
import catchla.yep.adapter.DribbbleShotsAdapter
import catchla.yep.loader.DribbbleShotsLoader
import catchla.yep.model.DribbbleShots
import catchla.yep.model.User

/**
 * Created by mariotaku on 15/6/3.
 */
class DribbbleShotsFragment : Fragment(), Constants, LoaderManager.LoaderCallbacks<DribbbleShots> {

    private lateinit var recyclerView: RecyclerView
    private lateinit var loadProgress: View

    private lateinit var adapter: DribbbleShotsAdapter

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<DribbbleShots> {
        val fragmentArgs = arguments
        val user = fragmentArgs.getParcelable<User>(Constants.EXTRA_USER)
        val userId = user!!.id
        return DribbbleShotsLoader(activity, account, userId, false, false)
    }

    private val account: Account
        get() = arguments.getParcelable<Account>(Constants.EXTRA_ACCOUNT)


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        adapter = DribbbleShotsAdapter(activity)
        val layoutManager = GridLayoutManager(activity, 2)
        layoutManager.orientation = GridLayoutManager.VERTICAL
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter

        loaderManager.initLoader(0, null, this)
        showProgress()
    }

    override fun onLoadFinished(loader: Loader<DribbbleShots>, data: DribbbleShots?) {
        if (data != null) {
            adapter.shots = data.shots
        } else {
            adapter.shots = null
        }
        showContent()
    }

    override fun onLoaderReset(loader: Loader<DribbbleShots>) {
        adapter.shots = null
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view!!.findViewById(R.id.recycler_view) as RecyclerView
        loadProgress = view.findViewById(R.id.load_progress)
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
