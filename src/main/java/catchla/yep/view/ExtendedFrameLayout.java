/*
 * Copyright (c) 2015. Catch Inc,
 */

package catchla.yep.view;

import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import catchla.yep.view.iface.IExtendedView;


public class ExtendedFrameLayout extends FrameLayout implements IExtendedView {

    private TouchInterceptor touchInterceptor;
    private OnSizeChangedListener onSizeChangedListener;
    private OnFitSystemWindowsListener onFitSystemWindowsListener;

    public ExtendedFrameLayout(final Context context) {
        super(context);
    }

    public ExtendedFrameLayout(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    public ExtendedFrameLayout(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public final boolean dispatchTouchEvent(@NonNull final MotionEvent event) {
        if (touchInterceptor != null) {
            final boolean ret = touchInterceptor.dispatchTouchEvent(this, event);
            if (ret) return true;
        }
        return super.dispatchTouchEvent(event);
    }

    @Override
    public final boolean onInterceptTouchEvent(final MotionEvent event) {
        if (touchInterceptor != null) {
            final boolean ret = touchInterceptor.onInterceptTouchEvent(this, event);
            if (ret) return true;
        }
        return super.onInterceptTouchEvent(event);
    }

    @Override
    public void setOnFitSystemWindowsListener(OnFitSystemWindowsListener listener) {
        onFitSystemWindowsListener = listener;
    }

    @Override
    public final void setOnSizeChangedListener(final OnSizeChangedListener listener) {
        onSizeChangedListener = listener;
    }

    @Override
    public final void setTouchInterceptor(final TouchInterceptor listener) {
        touchInterceptor = listener;
    }

    @Override
    @SuppressWarnings("deprecation")
    protected boolean fitSystemWindows(@NonNull Rect insets) {
        if (onFitSystemWindowsListener != null) {
            onFitSystemWindowsListener.onFitSystemWindows(insets);
        }
        return super.fitSystemWindows(insets);
    }

    @Override
    public final boolean onTouchEvent(@NonNull final MotionEvent event) {
        if (touchInterceptor != null) {
            final boolean ret = touchInterceptor.onTouchEvent(this, event);
            if (ret) return true;
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected final void onSizeChanged(final int w, final int h, final int oldw, final int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (onSizeChangedListener != null) {
            onSizeChangedListener.onSizeChanged(this, w, h, oldw, oldh);
        }
    }

}
