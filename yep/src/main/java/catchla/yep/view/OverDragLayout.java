package catchla.yep.view;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

/**
 * Created by mariotaku on 15/12/11.
 */
public class OverDragLayout extends FrameLayout {


    private final ViewDragHelper mViewDragHelper;

    public OverDragLayout(final Context context) {
        this(context, null);
    }

    public OverDragLayout(final Context context, final AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public OverDragLayout(final Context context, final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mViewDragHelper = ViewDragHelper.create(this, new ViewDragHelper.Callback() {
            @Override
            public boolean tryCaptureView(final View child, final int pointerId) {
                return true;
            }

            @Override
            public void onViewCaptured(final View capturedChild, final int activePointerId) {
                super.onViewCaptured(capturedChild, activePointerId);
            }

            @Override
            public void onViewReleased(final View releasedChild, final float xvel, final float yvel) {
                mViewDragHelper.settleCapturedViewAt(0, 0);
                ViewCompat.postInvalidateOnAnimation(OverDragLayout.this);
            }

            @Override
            public int clampViewPositionVertical(final View child, final int top, final int dy) {
                if (ViewCompat.canScrollVertically(child, dy)) {
                    return 0;
                }
                return top - dy / 2;
            }

            @Override
            public int getViewVerticalDragRange(final View child) {
                return Integer.MAX_VALUE;
            }
        });
    }

    @Override
    public void computeScroll() {
        if (mViewDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(OverDragLayout.this);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(final MotionEvent ev) {
        if (mViewDragHelper.shouldInterceptTouchEvent(ev)) {
            return true;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(final MotionEvent event) {
        mViewDragHelper.processTouchEvent(event);
        return true;
    }

}
