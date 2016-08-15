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

package catchla.yep.util;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.view.MotionEvent;
import android.view.View;

import catchla.yep.adapter.iface.ILoadMoreSupportAdapter;
import catchla.yep.adapter.iface.ILoadMoreSupportAdapter.IndicatorPosition;


/**
 * Created by mariotaku on 15/3/15.
 */
public class ContentListScrollListener extends OnScrollListener {

    private final ContentListSupport mContentListSupport;
    private final ViewCallback mViewCallback;
    private final TouchListener mTouchListener;

    private int mScrollState;
    private int mScrollSum;
    private int mTouchSlop;

    private int mScrollDirection;

    public ContentListScrollListener(@NonNull ContentListSupport contentListSupport, @Nullable ViewCallback viewCallback) {
        mContentListSupport = contentListSupport;
        mViewCallback = viewCallback;
        mTouchListener = new TouchListener(this);
    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        if (mContentListSupport instanceof Fragment) {
            if (((Fragment) mContentListSupport).getContext() == null) return;
        }
        if (mScrollState != RecyclerView.SCROLL_STATE_IDLE) {
            postNotifyScrollStateChanged();
        }
        mScrollState = newState;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        if (mContentListSupport instanceof Fragment) {
            if (((Fragment) mContentListSupport).getContext() == null) return;
        }
        //Reset mScrollSum when scrolling in reverse direction
        if (dy * mScrollSum < 0) {
            mScrollSum = 0;
        }
        mScrollSum += dy;
        if (Math.abs(mScrollSum) > mTouchSlop) {
            mContentListSupport.setControlVisible(dy < 0);
            mScrollSum = 0;
        }
        if (recyclerView.getScrollState() == RecyclerView.SCROLL_STATE_IDLE) {
            postNotifyScrollStateChanged();
        }
    }

    public void setTouchSlop(int touchSlop) {
        mTouchSlop = touchSlop;
    }

    private void postNotifyScrollStateChanged() {
        if (mContentListSupport instanceof Fragment) {
            if (((Fragment) mContentListSupport).getContext() == null) return;
        }
        if (mViewCallback != null) {
            mViewCallback.post(new Runnable() {
                @Override
                public void run() {
                    if (mViewCallback.isComputingLayout()) {
                        mViewCallback.post(this);
                    } else {
                        notifyScrollStateChanged();
                    }
                }
            });
        } else {
            notifyScrollStateChanged();
        }
    }

    private void notifyScrollStateChanged() {
        if (mContentListSupport instanceof Fragment) {
            if (((Fragment) mContentListSupport).getContext() == null) return;
        }
        final Object adapter = mContentListSupport.getContentAdapter();
        if (!(adapter instanceof ILoadMoreSupportAdapter)) return;
        final ILoadMoreSupportAdapter loadMoreAdapter = (ILoadMoreSupportAdapter) adapter;
        if (!mContentListSupport.isRefreshing() && loadMoreAdapter.getLoadMoreSupportedPosition() != IndicatorPosition.NONE
                && loadMoreAdapter.getLoadMoreIndicatorPosition() == IndicatorPosition.NONE) {
            int position = 0;
            if (mContentListSupport.isReachingEnd() && mScrollDirection >= 0) {
                position |= IndicatorPosition.END;
            }
            if (mContentListSupport.isReachingStart() && mScrollDirection <= 0) {
                position |= IndicatorPosition.START;
            }
            resetScrollDirection();
            if (position != 0) {
                mContentListSupport.onLoadMoreContents(position);
            }
        }
    }

    public View.OnTouchListener getOnTouchListener() {
        return mTouchListener;
    }

    static class TouchListener implements View.OnTouchListener {

        private final ContentListScrollListener listener;
        private float mLastY;

        TouchListener(ContentListScrollListener listener) {
            this.listener = listener;
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN: {
                    listener.resetScrollDirection();
                    mLastY = Float.NaN;
                    break;
                }
                case MotionEvent.ACTION_MOVE: {
                    if (!Float.isNaN(mLastY)) {
                        float delta = mLastY - event.getRawY();
                        listener.setScrollDirection(delta < 0 ? -1 : 1);
                    } else {
                        mLastY = event.getRawY();
                    }
                    break;
                }
            }
            return false;
        }
    }

    private void setScrollDirection(int direction) {
        mScrollDirection = direction;
    }

    private void resetScrollDirection() {
        mScrollDirection = 0;
    }

    public interface ViewCallback {
        boolean isComputingLayout();

        void post(Runnable runnable);
    }

    public interface ContentListSupport {

        Object getContentAdapter();

        boolean isRefreshing();

        void onLoadMoreContents(@IndicatorPosition int position);

        void setControlVisible(boolean visible);

        boolean isReachingStart();

        boolean isReachingEnd();

    }

    public static class RecyclerViewCallback implements ViewCallback {
        private final RecyclerView recyclerView;

        public RecyclerViewCallback(RecyclerView recyclerView) {
            this.recyclerView = recyclerView;
        }

        @Override
        public boolean isComputingLayout() {
            return recyclerView.isComputingLayout();
        }

        @Override
        public void post(Runnable action) {
            recyclerView.post(action);
        }
    }
}
