/*
 * Copyright (c) 2015. Catch Inc,
 */

package catchla.yep.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.LoaderManager
import android.support.v4.content.Loader
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import catchla.yep.Constants.*
import catchla.yep.R
import catchla.yep.activity.ChatActivity
import catchla.yep.activity.CirclesListActivity
import catchla.yep.adapter.ChatsListAdapter
import catchla.yep.adapter.decorator.DividerItemDecoration
import catchla.yep.extension.Bundle
import catchla.yep.extension.account
import catchla.yep.fragment.iface.IActionButtonSupportFragment
import catchla.yep.loader.ConversationsLoader
import catchla.yep.message.MessageRefreshedEvent
import catchla.yep.model.Conversation
import catchla.yep.model.Message
import catchla.yep.service.MessageService
import com.squareup.otto.Subscribe

/**
 * Created by mariotaku on 15/4/29.
 */
class ConversationsListFragment : AbsContentListRecyclerViewFragment<ChatsListAdapter>(),
        LoaderManager.LoaderCallbacks<List<Conversation>>, IActionButtonSupportFragment {
    val showSearchBox: Boolean
        get() = arguments.getBoolean(EXTRA_SHOW_SEARCH_BOX, true)


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setHasOptionsMenu(true)

        adapter.showSearchBox = showSearchBox
        adapter.itemClickListener = { position, holder ->
            val adapter = adapter
            if (adapter.getItemViewType(position) == ChatsListAdapter.ITEM_VIEW_TYPE_CIRCLES_ENTRY) {
                val intent = Intent(context, CirclesListActivity::class.java)
                intent.putExtra(EXTRA_ACCOUNT, account)
                startActivity(intent)
            } else {
                val conversation = adapter.getConversation(position)
                val intent = Intent(context, ChatActivity::class.java)
                intent.putExtra(EXTRA_ACCOUNT, account)
                intent.putExtra(EXTRA_CONVERSATION, conversation)
                startActivity(intent)
            }
        }
        val loaderArgs = Bundle {
            putString(EXTRA_RECIPIENT_TYPE, recipientType)
        }
        loaderManager.initLoader(0, loaderArgs, this)
        showProgress()
    }

    override fun onStart() {
        super.onStart()
        bus.register(this)
    }

    override fun onStop() {
        bus.unregister(this)
        super.onStop()
    }

    @Subscribe
    fun onMessageRefreshed(event: MessageRefreshedEvent) {
        refreshing = false
        val loaderArgs = Bundle {
            putString(EXTRA_RECIPIENT_TYPE, recipientType)
        }
        loaderManager.restartLoader(0, loaderArgs, this)
    }

    private val recipientType: String
        get() = arguments.getString(EXTRA_RECIPIENT_TYPE, Message.RecipientType.USER)

    override fun onCreateAdapter(context: Context): ChatsListAdapter {
        return ChatsListAdapter(context)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater!!.inflate(R.menu.menu_fragment_chats_list, menu)
    }

    override fun onBaseViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onBaseViewCreated(view, savedInstanceState)
    }

    override var refreshing: Boolean
        get() = false
        set(value) {
            super.refreshing = value
        }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<List<Conversation>> {
        val recipientType = recipientType
        return ConversationsLoader(activity, account, recipientType,
                Message.RecipientType.CIRCLE != recipientType)
    }

    override fun onLoadFinished(loader: Loader<List<Conversation>>, data: List<Conversation>) {
        adapter.setConversations(data, Message.RecipientType.CIRCLE == recipientType)
        showContent()
    }

    override fun onLoaderReset(loader: Loader<List<Conversation>>) {
        adapter.setConversations(null, Message.RecipientType.CIRCLE == recipientType)
    }

    override fun onRefresh() {
        super.onRefresh()
        val activity = activity
        val intent = Intent(activity, MessageService::class.java)
        intent.action = MessageService.ACTION_REFRESH_MESSAGES
        intent.putExtra(EXTRA_ACCOUNT, account)
        activity.startService(intent)
    }

    override fun getActionIcon(): Int {
        return R.drawable.ic_action_search
    }

    override fun onActionPerformed() {

    }

    override fun getActionMenuFragment(): Class<out FloatingActionMenuFragment>? {
        return null
    }

    override fun createItemDecoration(context: Context,
                                      recyclerView: RecyclerView,
                                      layoutManager: LinearLayoutManager): RecyclerView.ItemDecoration? {
        val decoration = super.createItemDecoration(context, recyclerView, layoutManager) as DividerItemDecoration
        if (showSearchBox) {
            decoration.setDecorationStart(1)
        }
        val leftPadding = resources.getDimensionPixelSize(R.dimen.icon_size_status_profile_image) +
                resources.getDimensionPixelSize(R.dimen.element_spacing_normal) * 2
        decoration.setPadding(leftPadding, 0, 0, 0)
        return decoration
    }

    companion object {

        val EXTRA_RECIPIENT_TYPE = "recipient_type"
    }
}
