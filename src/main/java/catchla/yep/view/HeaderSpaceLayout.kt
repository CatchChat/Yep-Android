/*
 * Copyright (c) 2015. Catch Inc,
 */

package catchla.yep.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout

/**
 * Created by mariotaku on 15/5/5.
 */
class HeaderSpaceLayout : FrameLayout {

    var minusTop: Int = 0
    var widthHeightRatio = 1f
        set(ratio) {
            field = ratio
            requestLayout()
        }

    constructor(context: Context) : super(context) {
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = View.MeasureSpec.getSize(widthMeasureSpec)
        val height = Math.round(width * this.widthHeightRatio) - minusTop
        val hSpec = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.getMode(heightMeasureSpec))
        super.onMeasure(widthMeasureSpec, hSpec)
        setMeasuredDimension(width, height)
    }

}
