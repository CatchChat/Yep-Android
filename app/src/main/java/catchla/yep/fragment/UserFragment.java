/*
 * Copyright (c) 2015. Catch Inc,
 */

package catchla.yep.fragment;

import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;

import catchla.yep.R;
import catchla.yep.util.MathUtils;
import catchla.yep.view.HeaderDrawerLayout;

/**
 * Created by mariotaku on 15/4/29.
 */
public class UserFragment extends Fragment implements HeaderDrawerLayout.DrawerCallback {
    private HeaderDrawerLayout mHeaderDrawerLayout;
    private ScrollView mScrollView;
    private ImageView mProfileImageView;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mHeaderDrawerLayout.setDrawerCallback(this);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mHeaderDrawerLayout = (HeaderDrawerLayout) view.findViewById(R.id.header_drawer);
        mScrollView = (ScrollView) view.findViewById(R.id.scroll_view);
        mProfileImageView = (ImageView) view.findViewById(R.id.profile_image);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user, container, false);
    }

    @Override
    public boolean canScroll(float dy) {
        return ViewCompat.canScrollVertically(mScrollView, (int) dy);
    }

    @Override
    public void cancelTouch() {
        mScrollView.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(),
                SystemClock.uptimeMillis(), MotionEvent.ACTION_CANCEL, 0, 0, 0));
    }

    @Override
    public void fling(float velocity) {
        mScrollView.fling((int) velocity);
    }

    @Override
    public boolean isScrollContent(float x, float y) {
        final ScrollView v = mScrollView;
        final int[] location = new int[2];
        v.getLocationInWindow(location);
        return x >= location[0] && x <= location[0] + v.getWidth()
                && y >= location[1] && y <= location[1] + v.getHeight();
    }

    @Override
    public void scrollBy(float dy) {
        mScrollView.scrollBy(0, (int) dy);
    }

    @Override
    public boolean shouldLayoutHeaderBottom() {
        return true;
    }

    @Override
    public void topChanged(int offset) {
        mProfileImageView.setTranslationY(MathUtils.clamp(offset, 0, -mProfileImageView.getHeight()) * 0.3f);
    }
}
