/*
 * Copyright (c) 2015. Catch Inc,
 */

package catchla.yep.view.iface

import android.graphics.Rect
import android.view.MotionEvent
import android.view.View

interface IExtendedView {

    var touchInterceptor: TouchInterceptor?

    var onSizeChangedListener: OnSizeChangedListener?

    var onFitSystemWindowsListener: OnFitSystemWindowsListener?

    interface OnFitSystemWindowsListener {
        fun onFitSystemWindows(insets: Rect)
    }

    interface OnSizeChangedListener {
        fun onSizeChanged(view: View, w: Int, h: Int, oldw: Int, oldh: Int)
    }

    interface TouchInterceptor {

        fun dispatchTouchEvent(view: View, event: MotionEvent): Boolean

        fun onInterceptTouchEvent(view: View, event: MotionEvent): Boolean

        fun onTouchEvent(view: View, event: MotionEvent): Boolean

    }
}
