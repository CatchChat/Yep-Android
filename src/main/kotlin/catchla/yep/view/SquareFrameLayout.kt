/*
 * Copyright (c) 2015. Catch Inc,
 */

package catchla.yep.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout

class SquareFrameLayout @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : FrameLayout(context, attrs, defStyle) {

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = View.MeasureSpec.getSize(widthMeasureSpec)
        val height = View.MeasureSpec.getSize(heightMeasureSpec)
        val lp = layoutParams
        if (lp.height == ViewGroup.LayoutParams.MATCH_PARENT && lp.width == ViewGroup.LayoutParams.WRAP_CONTENT) {
            super.onMeasure(makeSpec(heightMeasureSpec, View.MeasureSpec.EXACTLY), makeSpec(heightMeasureSpec, View.MeasureSpec.EXACTLY))
        } else if (lp.width == ViewGroup.LayoutParams.MATCH_PARENT && lp.height == ViewGroup.LayoutParams.WRAP_CONTENT) {
            super.onMeasure(makeSpec(widthMeasureSpec, View.MeasureSpec.EXACTLY), makeSpec(widthMeasureSpec, View.MeasureSpec.EXACTLY))
        } else {
            if (width > height) {
                super.onMeasure(makeSpec(heightMeasureSpec, View.MeasureSpec.EXACTLY), makeSpec(heightMeasureSpec, View.MeasureSpec.EXACTLY))
            } else {
                super.onMeasure(makeSpec(widthMeasureSpec, View.MeasureSpec.EXACTLY), makeSpec(widthMeasureSpec, View.MeasureSpec.EXACTLY))
            }
        }
    }

    private fun makeSpec(spec: Int, mode: Int): Int {
        return View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(spec), mode)
    }

}
