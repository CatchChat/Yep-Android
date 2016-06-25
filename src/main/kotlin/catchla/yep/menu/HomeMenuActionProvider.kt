/*
 * Copyright (c) 2015. Catch Inc,
 */

package catchla.yep.menu

import android.accounts.Account
import android.accounts.AccountManager
import android.content.Context
import android.support.v4.view.ActionProvider
import android.support.v4.view.GravityCompat
import android.support.v7.widget.ForwardingListener
import android.support.v7.widget.ListPopupWindow
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import catchla.yep.BuildConfig
import catchla.yep.Constants
import catchla.yep.R
import catchla.yep.adapter.LayoutAdapter
import catchla.yep.util.ImageLoaderWrapper
import catchla.yep.util.ThemeUtils
import catchla.yep.util.dagger.GeneralComponentHelper
import com.commonsware.cwac.merge.MergeAdapter
import javax.inject.Inject

/**
 * Created by mariotaku on 15/5/4.
 */
class HomeMenuActionProvider
/**
 * Creates a new instance.

 * @param context Context for accessing resources.
 */
(context: Context) : ActionProvider(context), Constants, View.OnClickListener {

    private val popupMaxWidth: Int
    private var actionView: View? = null
    private val overflowPopup: ListPopupWindow?
    private var actionsAdapter: HomeMenuActionsAdapter? = null
    private val adapter: MergeAdapter
    private var hasContentWidth: Boolean = false
    private var contentWidth: Int = 0
    private var postedOpenRunnable: Runnable? = null
    private val onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
        if (onActionListener == null) return@OnItemClickListener
        if (position == 0) {
            onActionListener!!.onProfileClick()
        } else {
            onActionListener!!.onActionClick(parent.getItemAtPosition(position) as Action)
        }
        dismissPopup()
    }
    private var onActionListener: OnActionListener? = null
    private var account: Account? = null
    private var headersAdapter: HeadersAdapter? = null

    init {
        val res = context.resources
        popupMaxWidth = Math.max(res.displayMetrics.widthPixels / 2,
                res.getDimensionPixelSize(android.support.v7.appcompat.R.dimen.abc_panel_menu_list_width))


        val popupContext = ThemeUtils.getActionBarPopupThemedContext(getContext())

        adapter = MergeAdapter()
        headersAdapter = HeadersAdapter(popupContext)
        actionsAdapter = HomeMenuActionsAdapter(popupContext)
        adapter.addAdapter(headersAdapter)
        adapter.addAdapter(actionsAdapter)

        headersAdapter!!.add(R.layout.header_home_menu_profile, "profile", true)
        headersAdapter!!.add(R.layout.layout_divider_vertical, "divider", false)

        headersAdapter!!.makeFinal()

        actionsAdapter!!.add(Action(popupContext.getString(R.string.settings), R.id.settings))
        actionsAdapter!!.add(Action(popupContext.getString(R.string.about), R.id.about))
        if (BuildConfig.DEBUG) {
            actionsAdapter!!.add(Action("Dev settings", R.id.development))
        }

        overflowPopup = ListPopupWindow(popupContext, null, android.support.v7.appcompat.R.attr.actionOverflowMenuStyle, 0)
        overflowPopup.isModal = true
        overflowPopup.setAdapter(adapter)
        overflowPopup.setDropDownGravity(GravityCompat.END)
        overflowPopup.horizontalOffset = -popupContext.resources.getDimensionPixelOffset(R.dimen.element_spacing_normal)
        overflowPopup.verticalOffset = popupContext.resources.getDimensionPixelOffset(R.dimen.element_spacing_small)
        overflowPopup.setOnItemClickListener(onItemClickListener)
        overflowPopup.inputMethodMode = PopupWindow.INPUT_METHOD_NOT_NEEDED
    }

    override fun onPerformDefaultAction(): Boolean {
        return super.onPerformDefaultAction()
    }

    override fun onCreateActionView(): View {
        val view = LayoutInflater.from(context).inflate(R.layout.layout_action_item_home_menu, null)
        view.setOnClickListener(this)
        view.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
            override fun onViewAttachedToWindow(v: View) {

            }

            override fun onViewDetachedFromWindow(v: View) {
                dismissPopup()
            }
        })
        view.setOnTouchListener (object : ForwardingListener(view) {
            override fun getPopup(): ListPopupWindow {
                return overflowPopup!!
            }

            override fun onForwardingStarted(): Boolean {
                showPopup()
                return true
            }

            override fun onForwardingStopped(): Boolean {
                // Displaying the popup occurs asynchronously, so wait for
                // the runnable to finish before deciding whether to stop
                // forwarding.
                if (postedOpenRunnable != null) {
                    return false
                }

                dismissPopup()
                return true
            }
        })


        if (!hasContentWidth) {
            contentWidth = measureContentWidth()
            hasContentWidth = true
        }


        overflowPopup!!.anchorView = view
        overflowPopup.setContentWidth(contentWidth)
        actionView = view

        updateHeader()

        return view
    }

    private fun showPopup() {
        if (actionView == null || overflowPopup == null || overflowPopup.isShowing || account == null)
            return
        postedOpenRunnable = ShowPopupRunnable(overflowPopup)
        actionView!!.post(postedOpenRunnable)
    }

    override fun hasSubMenu(): Boolean {
        return false
    }

    override fun onClick(v: View) {
        dismissPopup()

        //        mOverflowPopup.getListView().setOnKeyListener(this);
        showPopup()
    }

    private fun dismissPopup() {
        if (overflowPopup == null || !overflowPopup.isShowing) return
        overflowPopup.dismiss()
    }

    fun setAccount(account: Account) {
        this.account = account
        updateHeader()
    }

    private fun updateHeader() {
        headersAdapter?.setAccount(account ?: return)
    }

    class Action(var title: String, var id: Int)

    class HeadersAdapter(context: Context) : LayoutAdapter(context) {

        @Inject
        internal lateinit var imageLoader: ImageLoaderWrapper

        private var account: Account? = null

        init {
            GeneralComponentHelper.build(context).inject(this)
        }

        fun setAccount(account: Account) {
            this.account = account
            notifyDataSetChanged()
        }

        override fun bindView(view: View, position: Int, tag: String) {
            if ("profile" != tag) return
            val account = account ?: return
            val profileImageView = view.findViewById(R.id.home_menu_profile_image) as ImageView
            val nameView = view.findViewById(R.id.home_menu_name) as TextView
            val am = AccountManager.get(this.context)
            nameView.text = am.getUserData(account, Constants.USER_DATA_NICKNAME)
            imageLoader.displayProfileImage(am.getUserData(account, Constants.USER_DATA_AVATAR), profileImageView)
        }
    }

    private class HomeMenuActionsAdapter(context: Context) : ArrayAdapter<Action>(context, R.layout.list_item_menu, android.R.id.text1) {

        override fun getView(position: Int, convertView: View, parent: ViewGroup): View {
            val view = super.getView(position, convertView, parent)
            val icon = view.findViewById(android.R.id.icon) as ImageView
            icon.visibility = View.GONE
            val text1 = view.findViewById(android.R.id.text1) as TextView
            text1.text = getItem(position).title
            return view
        }
    }

    private var mMeasureParent: ViewGroup? = null

    private fun measureContentWidth(): Int {
        // Menus don't tend to be long, so this is more sane than it looks.
        var maxWidth = 0
        var itemView: View? = null
        var itemType = 0

        val adapter = adapter
        val widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        val heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        val count = adapter.count
        for (i in 0..count - 1) {
            val positionType = adapter.getItemViewType(i)
            if (positionType != itemType) {
                itemType = positionType
                itemView = null
            }

            if (mMeasureParent == null) {
                mMeasureParent = FrameLayout(context)
            }

            itemView = adapter.getView(i, itemView, mMeasureParent)
            itemView!!.measure(widthMeasureSpec, heightMeasureSpec)

            val itemWidth = itemView.measuredWidth
            if (itemWidth >= popupMaxWidth) {
                return popupMaxWidth
            } else if (itemWidth > maxWidth) {
                maxWidth = itemWidth
            }
        }

        return maxWidth
    }


    private inner class ShowPopupRunnable(private val popup: ListPopupWindow) : Runnable {

        override fun run() {
            val itemView = actionView
            if (itemView != null && itemView.windowToken != null) {
                popup.show()
            }
            postedOpenRunnable = null
        }
    }

    fun setOnActionListener(listener: OnActionListener) {
        onActionListener = listener
    }

    interface OnActionListener {
        fun onProfileClick()

        fun onActionClick(action: Action)
    }
}
