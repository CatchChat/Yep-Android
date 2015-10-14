package catchla.yep.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.design.widget.AnimationUtilsTrojan;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TrojanValueAnimatorCompat;
import android.support.design.widget.TrojanViewOffsetBehavior;
import android.support.design.widget.ViewUtilsTrojan;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ScrollerCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import catchla.yep.R;
import catchla.yep.util.MathUtils;

/**
 * Created by mariotaku on 15/10/14.
 */
@CoordinatorLayout.DefaultBehavior(UserProfileHeader.Behavior.class)
public class UserProfileHeader extends SquareFrameLayout {


    private static final int PENDING_ACTION_NONE = 0x0;
    private static final int PENDING_ACTION_EXPANDED = 0x1;
    private static final int PENDING_ACTION_COLLAPSED = 0x2;
    private static final int PENDING_ACTION_ANIMATE_ENABLED = 0x4;

    private final List<OnOffsetChangedListener> mListeners;

    private static final int INVALID_SCROLL_RANGE = -1;
    private int mTotalScrollRange = INVALID_SCROLL_RANGE;
    private int mDownPreScrollRange = INVALID_SCROLL_RANGE;
    private int mDownScrollRange = INVALID_SCROLL_RANGE;


    public UserProfileHeader(final Context context) {
        this(context, null);
    }

    public UserProfileHeader(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        mListeners = new ArrayList<>();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        // Invalidate the scroll ranges
        mTotalScrollRange = INVALID_SCROLL_RANGE;
        mDownPreScrollRange = INVALID_SCROLL_RANGE;
        mDownPreScrollRange = INVALID_SCROLL_RANGE;
    }

    /**
     * Add a listener that will be called when the offset of this {@link UserProfileHeader} changes.
     *
     * @param listener The listener that will be called when the offset changes.]
     * @see #removeOnOffsetChangedListener(OnOffsetChangedListener)
     */
    public void addOnOffsetChangedListener(OnOffsetChangedListener listener) {
        if (listener != null && !mListeners.contains(listener)) {
            mListeners.add(listener);
        }
    }

    /**
     * Remove the previously added {@link OnOffsetChangedListener}.
     *
     * @param listener the listener to remove.
     */
    public void removeOnOffsetChangedListener(OnOffsetChangedListener listener) {
        if (listener != null) {
            mListeners.remove(listener);
        }
    }

    /**
     * Interface definition for a callback to be invoked when an {@link UserProfileHeader}'s vertical
     * offset changes.
     */
    public interface OnOffsetChangedListener {
        /**
         * Called when the {@link UserProfileHeader}'s layout offset has been changed. This allows
         * child views to implement custom behavior based on the offset (for instance pinning a
         * view at a certain y value).
         *
         * @param userProfileHeader the {@link UserProfileHeader} which offset has changed
         * @param verticalOffset    the vertical offset for the parent {@link UserProfileHeader}, in px
         */
        void onOffsetChanged(UserProfileHeader userProfileHeader, int verticalOffset);
    }

    public static class Behavior extends TrojanViewOffsetBehavior<UserProfileHeader> {
        private static final int INVALID_POINTER = -1;
        private static final int INVALID_POSITION = -1;
        private int mOffsetDelta;
        private boolean mSkipNestedPreScroll;
        private Runnable mFlingRunnable;
        private ScrollerCompat mScroller;
        private TrojanValueAnimatorCompat mAnimator;
        private int mOffsetToChildIndexOnLayout = INVALID_POSITION;
        private boolean mOffsetToChildIndexOnLayoutIsMinHeight;
        private float mOffsetToChildIndexOnLayoutPerc;
        private boolean mIsBeingDragged;
        private int mActivePointerId = INVALID_POINTER;
        private int mLastMotionY;
        private int mTouchSlop = -1;
        private WeakReference<View> mLastNestedScrollingChildRef;

        public Behavior() {
        }

        public Behavior(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        @Override
        public boolean onStartNestedScroll(CoordinatorLayout parent, UserProfileHeader child,
                                           View directTargetChild, View target, int nestedScrollAxes) {
            // Return true if we're nested scrolling vertically, and we have scrollable children
            // and the scrolling view is big enough to scroll
            final boolean started = (nestedScrollAxes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0
                    && child.hasScrollableChildren()
                    && parent.getHeight() - directTargetChild.getHeight() <= child.getHeight();
            if (started && mAnimator != null) {
                // Cancel any offset animation
                mAnimator.cancel();
            }
            // A new nested scroll has started so clear out the previous ref
            mLastNestedScrollingChildRef = null;
            return started;
        }

        @Override
        public void onNestedPreScroll(CoordinatorLayout coordinatorLayout, UserProfileHeader child,
                                      View target, int dx, int dy, int[] consumed) {
            if (dy != 0 && !mSkipNestedPreScroll) {
                int min, max;
                if (dy < 0) {
                    // We're scrolling down
                    min = -child.getTotalScrollRange();
                    max = min + child.getDownNestedPreScrollRange();
                } else {
                    // We're scrolling up
                    min = -child.getUpNestedPreScrollRange();
                    max = 0;
                }
                consumed[1] = scroll(coordinatorLayout, child, dy, min, max);
            }
        }

        @Override
        public void onNestedScroll(CoordinatorLayout coordinatorLayout, UserProfileHeader child,
                                   View target, int dxConsumed, int dyConsumed,
                                   int dxUnconsumed, int dyUnconsumed) {
            if (dyUnconsumed < 0) {
                // If the scrolling view is scrolling down but not consuming, it's probably be at
                // the top of it's content
                scroll(coordinatorLayout, child, dyUnconsumed,
                        -child.getDownNestedScrollRange(), 0);
                // Set the expanding flag so that onNestedPreScroll doesn't handle any events
                mSkipNestedPreScroll = true;
            } else {
                // As we're no longer handling nested scrolls, reset the skip flag
                mSkipNestedPreScroll = false;
            }
        }

        @Override
        public void onStopNestedScroll(CoordinatorLayout coordinatorLayout, UserProfileHeader child,
                                       View target) {
            // Reset the skip flag
            mSkipNestedPreScroll = false;
            // Keep a reference to the previous nested scrolling child
            mLastNestedScrollingChildRef = new WeakReference<>(target);
        }

        @Override
        public boolean onInterceptTouchEvent(CoordinatorLayout parent, UserProfileHeader child,
                                             MotionEvent ev) {
            if (mTouchSlop < 0) {
                mTouchSlop = ViewConfiguration.get(parent.getContext()).getScaledTouchSlop();
            }
            final int action = ev.getAction();
            // Shortcut since we're being dragged
            if (action == MotionEvent.ACTION_MOVE && mIsBeingDragged) {
                return true;
            }
            switch (MotionEventCompat.getActionMasked(ev)) {
                case MotionEvent.ACTION_MOVE: {
                    final int activePointerId = mActivePointerId;
                    if (activePointerId == INVALID_POINTER) {
                        // If we don't have a valid id, the touch down wasn't on content.
                        break;
                    }
                    final int pointerIndex = MotionEventCompat.findPointerIndex(ev, activePointerId);
                    if (pointerIndex == -1) {
                        break;
                    }
                    final int y = (int) MotionEventCompat.getY(ev, pointerIndex);
                    final int yDiff = Math.abs(y - mLastMotionY);
                    if (yDiff > mTouchSlop) {
                        mIsBeingDragged = true;
                        mLastMotionY = y;
                    }
                    break;
                }
                case MotionEvent.ACTION_DOWN: {
                    mIsBeingDragged = false;
                    final int x = (int) ev.getX();
                    final int y = (int) ev.getY();
                    if (parent.isPointInChildBounds(child, x, y) && canDragAppBarLayout()) {
                        mLastMotionY = y;
                        mActivePointerId = MotionEventCompat.getPointerId(ev, 0);
                    }
                    break;
                }
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:
                    mIsBeingDragged = false;
                    mActivePointerId = INVALID_POINTER;
                    break;
            }
            return mIsBeingDragged;
        }

        @Override
        public boolean onTouchEvent(CoordinatorLayout parent, UserProfileHeader child, MotionEvent ev) {
            if (mTouchSlop < 0) {
                mTouchSlop = ViewConfiguration.get(parent.getContext()).getScaledTouchSlop();
            }
            int x = (int) ev.getX();
            int y = (int) ev.getY();
            switch (MotionEventCompat.getActionMasked(ev)) {
                case MotionEvent.ACTION_DOWN:
                    if (parent.isPointInChildBounds(child, x, y) && canDragAppBarLayout()) {
                        mLastMotionY = y;
                        mActivePointerId = MotionEventCompat.getPointerId(ev, 0);
                    } else {
                        return false;
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    final int activePointerIndex = MotionEventCompat.findPointerIndex(ev,
                            mActivePointerId);
                    if (activePointerIndex == -1) {
                        return false;
                    }
                    y = (int) MotionEventCompat.getY(ev, activePointerIndex);
                    int dy = mLastMotionY - y;
                    if (!mIsBeingDragged && Math.abs(dy) > mTouchSlop) {
                        mIsBeingDragged = true;
                        if (dy > 0) {
                            dy -= mTouchSlop;
                        } else {
                            dy += mTouchSlop;
                        }
                    }
                    if (mIsBeingDragged) {
                        mLastMotionY = y;
                        // We're being dragged so scroll the ABL
                        scroll(parent, child, dy, -child.getDownNestedScrollRange(), 0);
                    }
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    mIsBeingDragged = false;
                    mActivePointerId = INVALID_POINTER;
                    break;
            }
            return true;
        }

        @Override
        public boolean onNestedFling(final CoordinatorLayout coordinatorLayout,
                                     final UserProfileHeader child, View target, float velocityX, float velocityY,
                                     boolean consumed) {
            if (!consumed) {
                // It has been consumed so let's fling ourselves
                return fling(coordinatorLayout, child, -child.getTotalScrollRange(), 0, -velocityY);
            } else {
                // If we're scrolling up and the child also consumed the fling. We'll fake scroll
                // upto our 'collapsed' offset
                int targetScroll;
                if (velocityY < 0) {
                    // We're scrolling down
                    targetScroll = -child.getTotalScrollRange()
                            + child.getDownNestedPreScrollRange();
                    if (getTopBottomOffsetForScrollingSibling() > targetScroll) {
                        // If we're currently expanded more than the target scroll, we'll return false
                        // now. This is so that we don't 'scroll' the wrong way.
                        return false;
                    }
                } else {
                    // We're scrolling up
                    targetScroll = -child.getUpNestedPreScrollRange();
                    if (getTopBottomOffsetForScrollingSibling() < targetScroll) {
                        // If we're currently expanded less than the target scroll, we'll return
                        // false now. This is so that we don't 'scroll' the wrong way.
                        return false;
                    }
                }
                if (getTopBottomOffsetForScrollingSibling() != targetScroll) {
                    animateOffsetTo(coordinatorLayout, child, targetScroll);
                    return true;
                }
            }
            return false;
        }

        private void animateOffsetTo(final CoordinatorLayout coordinatorLayout,
                                     final UserProfileHeader child, int offset) {
            if (mAnimator == null) {
                mAnimator = ViewUtilsTrojan.createAnimator();
                mAnimator.setInterpolator(AnimationUtilsTrojan.DECELERATE_INTERPOLATOR);
                mAnimator.setUpdateListener(new TrojanValueAnimatorCompat.TrojanAnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(TrojanValueAnimatorCompat animator) {
                        setAppBarTopBottomOffset(coordinatorLayout, child,
                                animator.getAnimatedIntValue());
                    }
                });
            } else {
                mAnimator.cancel();
            }
            mAnimator.setIntValues(getTopBottomOffsetForScrollingSibling(), offset);
            mAnimator.start();
        }

        private boolean fling(CoordinatorLayout coordinatorLayout, UserProfileHeader layout, int minOffset,
                              int maxOffset, float velocityY) {
            if (mFlingRunnable != null) {
                layout.removeCallbacks(mFlingRunnable);
            }
            if (mScroller == null) {
                mScroller = ScrollerCompat.create(layout.getContext());
            }
            mScroller.fling(
                    0, getTopBottomOffsetForScrollingSibling(), // curr
                    0, Math.round(velocityY), // velocity.
                    0, 0, // x
                    minOffset, maxOffset); // y
            if (mScroller.computeScrollOffset()) {
                mFlingRunnable = new FlingRunnable(coordinatorLayout, layout);
                ViewCompat.postOnAnimation(layout, mFlingRunnable);
                return true;
            } else {
                mFlingRunnable = null;
                return false;
            }
        }

        private class FlingRunnable implements Runnable {
            private final CoordinatorLayout mParent;
            private final UserProfileHeader mLayout;

            FlingRunnable(CoordinatorLayout parent, UserProfileHeader layout) {
                mParent = parent;
                mLayout = layout;
            }

            @Override
            public void run() {
                if (mLayout != null && mScroller != null && mScroller.computeScrollOffset()) {
                    setAppBarTopBottomOffset(mParent, mLayout, mScroller.getCurrY());
                    // Post ourselves so that we run on the next animation
                    ViewCompat.postOnAnimation(mLayout, this);
                }
            }
        }

        @Override
        public boolean onLayoutChild(CoordinatorLayout parent, UserProfileHeader abl,
                                     int layoutDirection) {
            boolean handled = super.onLayoutChild(parent, abl, layoutDirection);
            final int pendingAction = abl.getPendingAction();
            if (pendingAction != PENDING_ACTION_NONE) {
                final boolean animate = (pendingAction & PENDING_ACTION_ANIMATE_ENABLED) != 0;
                if ((pendingAction & PENDING_ACTION_COLLAPSED) != 0) {
                    final int offset = -abl.getUpNestedPreScrollRange();
                    if (animate) {
                        animateOffsetTo(parent, abl, offset);
                    } else {
                        setAppBarTopBottomOffset(parent, abl, offset);
                    }
                } else if ((pendingAction & PENDING_ACTION_EXPANDED) != 0) {
                    if (animate) {
                        animateOffsetTo(parent, abl, 0);
                    } else {
                        setAppBarTopBottomOffset(parent, abl, 0);
                    }
                }
                // Finally reset the pending state
                abl.resetPendingAction();
            } else if (mOffsetToChildIndexOnLayout >= 0) {
                View child = abl.getChildAt(mOffsetToChildIndexOnLayout);
                int offset = -child.getBottom();
                if (mOffsetToChildIndexOnLayoutIsMinHeight) {
                    offset += ViewCompat.getMinimumHeight(child);
                } else {
                    offset += Math.round(child.getHeight() * mOffsetToChildIndexOnLayoutPerc);
                }
                setTopAndBottomOffset(offset);
                mOffsetToChildIndexOnLayout = INVALID_POSITION;
            }
            // Make sure we update the elevation
            dispatchOffsetUpdates(abl);
            return handled;
        }

        private int scroll(CoordinatorLayout coordinatorLayout, UserProfileHeader userProfileHeader,
                           int dy, int minOffset, int maxOffset) {
            return setAppBarTopBottomOffset(coordinatorLayout, userProfileHeader,
                    getTopBottomOffsetForScrollingSibling() - dy, minOffset, maxOffset);
        }

        private boolean canDragAppBarLayout() {
            if (mLastNestedScrollingChildRef != null) {
                final View view = mLastNestedScrollingChildRef.get();
                return view != null && view.isShown() && !ViewCompat.canScrollVertically(view, -1);
            }
            return false;
        }

        final int setAppBarTopBottomOffset(CoordinatorLayout coordinatorLayout,
                                           UserProfileHeader userProfileHeader, int newOffset) {
            return setAppBarTopBottomOffset(coordinatorLayout, userProfileHeader, newOffset,
                    Integer.MIN_VALUE, Integer.MAX_VALUE);
        }

        final int setAppBarTopBottomOffset(CoordinatorLayout coordinatorLayout,
                                           UserProfileHeader userProfileHeader, int newOffset, int minOffset, int maxOffset) {
            final int curOffset = getTopBottomOffsetForScrollingSibling();
            int consumed = 0;
            if (minOffset != 0 && curOffset >= minOffset && curOffset <= maxOffset) {
                // If we have some scrolling range, and we're currently within the min and max
                // offsets, calculate a new offset
                newOffset = MathUtils.clamp(newOffset, minOffset, maxOffset);
                if (curOffset != newOffset) {
                    boolean offsetChanged = setTopAndBottomOffset(newOffset);
                    // Update how much dy we have consumed
                    consumed = curOffset - newOffset;
                    // Update the stored sibling offset
                    mOffsetDelta = 0;
                    // Dispatch the updates to any listeners
                    dispatchOffsetUpdates(userProfileHeader);
                }
            }
            return consumed;
        }

        private void dispatchOffsetUpdates(UserProfileHeader layout) {
            final List<OnOffsetChangedListener> listeners = layout.mListeners;
            // Iterate backwards through the list so that most recently added listeners
            // get the first chance to decide
            for (int i = 0, z = listeners.size(); i < z; i++) {
                final OnOffsetChangedListener listener = listeners.get(i);
                if (listener != null) {
                    listener.onOffsetChanged(layout, getTopAndBottomOffset());
                }
            }
        }

        private int interpolateOffset(UserProfileHeader layout, final int offset) {
            final int absOffset = Math.abs(offset);
            for (int i = 0, z = layout.getChildCount(); i < z; i++) {
                final View child = layout.getChildAt(i);
                final LayoutParams childLp = (LayoutParams) child.getLayoutParams();
                final Interpolator interpolator = childLp.getScrollInterpolator();
                if (absOffset >= child.getTop() && absOffset <= child.getBottom()) {
                    if (interpolator != null) {
                        int childScrollableHeight = 0;
                        final int flags = childLp.getScrollFlags();
                        if ((flags & LayoutParams.SCROLL_FLAG_SCROLL) != 0) {
                            // We're set to scroll so add the child's height plus margin
                            childScrollableHeight += child.getHeight() + childLp.topMargin
                                    + childLp.bottomMargin;
                            if ((flags & LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED) != 0) {
                                // For a collapsing scroll, we to take the collapsed height
                                // into account.
                                childScrollableHeight -= ViewCompat.getMinimumHeight(child);
                            }
                        }
                        if (childScrollableHeight > 0) {
                            final int offsetForView = absOffset - child.getTop();
                            final int interpolatedDiff = Math.round(childScrollableHeight *
                                    interpolator.getInterpolation(
                                            offsetForView / (float) childScrollableHeight));
                            return Integer.signum(offset) * (child.getTop() + interpolatedDiff);
                        }
                    }
                    // If we get to here then the view on the offset isn't suitable for interpolated
                    // scrolling. So break out of the loop
                    break;
                }
            }
            return offset;
        }

        final int getTopBottomOffsetForScrollingSibling() {
            return getTopAndBottomOffset() + mOffsetDelta;
        }

        @Override
        public Parcelable onSaveInstanceState(CoordinatorLayout parent, UserProfileHeader userProfileHeader) {
            final Parcelable superState = super.onSaveInstanceState(parent, userProfileHeader);
            final int offset = getTopAndBottomOffset();
            // Try and find the first visible child...
            for (int i = 0, count = userProfileHeader.getChildCount(); i < count; i++) {
                View child = userProfileHeader.getChildAt(i);
                final int visBottom = child.getBottom() + offset;
                if (child.getTop() + offset <= 0 && visBottom >= 0) {
                    final SavedState ss = new SavedState(superState);
                    ss.firstVisibleChildIndex = i;
                    ss.firstVisibileChildAtMinimumHeight =
                            visBottom == ViewCompat.getMinimumHeight(child);
                    ss.firstVisibileChildPercentageShown = visBottom / (float) child.getHeight();
                    return ss;
                }
            }
            // Else we'll just return the super state
            return superState;
        }

        @Override
        public void onRestoreInstanceState(CoordinatorLayout parent, UserProfileHeader userProfileHeader,
                                           Parcelable state) {
            if (state instanceof SavedState) {
                final SavedState ss = (SavedState) state;
                super.onRestoreInstanceState(parent, userProfileHeader, ss.getSuperState());
                mOffsetToChildIndexOnLayout = ss.firstVisibleChildIndex;
                mOffsetToChildIndexOnLayoutPerc = ss.firstVisibileChildPercentageShown;
                mOffsetToChildIndexOnLayoutIsMinHeight = ss.firstVisibileChildAtMinimumHeight;
            } else {
                super.onRestoreInstanceState(parent, userProfileHeader, state);
                mOffsetToChildIndexOnLayout = INVALID_POSITION;
            }
        }

        protected static class SavedState extends BaseSavedState {
            int firstVisibleChildIndex;
            float firstVisibileChildPercentageShown;
            boolean firstVisibileChildAtMinimumHeight;

            public SavedState(Parcel source) {
                super(source);
                firstVisibleChildIndex = source.readInt();
                firstVisibileChildPercentageShown = source.readFloat();
                firstVisibileChildAtMinimumHeight = source.readByte() != 0;
            }

            public SavedState(Parcelable superState) {
                super(superState);
            }

            @Override
            public void writeToParcel(Parcel dest, int flags) {
                super.writeToParcel(dest, flags);
                dest.writeInt(firstVisibleChildIndex);
                dest.writeFloat(firstVisibileChildPercentageShown);
                dest.writeByte((byte) (firstVisibileChildAtMinimumHeight ? 1 : 0));
            }

            public static final Creator<SavedState> CREATOR =
                    new Creator<SavedState>() {
                        @Override
                        public SavedState createFromParcel(Parcel source) {
                            return new SavedState(source);
                        }

                        @Override
                        public SavedState[] newArray(int size) {
                            return new SavedState[size];
                        }
                    };
        }
    }

    private boolean hasChildWithInterpolator() {
        return false;
    }

    public static class LayoutParams extends FrameLayout.LayoutParams {
        @Nullable
        public Interpolator getScrollInterpolator() {
            return null;
        }

        /**
         * @hide
         */
        @IntDef(flag = true, value = {
                SCROLL_FLAG_SCROLL,
                SCROLL_FLAG_EXIT_UNTIL_COLLAPSED,
                SCROLL_FLAG_ENTER_ALWAYS,
                SCROLL_FLAG_ENTER_ALWAYS_COLLAPSED
        })
        @Retention(RetentionPolicy.SOURCE)
        public @interface ScrollFlags {
        }

        /**
         * The view will be scroll in direct relation to scroll events. This flag needs to be
         * set for any of the other flags to take effect. If any sibling views
         * before this one do not have this flag, then this value has no effect.
         */
        public static final int SCROLL_FLAG_SCROLL = 0x1;
        /**
         * When exiting (scrolling off screen) the view will be scrolled until it is
         * 'collapsed'. The collapsed height is defined by the view's minimum height.
         *
         * @see ViewCompat#getMinimumHeight(View)
         * @see View#setMinimumHeight(int)
         */
        public static final int SCROLL_FLAG_EXIT_UNTIL_COLLAPSED = 0x2;
        /**
         * When entering (scrolling on screen) the view will scroll on any downwards
         * scroll event, regardless of whether the scrolling view is also scrolling. This
         * is commonly referred to as the 'quick return' pattern.
         */
        public static final int SCROLL_FLAG_ENTER_ALWAYS = 0x4;
        /**
         * An additional flag for 'enterAlways' which modifies the returning view to
         * only initially scroll back to it's collapsed height. Once the scrolling view has
         * reached the end of it's scroll range, the remainder of this view will be scrolled
         * into view. The collapsed height is defined by the view's minimum height.
         *
         * @see ViewCompat#getMinimumHeight(View)
         * @see View#setMinimumHeight(int)
         */
        public static final int SCROLL_FLAG_ENTER_ALWAYS_COLLAPSED = 0x8;
        /**
         * Internal flag which allows quick checking of 'quick return'
         */
        static final int FLAG_QUICK_RETURN = SCROLL_FLAG_SCROLL | SCROLL_FLAG_ENTER_ALWAYS;
        int mScrollFlags = SCROLL_FLAG_SCROLL;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
            TypedArray a = c.obtainStyledAttributes(attrs, R.styleable.AppBarLayout_LayoutParams);
            mScrollFlags = a.getInt(R.styleable.AppBarLayout_LayoutParams_layout_scrollFlags, 0);
            a.recycle();
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(ViewGroup.LayoutParams p) {
            super(p);
        }

        public LayoutParams(MarginLayoutParams source) {
            super(source);
        }

        public LayoutParams(LinearLayout.LayoutParams source) {
            super(source);
        }

        public LayoutParams(LayoutParams source) {
            super(source);
            mScrollFlags = source.mScrollFlags;
        }

        /**
         * Set the scrolling flags.
         *
         * @param flags bitwise int of {@link #SCROLL_FLAG_SCROLL},
         *              {@link #SCROLL_FLAG_EXIT_UNTIL_COLLAPSED}, {@link #SCROLL_FLAG_ENTER_ALWAYS}
         *              and {@link #SCROLL_FLAG_ENTER_ALWAYS_COLLAPSED}.
         * @attr ref android.support.design.R.styleable#AppBarLayout_LayoutParams_layout_scrollFlags
         * @see #getScrollFlags()
         */
        public void setScrollFlags(@ScrollFlags int flags) {
            mScrollFlags = flags;
        }

        /**
         * Returns the scrolling flags.
         *
         * @attr ref android.support.design.R.styleable#AppBarLayout_LayoutParams_layout_scrollFlags
         * @see #setScrollFlags(int)
         */
        @ScrollFlags
        public int getScrollFlags() {
            return mScrollFlags;
        }

    }
}
