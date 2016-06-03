/*
 * Copyright (c) 2015. Catch Inc,
 */

package catchla.yep.view

import android.content.Context
import android.support.v4.view.MotionEventCompat
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.util.TypedValue
import android.view.InputDevice
import android.view.MotionEvent

import catchla.yep.util.MouseScrollDirectionDecider


/**
 * Created by mariotaku on 15/3/30.
 */
class RecyclerViewBackport @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : RecyclerView(context, attrs, defStyle) {

    private val mouseScrollDirectionDecider: MouseScrollDirectionDecider
    // This value is used when handling generic motion events.
    private var scrollFactor = Float.NaN

    init {
        mouseScrollDirectionDecider = MouseScrollDirectionDecider(context, scrollFactorBackport)
    }

    override fun onGenericMotionEvent(event: MotionEvent): Boolean {
        val lm = layoutManager ?: return false
        if (event.source and InputDevice.SOURCE_CLASS_POINTER != 0) {
            if (event.action == MotionEventCompat.ACTION_SCROLL) {
                val vScroll: Float
                val hScroll: Float
                if (lm.canScrollVertically()) {
                    vScroll = event.getAxisValue(MotionEvent.AXIS_VSCROLL)
                    if (!mouseScrollDirectionDecider.isVerticalAvailable) {
                        mouseScrollDirectionDecider.guessDirection(event)
                    }
                } else {
                    vScroll = 0f
                }
                if (lm.canScrollHorizontally()) {
                    hScroll = event.getAxisValue(MotionEvent.AXIS_HSCROLL)
                    if (!mouseScrollDirectionDecider.isHorizontalAvailable) {
                        mouseScrollDirectionDecider.guessDirection(event)
                    }
                } else {
                    hScroll = 0f
                }
                if (vScroll != 0f || hScroll != 0f) {
                    val scrollFactor = scrollFactorBackport
                    val horizontalDirection = mouseScrollDirectionDecider.horizontalDirection
                    val verticalDirection = mouseScrollDirectionDecider.verticalDirection
                    val hFactor = if (horizontalDirection != 0) scrollFactor * horizontalDirection else -1f
                    val vFactor = if (verticalDirection != 0) scrollFactor * verticalDirection else -1f
                    smoothScrollBy((hScroll * hFactor).toInt(), (vScroll * vFactor).toInt())
                }
            }
        }
        return false
    }

    /**
     * Ported from View.getVerticalScrollFactor.
     */
    private //listPreferredItemHeight is not defined, no generic scrolling
    val scrollFactorBackport: Float
        get() {
            if (scrollFactor.isNaN()) {
                val outValue = TypedValue()
                if (context.theme.resolveAttribute(android.R.attr.listPreferredItemHeight, outValue, true)) {
                    scrollFactor = outValue.getDimension(context.resources.displayMetrics)
                } else {
                    return 0f
                }
            }
            return scrollFactor
        }

}
