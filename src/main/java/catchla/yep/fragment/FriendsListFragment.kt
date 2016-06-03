/*
 * Copyright (c) 2015. Catch Inc,
 */

package catchla.yep.fragment

import android.accounts.Account
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.LoaderManager
import android.support.v4.content.Loader
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import catchla.yep.Constants
import catchla.yep.R
import catchla.yep.activity.FindFriendActivity
import catchla.yep.activity.UserActivity
import catchla.yep.adapter.FriendsListAdapter
import catchla.yep.fragment.iface.IActionButtonSupportFragment
import catchla.yep.loader.FriendshipsLoader
import catchla.yep.message.FriendshipsRefreshedEvent
import catchla.yep.model.Friendship
import catchla.yep.service.MessageService
import com.squareup.otto.Subscribe

/**
 * Created by mariotaku on 15/4/29.
 */
class FriendsListFragment : AbsContentListRecyclerViewFragment<FriendsListAdapter>(), LoaderManager.LoaderCallbacks<List<Friendship>>, IActionButtonSupportFragment {

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setHasOptionsMenu(true)
        adapter.setItemClickListener { position, holder ->
            val friendship = adapter.getFriendship(position)
            val intent = Intent(activity, UserActivity::class.java)
            intent.putExtra(Constants.EXTRA_ACCOUNT, account)
            intent.putExtra(Constants.EXTRA_USER, friendship.friend)
            startActivity(intent)
        }
        loaderManager.initLoader(0, null, this)
        showProgress()
    }

    private val account: Account
        get() = arguments.getParcelable<Account>(Constants.EXTRA_ACCOUNT)

    override fun onCreateAdapter(context: Context): FriendsListAdapter {
        return FriendsListAdapter(context)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater!!.inflate(R.menu.menu_fragment_friends_list, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {

        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBaseViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onBaseViewCreated(view, savedInstanceState)
    }

    override fun isRefreshing(): Boolean {
        return false
    }


    override fun onCreateLoader(id: Int, args: Bundle?): Loader<List<Friendship>> {
        return FriendshipsLoader(activity, account)
    }

    override fun onLoadFinished(loader: Loader<List<Friendship>>, data: List<Friendship>) {
        adapter.setData(data)
        showContent()
    }

    override fun onStart() {
        super.onStart()
        mBus.register(this)
    }

    override fun onStop() {
        mBus.unregister(this)
        super.onStop()
    }

    @Subscribe
    fun onMessageRefreshed(event: FriendshipsRefreshedEvent) {
        isRefreshing = false
        loaderManager.restartLoader(0, null, this)
    }

    override fun triggerRefresh(): Boolean {
        val activity = activity
        val intent = Intent(activity, MessageService::class.java)
        intent.action = MessageService.ACTION_REFRESH_FRIENDSHIPS
        intent.putExtra(Constants.EXTRA_ACCOUNT, account)
        activity.startService(intent)
        return true
    }

    override fun onLoaderReset(loader: Loader<List<Friendship>>) {
        adapter.setData(null)
    }

    override fun getActionIcon(): Int {
        return R.drawable.ic_action_add
    }

    override fun onActionPerformed() {
        val intent = Intent(activity, FindFriendActivity::class.java)
        intent.putExtra(Constants.EXTRA_ACCOUNT, account)
        startActivity(intent)
    }

    override fun getActionMenuFragment(): Class<out FloatingActionMenuFragment>? {
        return null
    }
}
