/*
 * Copyright (c) 2015. Catch Inc,
 */

package catchla.yep.util;

import android.os.SystemClock;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;

import catchla.yep.view.HeaderDrawerLayout;


/**
 * Created by mariotaku on 14/12/2.
 */
public class SimpleDrawerCallback implements HeaderDrawerLayout.DrawerCallback {

    private final RecyclerView mRecyclerView;

    public SimpleDrawerCallback(RecyclerView recyclerView) {
        mRecyclerView = recyclerView;
    }


    @Override
    public void fling(float velocity) {
        mRecyclerView.fling(0, (int) velocity);
    }

    @Override
    public void scrollBy(float dy) {
        mRecyclerView.scrollBy(0, (int) dy);
    }

    @Override
    public boolean canScroll(float dy) {
        return mRecyclerView.canScrollVertically((int) dy);
    }

    @Override
    public boolean isScrollContent(float x, float y) {
        final int[] location = new int[2];
        mRecyclerView.getLocationOnScreen(location);
        return x >= location[0] && x <= location[0] && y >= location[1] && y <= location[1];
    }

    @Override
    public void cancelTouch() {
        mRecyclerView.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(),
                SystemClock.uptimeMillis(), MotionEvent.ACTION_CANCEL, 0, 0, 0));
    }

    @Override
    public boolean shouldLayoutHeaderBottom() {
        return true;
    }


    @Override
    public void topChanged(int offset) {

    }
}
