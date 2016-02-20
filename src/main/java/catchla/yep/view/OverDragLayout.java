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
    private OnDragListener mOnDragListener;

    public OverDragLayout(final Context context) {
        this(context, null);
    }

    public OverDragLayout(final Context context, final AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public OverDragLayout(final Context context, final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mViewDragHelper = ViewDragHelper.create(this, new ViewDragHelper.Callback() {
            public int top;

            @Override
            public boolean tryCaptureView(final View child, final int pointerId) {
                return mOnDragListener != null && mOnDragListener.shouldStartDragging(child);
            }

            @Override
            public void onViewCaptured(final View capturedChild, final int activePointerId) {
                super.onViewCaptured(capturedChild, activePointerId);
            }

            @Override
            public void onViewReleased(final View releasedChild, final float xvel, final float yvel) {
                if (mOnDragListener != null && mOnDragListener.onReleased(top)) {
                    mViewDragHelper.settleCapturedViewAt(0, 0);
                    ViewCompat.postInvalidateOnAnimation(OverDragLayout.this);
                }
            }

            @Override
            public int clampViewPositionVertical(final View child, final int top, final int dy) {
                if (ViewCompat.canScrollVertically(child, dy)) {
                    return 0;
                }
                return top - dy / 2;
            }

            @Override
            public void onViewPositionChanged(final View changedView, final int left, final int top,
                                              final int dx, final int dy) {
                this.top = top;
                if (mOnDragListener != null) {
                    mOnDragListener.onPositionChanged(top);
                }
                super.onViewPositionChanged(changedView, left, top, dx, dy);
            }

            @Override
            public int getViewVerticalDragRange(final View child) {
                return Integer.MAX_VALUE;
            }
        });
    }

    public void setOnDragListener(OnDragListener listener) {
        mOnDragListener = listener;
    }

    public interface OnDragListener {
        void onPositionChanged(int top);

        boolean onReleased(int top);

        boolean shouldStartDragging(View child);
    }

    @Override
    public void computeScroll() {
        if (mViewDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(OverDragLayout.this);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(final MotionEvent ev) {
        try {
            if (mViewDragHelper.shouldInterceptTouchEvent(ev)) {
                return true;
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            return false;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(final MotionEvent event) {
        mViewDragHelper.processTouchEvent(event);
        return true;
    }

}
