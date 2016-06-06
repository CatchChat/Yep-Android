package catchla.yep.graphic

import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.PixelFormat
import android.graphics.drawable.Drawable

class EmptyDrawable(
        private val intrinsicWidth: Int,
        private val intrinsicHeight: Int,
        private val minimumWidth: Int,
        private val minimumHeight: Int
) : Drawable() {

    constructor(drawableToCopySize: Drawable) : this(drawableToCopySize.minimumWidth, drawableToCopySize.minimumHeight,
            drawableToCopySize.intrinsicWidth, drawableToCopySize.intrinsicHeight)

    override fun getMinimumHeight(): Int {
        return minimumHeight
    }

    override fun getMinimumWidth(): Int {
        return minimumWidth
    }

    override fun getIntrinsicHeight(): Int {
        return intrinsicHeight
    }

    override fun getIntrinsicWidth(): Int {
        return intrinsicWidth
    }

    override fun draw(canvas: Canvas) {
    }

    override fun getOpacity(): Int {
        return PixelFormat.TRANSPARENT
    }

    override fun setAlpha(alpha: Int) {
    }

    override fun setColorFilter(cf: ColorFilter?) {
    }

}
