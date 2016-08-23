/*
 *                 Twidere - Twitter client for Android
 *
 *  Copyright (C) 2012-2015 Mariotaku Lee <mariotaku.lee@gmail.com>
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package catchla.yep.fragment

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewGroup
import catchla.yep.R
import catchla.yep.activity.iface.IControlBarActivity
import catchla.yep.adapter.LoadMoreSupportAdapter
import catchla.yep.adapter.iface.ILoadMoreSupportAdapter.IndicatorPosition
import catchla.yep.fragment.iface.RefreshScrollTopInterface
import catchla.yep.util.ContentListScrollListener
import catchla.yep.util.SimpleDrawerCallback
import catchla.yep.util.ThemeUtils
import catchla.yep.view.HeaderDrawerLayout
import kotlinx.android.synthetic.main.layout_content_recyclerview_common.*


/**
 * Created by mariotaku on 15/10/26.
 */
abstract class AbsContentRecyclerViewFragment<A : LoadMoreSupportAdapter<RecyclerView.ViewHolder>,
        L : RecyclerView.LayoutManager> : BaseFragment(), SwipeRefreshLayout.OnRefreshListener,
        HeaderDrawerLayout.DrawerCallback, RefreshScrollTopInterface,
        IControlBarActivity.ControlBarOffsetListener, ContentListScrollListener.ContentListSupport {


    lateinit var layoutManager: L
        private set
    lateinit var adapter: A
        set get

    // Callbacks and listeners
    private var mDrawerCallback: SimpleDrawerCallback? = null
    private var mScrollListener: ContentListScrollListener? = null

    // Data fields
    private val systemWindowsInsets = Rect()

    override fun canScroll(dy: Float): Boolean {
        return mDrawerCallback!!.canScroll(dy)
    }

    override fun cancelTouch() {
        mDrawerCallback!!.cancelTouch()
    }

    override fun fling(velocity: Float) {
        mDrawerCallback!!.fling(velocity)
    }

    override fun isScrollContent(x: Float, y: Float): Boolean {
        return mDrawerCallback!!.isScrollContent(x, y)
    }

    override fun onControlBarOffsetChanged(activity: IControlBarActivity, offset: Float) {
        updateRefreshProgressOffset()
    }

    override fun onRefresh() {
        triggerRefresh()
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        updateRefreshProgressOffset()
    }

    override fun scrollBy(dy: Float) {
        mDrawerCallback!!.scrollBy(dy)
    }

    override fun scrollToStart(): Boolean {
        scrollToPositionWithOffset(0, 0)
        recyclerView.stopScroll()
        setControlVisible(true)
        return true
    }

    override val contentAdapter: Any?
        get() = adapter

    protected abstract fun onScrollToPositionWithOffset(layoutManager: L, position: Int, offset: Int)

    fun scrollToPositionWithOffset(position: Int, offset: Int) {
        onScrollToPositionWithOffset(layoutManager, position, offset)
    }

    override fun setControlVisible(visible: Boolean) {
        val activity = activity
        if (activity is IControlBarActivity) {
            activity.setControlBarVisibleAnimate(visible)
        }
    }

    override fun shouldLayoutHeaderBottom(): Boolean {
        return mDrawerCallback!!.shouldLayoutHeaderBottom()
    }

    override fun topChanged(offset: Int) {
        mDrawerCallback!!.topChanged(offset)
    }

    override var refreshing: Boolean
        get() = false
        set(value) {
            val currentRefreshing = swipeLayout.isRefreshing
            if (!currentRefreshing) {
                updateRefreshProgressOffset()
            }
            if (value == currentRefreshing) return
            val layoutRefreshing = value && adapter.loadMoreIndicatorPosition != IndicatorPosition.NONE
            swipeLayout.isRefreshing = layoutRefreshing
        }

    override fun onLoadMoreContents(@IndicatorPosition position: Int) {
        loadMoreIndicatorPosition = position
        refreshEnabled = position == IndicatorPosition.NONE
    }

    @IndicatorPosition open var loadMoreIndicatorPosition: Int
        get() = adapter.loadMoreIndicatorPosition
        set(value) {
            adapter.loadMoreIndicatorPosition = value
        }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is IControlBarActivity) {
            context.registerControlBarOffsetListener(this)
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_content_recyclerview, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mDrawerCallback = SimpleDrawerCallback(recyclerView)

        val view = view ?: throw AssertionError()
        val context = view.context
        swipeLayout!!.setOnRefreshListener(this)
        swipeLayout!!.setColorSchemeColors(ThemeUtils.getColorAccent(context))
        adapter = onCreateAdapter(context)
        layoutManager = onCreateLayoutManager(context)
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)
        setupRecyclerView(context, recyclerView, layoutManager)
        recyclerView.adapter = adapter

        mScrollListener = ContentListScrollListener(this,
                ContentListScrollListener.RecyclerViewCallback(recyclerView))
        mScrollListener!!.setTouchSlop(ViewConfiguration.get(context).scaledTouchSlop)
    }

    protected abstract fun setupRecyclerView(context: Context, recyclerView: RecyclerView, layoutManager: L)

    protected abstract fun onCreateLayoutManager(context: Context): L

    override fun onStart() {
        super.onStart()
        recyclerView.addOnScrollListener(mScrollListener)
    }

    override fun onStop() {
        recyclerView.removeOnScrollListener(mScrollListener)
        super.onStop()
    }

    override fun onDetach() {
        val activity = activity
        if (activity is IControlBarActivity) {
            activity.unregisterControlBarOffsetListener(this)
        }
        super.onDetach()
    }

    protected val extraContentPadding: Rect
        get() = Rect()

    override fun fitSystemWindows(insets: Rect) {
        val extraPadding = extraContentPadding
        recyclerView.setPadding(insets.left + extraPadding.left, insets.top + extraPadding.top,
                insets.right + extraPadding.right, insets.bottom + extraPadding.bottom)
        progressContainer.setPadding(insets.left, insets.top, insets.right, insets.bottom)
        systemWindowsInsets.set(insets)
        updateRefreshProgressOffset()
    }

    var refreshEnabled: Boolean
        get() = swipeLayout.isEnabled
        set(value) {
            swipeLayout.isEnabled = value
        }

    override fun triggerRefresh(): Boolean {
        return false
    }

    protected abstract fun onCreateAdapter(context: Context): A

    override fun onDestroyView() {
        super.onDestroyView()
    }

    /**
     * Hide the progress view if it is visible. The progress view will not be
     * hidden until it has been shown for at least a minimum show time. If the
     * progress view was not yet visible, cancels showing the progress view.
     */
    fun showContent() {
        showContentInternal(true)
    }

    /**
     * Show the progress view after waiting for a minimum delay. If
     * during that time, hide() is called, the view is never made visible.
     */
    protected fun showProgress() {
        // Reset the start time.
        showProgressInternal(true)
    }

    private fun showContentInternal(animate: Boolean) {
        if (animate) {
            progressContainer.animate().alpha(0f).setDuration(200).start()
            swipeLayout.animate().alpha(1f).setDuration(200).start()
        }
        progressContainer.visibility = View.GONE
        swipeLayout.visibility = View.VISIBLE
    }

    private fun showProgressInternal(animate: Boolean) {
        if (animate) {
            progressContainer.animate().alpha(1f).setDuration(200).start()
            swipeLayout.animate().alpha(0f).setDuration(200).start()
        }
        progressContainer.visibility = View.VISIBLE
        swipeLayout.visibility = View.GONE
    }


    protected fun updateRefreshProgressOffset() {
        val activity = activity
        val insets = this.systemWindowsInsets
        val layout = this.swipeLayout
        if (activity !is IControlBarActivity || insets.top == 0 || layout == null
                || refreshing) {
            return
        }
        val progressCircleDiameter = layout.progressCircleDiameter
        if (progressCircleDiameter == 0) return
        val density = resources.displayMetrics.density
        val controlBarOffsetPixels = Math.round(activity.controlBarHeight * (1 - activity.controlBarOffset))
        val swipeStart = insets.top - controlBarOffsetPixels - progressCircleDiameter
        // 64: SwipeRefreshLayout.DEFAULT_CIRCLE_TARGET
        val swipeDistance = Math.round(64 * density)
        layout.setProgressViewOffset(false, swipeStart, swipeStart + swipeDistance)
    }
}
