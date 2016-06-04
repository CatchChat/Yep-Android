package catchla.yep.graphic

import android.annotation.TargetApi
import android.graphics.Outline
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.os.Build

import catchla.yep.util.MathUtils

/**
 * Created by mariotaku on 15/6/3.
 */
class ActionBarDrawable(shadow: Drawable) : LayerDrawable(arrayOf(shadow, ActionBarColorDrawable.create(true))) {

    private val mShadowDrawable: Drawable
    private val mColorDrawable: ColorDrawable

    private var mFactor: Float = 0.toFloat()
    var color: Int = 0
        set(value) {
            field = value
            mColorDrawable.color = value
            setFactor(mFactor)
        }
    private var mAlpha: Int = 0
    private var mOutlineAlphaFactor: Float = 0.toFloat()

    init {
        mShadowDrawable = getDrawable(0)
        mColorDrawable = getDrawable(1) as ColorDrawable
        alpha = 0xFF
        setOutlineAlphaFactor(1f)
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    override fun getOutline(outline: Outline) {
        mColorDrawable.getOutline(outline)
        outline.alpha = mFactor * mOutlineAlphaFactor * 0.99f
    }

    override fun setAlpha(alpha: Int) {
        mAlpha = alpha
        setFactor(mFactor)
    }

    override fun getIntrinsicWidth(): Int {
        return mColorDrawable.intrinsicWidth
    }

    override fun getIntrinsicHeight(): Int {
        return mColorDrawable.intrinsicHeight
    }

    fun setFactor(f: Float) {
        mFactor = f
        mShadowDrawable.alpha = Math.round(mAlpha * MathUtils.clamp(1 - f, 0f, 1f))
        val hasColor = color != 0
        mColorDrawable.alpha = if (hasColor) Math.round(mAlpha * MathUtils.clamp(f, 0f, 1f)) else 0
    }

    fun setOutlineAlphaFactor(f: Float) {
        mOutlineAlphaFactor = f
        invalidateSelf()
    }

}
