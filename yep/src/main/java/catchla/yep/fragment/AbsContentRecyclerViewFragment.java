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

package catchla.yep.fragment;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;

import catchla.yep.R;
import catchla.yep.activity.iface.IControlBarActivity;
import catchla.yep.adapter.LoadMoreSupportAdapter;
import catchla.yep.fragment.iface.RefreshScrollTopInterface;
import catchla.yep.util.ContentListScrollListener;
import catchla.yep.util.SimpleDrawerCallback;
import catchla.yep.util.ThemeUtils;
import catchla.yep.view.HeaderDrawerLayout;


/**
 * Created by mariotaku on 15/10/26.
 */
public abstract class AbsContentRecyclerViewFragment<A extends LoadMoreSupportAdapter, L extends RecyclerView.LayoutManager> extends BaseFragment
        implements SwipeRefreshLayout.OnRefreshListener, HeaderDrawerLayout.DrawerCallback,
        RefreshScrollTopInterface, IControlBarActivity.ControlBarOffsetListener,
        ContentListScrollListener.ContentListSupport {

    private static final int MIN_SHOW_TIME = 500; // ms
    private static final int MIN_DELAY = 500; // ms

    private View mProgressContainer;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;

    private L mLayoutManager;
    private A mAdapter;

    // Callbacks and listeners
    private SimpleDrawerCallback mDrawerCallback;
    private ContentListScrollListener mScrollListener;

    private long mStartTime = -1;

    private boolean mPostedHide = false;

    private boolean mPostedShow = false;

    private boolean mDismissed = false;

    private final Runnable mDelayedShowContent = new Runnable() {

        @Override
        public void run() {
            mPostedHide = false;
            mStartTime = -1;
            showContentInternal(true);
        }
    };

    private final Runnable mDelayedShowProgress = new Runnable() {

        @Override
        public void run() {
            mPostedShow = false;
            if (!mDismissed) {
                mStartTime = System.currentTimeMillis();
                showProgressInternal(true);
            }
        }
    };

    // Data fields
    private Rect mSystemWindowsInsets = new Rect();

    private Handler mHandler = new Handler();

    @Override
    public boolean canScroll(float dy) {
        return mDrawerCallback.canScroll(dy);
    }

    @Override
    public void cancelTouch() {
        mDrawerCallback.cancelTouch();
    }

    @Override
    public void fling(float velocity) {
        mDrawerCallback.fling(velocity);
    }

    @Override
    public boolean isScrollContent(float x, float y) {
        return mDrawerCallback.isScrollContent(x, y);
    }

    @Override
    public void onControlBarOffsetChanged(IControlBarActivity activity, float offset) {
        updateRefreshProgressOffset();
    }

    @Override
    public void onRefresh() {
        triggerRefresh();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        updateRefreshProgressOffset();
    }

    @Override
    public void scrollBy(float dy) {
        mDrawerCallback.scrollBy(dy);
    }

    @Override
    public boolean scrollToStart() {
        scrollToPositionWithOffset(0, 0);
        mRecyclerView.stopScroll();
        setControlVisible(true);
        return true;
    }

    protected abstract void onScrollToPositionWithOffset(final L layoutManager, int position, int offset);

    public final void scrollToPositionWithOffset(int position, int offset) {
        onScrollToPositionWithOffset(mLayoutManager, position, offset);
    }

    @Override
    public void setControlVisible(boolean visible) {
        final FragmentActivity activity = getActivity();
        if (activity instanceof IControlBarActivity) {
            ((IControlBarActivity) activity).setControlBarVisibleAnimate(visible);
        }
    }

    @Override
    public boolean shouldLayoutHeaderBottom() {
        return mDrawerCallback.shouldLayoutHeaderBottom();
    }

    @Override
    public void topChanged(int offset) {
        mDrawerCallback.topChanged(offset);
    }

    @Override
    public A getAdapter() {
        return mAdapter;
    }

    @Override
    public abstract boolean isRefreshing();

    public L getLayoutManager() {
        return mLayoutManager;
    }

    public void setRefreshing(final boolean refreshing) {
        final boolean currentRefreshing = mSwipeRefreshLayout.isRefreshing();
        if (!currentRefreshing) {
            updateRefreshProgressOffset();
        }
        if (refreshing == currentRefreshing) return;
        final boolean layoutRefreshing = refreshing && !mAdapter.isLoadMoreIndicatorVisible();
        mSwipeRefreshLayout.setRefreshing(layoutRefreshing);
    }

    @Override
    public void onLoadMoreContents() {
        setLoadMoreIndicatorVisible(true);
        setRefreshEnabled(false);
    }

    public final RecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof IControlBarActivity) {
            ((IControlBarActivity) context).registerControlBarOffsetListener(this);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_content_recyclerview, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mDrawerCallback = new SimpleDrawerCallback(mRecyclerView);

        final View view = getView();
        if (view == null) throw new AssertionError();
        final Context context = view.getContext();
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeColors(ThemeUtils.getColorAccent(context));
        mAdapter = onCreateAdapter(context);
        mLayoutManager = onCreateLayoutManager(context);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        setupRecyclerView(context, mRecyclerView, mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        mScrollListener = new ContentListScrollListener(this);
        mScrollListener.setTouchSlop(ViewConfiguration.get(context).getScaledTouchSlop());
    }

    protected abstract void setupRecyclerView(Context context, final RecyclerView recyclerView, final L layoutManager);

    @NonNull
    protected abstract L onCreateLayoutManager(Context context);

    @Override
    public void onStart() {
        super.onStart();
        mRecyclerView.addOnScrollListener(mScrollListener);
    }

    @Override
    public void onStop() {
        mRecyclerView.removeOnScrollListener(mScrollListener);
        super.onStop();
    }

    @Override
    public void onBaseViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onBaseViewCreated(view, savedInstanceState);
        mProgressContainer = view.findViewById(R.id.progress_container);
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_layout);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
    }

    @Override
    public void onDetach() {
        final FragmentActivity activity = getActivity();
        if (activity instanceof IControlBarActivity) {
            ((IControlBarActivity) activity).unregisterControlBarOffsetListener(this);
        }
        super.onDetach();
    }

    @NonNull
    protected Rect getExtraContentPadding() {
        return new Rect();
    }

    @Override
    protected void fitSystemWindows(Rect insets) {
        final Rect extraPadding = getExtraContentPadding();
        mRecyclerView.setPadding(insets.left + extraPadding.left, insets.top + extraPadding.top,
                insets.right + extraPadding.right, insets.bottom + extraPadding.bottom);
        mProgressContainer.setPadding(insets.left, insets.top, insets.right, insets.bottom);
        mSystemWindowsInsets.set(insets);
        updateRefreshProgressOffset();
    }

    public void setLoadMoreIndicatorVisible(boolean visible) {
        mAdapter.setLoadMoreIndicatorVisible(visible);
    }

    public void setRefreshEnabled(boolean enabled) {
        mSwipeRefreshLayout.setEnabled(enabled);
    }

    @Override
    public boolean triggerRefresh() {
        return false;
    }

    @NonNull
    protected abstract A onCreateAdapter(Context context);


    /**
     * Hide the progress view if it is visible. The progress view will not be
     * hidden until it has been shown for at least a minimum show time. If the
     * progress view was not yet visible, cancels showing the progress view.
     */
    public void showContent() {
        mDismissed = true;
        mHandler.removeCallbacks(mDelayedShowProgress);
        long diff = System.currentTimeMillis() - mStartTime;
        if (diff >= MIN_SHOW_TIME || mStartTime == -1) {
            // The progress spinner has been shown long enough
            // OR was not shown yet. If it wasn't shown yet,
            // it will just never be shown.
            showContentInternal(false);
        } else {
            // The progress spinner is shown, but not long enough,
            // so put a delayed message in to hide it when its been
            // shown long enough.
            if (!mPostedHide) {
                mHandler.postDelayed(mDelayedShowContent, MIN_SHOW_TIME - diff);
                mPostedHide = true;
            }
        }
    }

    /**
     * Show the progress view after waiting for a minimum delay. If
     * during that time, hide() is called, the view is never made visible.
     */
    protected void showProgress() {
        // Reset the start time.
        mStartTime = -1;
        mDismissed = false;
        mHandler.removeCallbacks(mDelayedShowContent);
        if (!mPostedShow) {
            mHandler.postDelayed(mDelayedShowProgress, MIN_DELAY);
            mPostedShow = true;
        }
    }

    private void showContentInternal(boolean animate) {
        if (animate) {
            mProgressContainer.animate().alpha(0).setDuration(200).start();
            mSwipeRefreshLayout.animate().alpha(1).setDuration(200).start();
        }
        mProgressContainer.setVisibility(View.GONE);
        mSwipeRefreshLayout.setVisibility(View.VISIBLE);
    }

    private void showProgressInternal(boolean animate) {
        if (animate) {
            mProgressContainer.animate().alpha(1).setDuration(200).start();
            mSwipeRefreshLayout.animate().alpha(0).setDuration(200).start();
        }
        mProgressContainer.setVisibility(View.VISIBLE);
        mSwipeRefreshLayout.setVisibility(View.GONE);
    }


    protected void updateRefreshProgressOffset() {
        final FragmentActivity activity = getActivity();
        final Rect insets = this.mSystemWindowsInsets;
        final SwipeRefreshLayout layout = this.mSwipeRefreshLayout;
        if (!(activity instanceof IControlBarActivity) || insets.top == 0 || layout == null
                || isRefreshing()) {
            return;
        }
        final int progressCircleDiameter = layout.getProgressCircleDiameter();
        if (progressCircleDiameter == 0) return;
        final float density = getResources().getDisplayMetrics().density;
        final IControlBarActivity control = (IControlBarActivity) activity;
        final int controlBarOffsetPixels = Math.round(control.getControlBarHeight() * (1 - control.getControlBarOffset()));
        final int swipeStart = (insets.top - controlBarOffsetPixels) - progressCircleDiameter;
        // 64: SwipeRefreshLayout.DEFAULT_CIRCLE_TARGET
        final int swipeDistance = Math.round(64 * density);
        layout.setProgressViewOffset(false, swipeStart, swipeStart + swipeDistance);
    }
}
