/*
 * Copyright (c) 2015. Catch Inc,
 */

package catchla.yep.view;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.InputDevice;
import android.view.MotionEvent;

import catchla.yep.util.MouseScrollDirectionDecider;


/**
 * Created by mariotaku on 15/3/30.
 */
public class RecyclerViewBackport extends RecyclerView {

    private final MouseScrollDirectionDecider mMouseScrollDirectionDecider;
    // This value is used when handling generic motion events.
    private float mScrollFactor = Float.MIN_VALUE;

    public RecyclerViewBackport(Context context) {
        this(context, null);
    }

    public RecyclerViewBackport(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RecyclerViewBackport(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mMouseScrollDirectionDecider = new MouseScrollDirectionDecider(context, getScrollFactorBackport());
    }

    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {
        final LayoutManager lm = getLayoutManager();
        if (lm == null) {
            return false;
        }
        if ((event.getSource() & InputDevice.SOURCE_CLASS_POINTER) != 0) {
            if (event.getAction() == MotionEventCompat.ACTION_SCROLL) {
                final float vScroll, hScroll;
                if (lm.canScrollVertically()) {
                    vScroll = event.getAxisValue(MotionEvent.AXIS_VSCROLL);
                    if (!mMouseScrollDirectionDecider.isVerticalAvailable()) {
                        mMouseScrollDirectionDecider.guessDirection(event);
                    }
                } else {
                    vScroll = 0f;
                }
                if (lm.canScrollHorizontally()) {
                    hScroll = event.getAxisValue(MotionEvent.AXIS_HSCROLL);
                    if (!mMouseScrollDirectionDecider.isHorizontalAvailable()) {
                        mMouseScrollDirectionDecider.guessDirection(event);
                    }
                } else {
                    hScroll = 0f;
                }
                if ((vScroll != 0 || hScroll != 0)) {
                    final float scrollFactor = getScrollFactorBackport();
                    float horizontalDirection = mMouseScrollDirectionDecider.getHorizontalDirection();
                    float verticalDirection = mMouseScrollDirectionDecider.getVerticalDirection();
                    final float hFactor = scrollFactor * (horizontalDirection != 0 ? horizontalDirection : -1);
                    final float vFactor = scrollFactor * (verticalDirection != 0 ? verticalDirection : -1);
                    smoothScrollBy((int) (hScroll * hFactor), (int) (vScroll * vFactor));
                }
            }
        }
        return false;
    }

    /**
     * Ported from View.getVerticalScrollFactor.
     */
    private float getScrollFactorBackport() {
        if (mScrollFactor == Float.MIN_VALUE) {
            TypedValue outValue = new TypedValue();
            if (getContext().getTheme().resolveAttribute(
                    android.R.attr.listPreferredItemHeight, outValue, true)) {
                mScrollFactor = outValue.getDimension(
                        getContext().getResources().getDisplayMetrics());
            } else {
                return 0; //listPreferredItemHeight is not defined, no generic scrolling
            }

        }
        return mScrollFactor;
    }

}
