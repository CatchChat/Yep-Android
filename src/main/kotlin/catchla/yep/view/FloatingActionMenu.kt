package catchla.yep.view

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.support.design.widget.AnimationUtilsAccessor
import android.support.design.widget.FloatingActionButtonImplAccessor
import android.support.v4.view.ViewCompat
import android.support.v7.widget.CardView
import android.util.AttributeSet
import android.view.View

/**
 * Created by mariotaku on 15/12/14.
 */
class FloatingActionMenu : CardView {
    private var hiding: Boolean = false

    constructor(context: Context) : super(context) {
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
    }

    @JvmOverloads fun show(listener: InternalVisibilityChangedListener? = null) {
        if (this.visibility != View.VISIBLE) {
            if (ViewCompat.isLaidOut(this) && !this.isInEditMode) {
                this.alpha = 0f
                this.scaleY = 0f
                this.scaleX = 0f
                this.animate().scaleX(1f).scaleY(1f).alpha(1f).setDuration(FloatingActionButtonImplAccessor.SHOW_HIDE_ANIM_DURATION.toLong()).setInterpolator(AnimationUtilsAccessor.FAST_OUT_SLOW_IN_INTERPOLATOR).setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationStart(animation: Animator) {
                        this@FloatingActionMenu.visibility = View.VISIBLE
                    }

                    override fun onAnimationEnd(animation: Animator) {
                        listener?.onShown()
                    }
                })
            } else {
                this.visibility = View.VISIBLE
                this.alpha = 1f
                this.scaleY = 1f
                this.scaleX = 1f
                listener?.onShown()
            }
        }
    }

    @JvmOverloads fun hide(listener: InternalVisibilityChangedListener? = null) {
        if (hiding || visibility != View.VISIBLE) {
            // A hide animation is in progress, or we're already hidden. Skip the call
            listener?.onHidden()
            return
        }

        if (!ViewCompat.isLaidOut(this) || isInEditMode) {
            // If the view isn't laid out, or we're in the editor, don't run the animation
            this.visibility = View.GONE
            listener?.onHidden()
        } else {
            animate().scaleX(0f).scaleY(0f).alpha(0f).setDuration(FloatingActionButtonImplAccessor.SHOW_HIDE_ANIM_DURATION.toLong()).setInterpolator(AnimationUtilsAccessor.FAST_OUT_SLOW_IN_INTERPOLATOR).setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationStart(animation: Animator) {
                    hiding = true
                    this@FloatingActionMenu.visibility = View.VISIBLE
                }

                override fun onAnimationCancel(animation: Animator) {
                    hiding = false
                }

                override fun onAnimationEnd(animation: Animator) {
                    hiding = false
                    this@FloatingActionMenu.visibility = View.GONE
                    listener?.onHidden()
                }
            })
        }
    }


    interface InternalVisibilityChangedListener {
        fun onShown()

        fun onHidden()
    }
}
