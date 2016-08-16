/*
 * Twidere - Twitter client for Android
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

package catchla.yep.util

import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.OnScrollListener
import android.view.MotionEvent
import android.view.View

import catchla.yep.adapter.iface.ILoadMoreSupportAdapter
import catchla.yep.adapter.iface.ILoadMoreSupportAdapter.IndicatorPosition


/**
 * Created by mariotaku on 15/3/15.
 */
class ContentListScrollListener(private val mContentListSupport: ContentListScrollListener.ContentListSupport, private val mViewCallback: ContentListScrollListener.ViewCallback?) : OnScrollListener() {
    private val mTouchListener: TouchListener

    private var mScrollState: Int = 0
    private var mScrollSum: Int = 0
    private var mTouchSlop: Int = 0

    private var mScrollDirection: Int = 0

    init {
        mTouchListener = TouchListener(this)
    }

    override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
        if (mContentListSupport is Fragment) {
            if (mContentListSupport.context == null) return
        }
        if (mScrollState != RecyclerView.SCROLL_STATE_IDLE) {
            postNotifyScrollStateChanged()
        }
        mScrollState = newState
    }

    override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
        if (mContentListSupport is Fragment) {
            if (mContentListSupport.context == null) return
        }
        //Reset mScrollSum when scrolling in reverse direction
        if (dy * mScrollSum < 0) {
            mScrollSum = 0
        }
        mScrollSum += dy
        if (Math.abs(mScrollSum) > mTouchSlop) {
            mContentListSupport.setControlVisible(dy < 0)
            mScrollSum = 0
        }
        if (recyclerView!!.scrollState == RecyclerView.SCROLL_STATE_IDLE) {
            postNotifyScrollStateChanged()
        }
    }

    fun setTouchSlop(touchSlop: Int) {
        mTouchSlop = touchSlop
    }

    private fun postNotifyScrollStateChanged() {
        if (mContentListSupport is Fragment) {
            if (mContentListSupport.context == null) return
        }
        if (mViewCallback != null) {
            mViewCallback.post(object : Runnable {
                override fun run() {
                    if (mViewCallback.isComputingLayout) {
                        mViewCallback.post(this)
                    } else {
                        notifyScrollStateChanged()
                    }
                }
            })
        } else {
            notifyScrollStateChanged()
        }
    }

    private fun notifyScrollStateChanged() {
        if (mContentListSupport is Fragment) {
            if (mContentListSupport.context == null) return
        }
        val adapter = mContentListSupport.contentAdapter
        if (adapter !is ILoadMoreSupportAdapter) return
        if (!mContentListSupport.refreshing && adapter.loadMoreSupportedPosition != IndicatorPosition.NONE
                && adapter.loadMoreIndicatorPosition == IndicatorPosition.NONE) {
            var position = 0
            if (mContentListSupport.reachingEnd && mScrollDirection >= 0) {
                position = position or IndicatorPosition.END
            }
            if (mContentListSupport.reachingStart && mScrollDirection <= 0) {
                position = position or IndicatorPosition.START
            }
            resetScrollDirection()
            if (position != 0) {
                mContentListSupport.onLoadMoreContents(position)
            }
        }
    }

    val onTouchListener: View.OnTouchListener
        get() = mTouchListener

    internal class TouchListener(private val listener: ContentListScrollListener) : View.OnTouchListener {
        private var mLastY: Float = 0.toFloat()

        override fun onTouch(v: View, event: MotionEvent): Boolean {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    listener.resetScrollDirection()
                    mLastY = java.lang.Float.NaN
                }
                MotionEvent.ACTION_MOVE -> {
                    if (!java.lang.Float.isNaN(mLastY)) {
                        val delta = mLastY - event.rawY
                        listener.setScrollDirection(if (delta < 0) -1 else 1)
                    } else {
                        mLastY = event.rawY
                    }
                }
            }
            return false
        }
    }

    private fun setScrollDirection(direction: Int) {
        mScrollDirection = direction
    }

    private fun resetScrollDirection() {
        mScrollDirection = 0
    }

    interface ViewCallback {
        val isComputingLayout: Boolean

        fun post(runnable: Runnable)
    }

    interface ContentListSupport {

        val contentAdapter: Any?

        val refreshing: Boolean

        fun onLoadMoreContents(@IndicatorPosition position: Int)

        fun setControlVisible(visible: Boolean)

        val reachingStart: Boolean

        val reachingEnd: Boolean

    }

    class RecyclerViewCallback(private val recyclerView: RecyclerView) : ViewCallback {

        override val isComputingLayout: Boolean
            get() = recyclerView.isComputingLayout

        override fun post(action: Runnable) {
            recyclerView.post(action)
        }
    }
}
