package catchla.yep.view

import android.content.Context
import android.support.v4.view.ViewCompat
import android.support.v4.widget.ViewDragHelper
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout

/**
 * Created by mariotaku on 15/12/11.
 */
class OverDragLayout @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : FrameLayout(context, attrs, defStyleAttr) {

    private var mViewDragHelper: ViewDragHelper? = null
    private var mOnDragListener: OnDragListener? = null

    init {
        mViewDragHelper = ViewDragHelper.create(this, object : ViewDragHelper.Callback() {
            var top: Int = 0

            override fun tryCaptureView(child: View, pointerId: Int): Boolean {
                return mOnDragListener != null && mOnDragListener!!.shouldStartDragging(child)
            }

            override fun onViewCaptured(capturedChild: View?, activePointerId: Int) {
                super.onViewCaptured(capturedChild, activePointerId)
            }

            override fun onViewReleased(releasedChild: View?, xvel: Float, yvel: Float) {
                if (mOnDragListener != null && mOnDragListener!!.onReleased(top)) {
                    mViewDragHelper!!.settleCapturedViewAt(0, 0)
                    ViewCompat.postInvalidateOnAnimation(this@OverDragLayout)
                }
            }

            override fun clampViewPositionVertical(child: View?, top: Int, dy: Int): Int {
                if (ViewCompat.canScrollVertically(child, dy)) {
                    return 0
                }
                return top - dy / 2
            }

            override fun onViewPositionChanged(changedView: View?, left: Int, top: Int,
                                               dx: Int, dy: Int) {
                this.top = top
                if (mOnDragListener != null) {
                    mOnDragListener!!.onPositionChanged(top)
                }
                super.onViewPositionChanged(changedView, left, top, dx, dy)
            }

            override fun getViewVerticalDragRange(child: View?): Int {
                return Integer.MAX_VALUE
            }
        })
    }

    fun setOnDragListener(listener: OnDragListener) {
        mOnDragListener = listener
    }

    interface OnDragListener {
        fun onPositionChanged(top: Int)

        fun onReleased(top: Int): Boolean

        fun shouldStartDragging(child: View): Boolean
    }

    override fun computeScroll() {
        if (mViewDragHelper!!.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this@OverDragLayout)
        }
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        try {
            if (mViewDragHelper!!.shouldInterceptTouchEvent(ev)) {
                return true
            }
        } catch (e: ArrayIndexOutOfBoundsException) {
            return false
        }

        return super.onInterceptTouchEvent(ev)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        mViewDragHelper!!.processTouchEvent(event)
        return true
    }

}
