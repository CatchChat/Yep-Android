/*
 * Copyright (c) 2015. Catch Inc,
 */

package catchla.yep.activity.iface

import android.animation.Animator
import android.animation.Animator.AnimatorListener
import android.animation.ObjectAnimator
import android.util.Property
import android.view.animation.DecelerateInterpolator

/**
 * Created by mariotaku on 14/10/21.
 */
interface IControlBarActivity {

    fun setControlBarVisibleAnimate(visible: Boolean)

    var controlBarOffset: Float

    val controlBarHeight: Int

    fun notifyControlBarOffsetChanged()

    fun registerControlBarOffsetListener(listener: ControlBarOffsetListener)

    fun unregisterControlBarOffsetListener(listener: ControlBarOffsetListener)

    interface ControlBarOffsetListener {
        fun onControlBarOffsetChanged(activity: IControlBarActivity, offset: Float)
    }

    class ControlBarShowHideHelper(private val activity: IControlBarActivity) {
        private var controlAnimationDirection: Int = 0

        private class ControlBarOffsetProperty : Property<IControlBarActivity, Float>(java.lang.Float.TYPE, null) {

            override fun set(obj: IControlBarActivity, value: Float) {
                obj.controlBarOffset = value
            }

            override fun get(obj: IControlBarActivity): Float {
                return obj.controlBarOffset
            }

            companion object {
                val SINGLETON = ControlBarOffsetProperty()
            }
        }

        fun setControlBarVisibleAnimate(visible: Boolean) {
            if (controlAnimationDirection != 0) return
            val animator: ObjectAnimator
            val offset = activity.controlBarOffset
            if (visible) {
                if (offset >= 1) return
                animator = ObjectAnimator.ofFloat<IControlBarActivity>(activity, ControlBarOffsetProperty.SINGLETON, offset, 1f)
            } else {
                if (offset <= 0) return
                animator = ObjectAnimator.ofFloat<IControlBarActivity>(activity, ControlBarOffsetProperty.SINGLETON, offset, 0f)
            }
            animator.interpolator = DecelerateInterpolator()
            animator.addListener(object : AnimatorListener {
                override fun onAnimationStart(animation: Animator) {
                }

                override fun onAnimationEnd(animation: Animator) {
                    controlAnimationDirection = 0
                }

                override fun onAnimationCancel(animation: Animator) {
                    controlAnimationDirection = 0
                }

                override fun onAnimationRepeat(animation: Animator) {

                }
            })
            animator.duration = DURATION
            animator.start()
            controlAnimationDirection = if (visible) 1 else -1
        }

        companion object {

            private val DURATION = 200L
        }
    }
}
