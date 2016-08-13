/*
 * Copyright (c) 2015. Catch Inc,
 */

package catchla.yep.view

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.RelativeLayout
import catchla.yep.view.iface.IExtendedView


open class ExtendedRelativeLayout : RelativeLayout, IExtendedView {

    override var touchInterceptor: IExtendedView.TouchInterceptor? = null
    override var onSizeChangedListener: IExtendedView.OnSizeChangedListener? = null
    override var onFitSystemWindowsListener: IExtendedView.OnFitSystemWindowsListener? = null

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle)

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        touchInterceptor?.let {
            val ret = it.dispatchTouchEvent(this, event)
            if (ret) return true
        }
        return super.dispatchTouchEvent(event)
    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        touchInterceptor?.let {
            val ret = it.onInterceptTouchEvent(this, event)
            if (ret) return true
        }
        return super.onInterceptTouchEvent(event)
    }

    @SuppressWarnings("deprecation")
    override fun fitSystemWindows(insets: Rect): Boolean {
        if (onFitSystemWindowsListener != null) {
            onFitSystemWindowsListener!!.onFitSystemWindows(insets)
        }
        return super.fitSystemWindows(insets)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (touchInterceptor != null) {
            val ret = touchInterceptor!!.onTouchEvent(this, event)
            if (ret) return true
        }
        return super.onTouchEvent(event)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        onSizeChangedListener?.onSizeChanged(this, w, h, oldw, oldh)
    }

}
