package catchla.yep.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView

/**
 * Created by mariotaku on 14/11/5.
 */
class MediaSizeImageView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyle: Int = 0
) : ImageView(context, attrs, defStyle) {

    private var mediaWidth: Int = 0
    private var mediaHeight: Int = 0

    fun setMediaSize(width: Int, height: Int) {
        mediaWidth = width
        mediaHeight = height
        requestLayout()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (mediaWidth == 0 || mediaHeight == 0) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
            return
        }
        val whRatio = mediaWidth.toFloat() / mediaHeight
        val width = View.MeasureSpec.getSize(widthMeasureSpec)
        val height = View.MeasureSpec.getSize(heightMeasureSpec)
        val lp = layoutParams
        if (lp.height == ViewGroup.LayoutParams.MATCH_PARENT && lp.width == ViewGroup.LayoutParams.WRAP_CONTENT) {
            val calcWidth = Math.round(height * whRatio)
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(calcWidth, View.MeasureSpec.EXACTLY), heightMeasureSpec)
            setMeasuredDimension(calcWidth, height)
        } else if (lp.width == ViewGroup.LayoutParams.MATCH_PARENT && lp.height == ViewGroup.LayoutParams.WRAP_CONTENT) {
            val calcHeight = Math.round(width / whRatio)
            super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(calcHeight, View.MeasureSpec.EXACTLY))
            setMeasuredDimension(width, calcHeight)
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        }
    }

}
