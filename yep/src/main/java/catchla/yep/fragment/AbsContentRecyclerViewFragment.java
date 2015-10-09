/*
 * Copyright (c) 2015. Catch Inc,
 */

package catchla.yep.fragment;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.widget.FixedLinearLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import catchla.yep.R;
import catchla.yep.activity.iface.IControlBarActivity;
import catchla.yep.adapter.decorator.DividerItemDecoration;
import catchla.yep.adapter.iface.ILoadMoreSupportAdapter;
import catchla.yep.fragment.iface.RefreshScrollTopInterface;
import catchla.yep.util.ContentListScrollListener;
import catchla.yep.util.SimpleDrawerCallback;
import catchla.yep.util.ThemeUtils;
import catchla.yep.util.dagger.ApplicationModule;
import catchla.yep.util.dagger.DaggerGeneralComponent;
import catchla.yep.view.HeaderDrawerLayout;


/**
 * Created by mariotaku on 15/4/16.
 */
public abstract class AbsContentRecyclerViewFragment<A extends ILoadMoreSupportAdapter> extends BaseFragment
        implements OnRefreshListener, HeaderDrawerLayout.DrawerCallback, RefreshScrollTopInterface,
        IControlBarActivity.ControlBarOffsetListener, ContentListScrollListener.ContentListSupport {

    private View mProgressContainer;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private View mErrorContainer;
    private ImageView mErrorIconView;
    private TextView mErrorTextView;

    private LinearLayoutManager mLayoutManager;
    private A mAdapter;

    // Callbacks and listeners
    private SimpleDrawerCallback mDrawerCallback;
    private ContentListScrollListener mScrollListener;

    // Data fields
    private Rect mSystemWindowsInsets = new Rect();

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
        mLayoutManager.scrollToPositionWithOffset(0, 0);
        mRecyclerView.stopScroll();
        setControlVisible(true);
        return true;
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

    public LinearLayoutManager getLayoutManager() {
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
        mLayoutManager = new FixedLinearLayoutManager(context);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                    updateRefreshProgressOffset();
                }
                return false;
            }
        });
        mRecyclerView.setAdapter((RecyclerView.Adapter) mAdapter);

        mScrollListener = new ContentListScrollListener(this);
        mScrollListener.setTouchSlop(ViewConfiguration.get(context).getScaledTouchSlop());
    }

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
        mErrorContainer = view.findViewById(R.id.error_container);
        mErrorIconView = (ImageView) view.findViewById(R.id.error_icon);
        mErrorTextView = (TextView) view.findViewById(R.id.error_text);
    }

    @Override
    public void onDetach() {
        final FragmentActivity activity = getActivity();
        if (activity instanceof IControlBarActivity) {
            ((IControlBarActivity) activity).unregisterControlBarOffsetListener(this);
        }
        super.onDetach();
    }

    @Override
    protected void fitSystemWindows(Rect insets) {
        mRecyclerView.setPadding(insets.left, insets.top, insets.right, insets.bottom);
        mErrorContainer.setPadding(insets.left, insets.top, insets.right, insets.bottom);
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

    protected final void showContent() {
        mErrorContainer.setVisibility(View.GONE);
        mProgressContainer.setVisibility(View.GONE);
        mSwipeRefreshLayout.setVisibility(View.VISIBLE);
    }

    protected final void showProgress() {
        mErrorContainer.setVisibility(View.GONE);
        mProgressContainer.setVisibility(View.VISIBLE);
        mSwipeRefreshLayout.setVisibility(View.GONE);
    }

    protected final void showError(int icon, CharSequence text) {
        mErrorContainer.setVisibility(View.VISIBLE);
        mProgressContainer.setVisibility(View.GONE);
        mSwipeRefreshLayout.setVisibility(View.GONE);
        mErrorIconView.setImageResource(icon);
        mErrorTextView.setText(text);
    }

    protected final void showEmpty(int icon, CharSequence text) {
        mErrorContainer.setVisibility(View.VISIBLE);
        mProgressContainer.setVisibility(View.GONE);
        mSwipeRefreshLayout.setVisibility(View.VISIBLE);
        mErrorIconView.setImageResource(icon);
        mErrorTextView.setText(text);
    }

    protected void updateRefreshProgressOffset() {
        final FragmentActivity activity = getActivity();
        if (!(activity instanceof IControlBarActivity) || mSystemWindowsInsets.top == 0 || mSwipeRefreshLayout == null
                || isRefreshing()) {
            return;
        }
        final float density = getResources().getDisplayMetrics().density;
        final int progressCircleDiameter = mSwipeRefreshLayout.getProgressCircleDiameter();
        final IControlBarActivity control = (IControlBarActivity) activity;
        final int controlBarOffsetPixels = Math.round(control.getControlBarHeight() * (1 - control.getControlBarOffset()));
        final int swipeStart = (mSystemWindowsInsets.top - controlBarOffsetPixels) - progressCircleDiameter;
        // 64: SwipeRefreshLayout.DEFAULT_CIRCLE_TARGET
        final int swipeDistance = Math.round(64 * density);
        mSwipeRefreshLayout.setProgressViewOffset(false, swipeStart, swipeStart + swipeDistance);
    }
}
