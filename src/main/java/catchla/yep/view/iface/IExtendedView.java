/*
 * Copyright (c) 2015. Catch Inc,
 */

package catchla.yep.view.iface;

import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;

public interface IExtendedView {

    void setOnFitSystemWindowsListener(final OnFitSystemWindowsListener listener);

    void setOnSizeChangedListener(final OnSizeChangedListener listener);

    void setTouchInterceptor(final TouchInterceptor listener);

    interface OnFitSystemWindowsListener {
        void onFitSystemWindows(Rect insets);
    }

    interface OnSizeChangedListener {
        void onSizeChanged(View view, int w, int h, int oldw, int oldh);
    }

    interface TouchInterceptor {

        boolean dispatchTouchEvent(View view, MotionEvent event);

        boolean onInterceptTouchEvent(View view, MotionEvent event);

        boolean onTouchEvent(View view, MotionEvent event);

    }
}
