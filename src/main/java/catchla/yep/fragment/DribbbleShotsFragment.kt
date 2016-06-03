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

    private var mRecyclerView: RecyclerView? = null
    private var mAdapter: DribbbleShotsAdapter? = null
    private var mLoadProgress: View? = null

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

        mAdapter = DribbbleShotsAdapter(activity)
        val layoutManager = GridLayoutManager(activity, 2)
        layoutManager.orientation = GridLayoutManager.VERTICAL
        mRecyclerView!!.layoutManager = layoutManager
        mRecyclerView!!.adapter = mAdapter

        loaderManager.initLoader(0, null, this)
        showProgress()
    }

    override fun onLoadFinished(loader: Loader<DribbbleShots>, data: DribbbleShots?) {
        if (data != null) {
            mAdapter!!.setData(data.shots)
        } else {
            mAdapter!!.setData(null)
        }
        showContent()
    }

    override fun onLoaderReset(loader: Loader<DribbbleShots>) {
        mAdapter!!.setData(null)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mRecyclerView = view!!.findViewById(R.id.recycler_view) as RecyclerView
        mLoadProgress = view.findViewById(R.id.load_progress)
    }

    private fun showContent() {
        mRecyclerView!!.visibility = View.VISIBLE
        mLoadProgress!!.visibility = View.GONE
    }

    private fun showProgress() {
        mRecyclerView!!.visibility = View.GONE
        mLoadProgress!!.visibility = View.VISIBLE
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_recycler_view, container, false)
    }

}
