/*
 * Copyright (c) 2015. Catch Inc,
 */

package catchla.yep.util;

import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnScrollListener;

import catchla.yep.adapter.iface.ILoadMoreSupportAdapter;


/**
 * Created by mariotaku on 15/3/15.
 */
public class ContentListScrollListener extends OnScrollListener {

    private int mScrollState;
    private int mScrollSum;
    private int mTouchSlop;

    private ContentListSupport mContentListSupport;

    public ContentListScrollListener(@NonNull ContentListSupport contentListSupport) {
        mContentListSupport = contentListSupport;
    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        if (mScrollState != RecyclerView.SCROLL_STATE_IDLE) {
            notifyScrollStateChanged(recyclerView);
        }
        mScrollState = newState;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
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
            notifyScrollStateChanged(recyclerView);
        }
    }

    public void setTouchSlop(int touchSlop) {
        mTouchSlop = touchSlop;
    }

    private void notifyScrollStateChanged(RecyclerView recyclerView) {
        final Object adapter = mContentListSupport.getAdapter();
        if (!(adapter instanceof ILoadMoreSupportAdapter)) return;
        final ILoadMoreSupportAdapter loadMoreAdapter = (ILoadMoreSupportAdapter) adapter;
        final LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        if (!mContentListSupport.isRefreshing() && loadMoreAdapter.isLoadMoreSupported()
                && !loadMoreAdapter.isLoadMoreIndicatorVisible()
                && layoutManager.findLastVisibleItemPosition() == layoutManager.getItemCount() - 1) {
            mContentListSupport.onLoadMoreContents();
        }
    }

    public interface ContentListSupport {

        Object getAdapter();

        boolean isRefreshing();

        void onLoadMoreContents();

        void setControlVisible(boolean visible);

    }
}
