/*
 * Copyright (c) 2015. Catch Inc,
 */

package catchla.yep.view

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint.Align
import android.graphics.Rect
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View

/**
 * Created by mariotaku on 14/11/16.
 */
class BadgeView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : View(context, attrs, defStyleAttr) {

    private val textPaint: TextPaint
    var text: String? = null
        set(text) {
            field = text
            updateTextPosition()
            invalidate()
        }
    private var textX: Float = 0.toFloat()
    private var textY: Float = 0.toFloat()
    private val textBounds: Rect

    init {
        textPaint = TextPaint(TextPaint.ANTI_ALIAS_FLAG)
        val a = context.obtainStyledAttributes(attrs,
                intArrayOf(android.R.attr.textColor, android.R.attr.text))
        setColor(a.getColor(0, Color.WHITE))
        text = a.getString(1)
        a.recycle()
        textPaint.textAlign = Align.CENTER
        textBounds = Rect()
    }


    fun setColor(color: Int) {
        textPaint.color = color
        invalidate()
    }

    override fun onSizeChanged(w: Int, h: Int, oldW: Int, oldH: Int) {
        super.onSizeChanged(w, h, oldW, oldH)
        val hPadding = (Math.round(w * (Math.pow(2.0, 0.5) - 1)) / 2).toInt()
        val vPadding = (Math.round(h * (Math.pow(2.0, 0.5) - 1)) / 2).toInt()
        setPadding(hPadding, vPadding, hPadding, vPadding)
        updateTextPosition()
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (!textBounds.isEmpty) {
            canvas.drawText(this.text!!, textX, textY, textPaint)
        }
    }

    private fun updateTextPosition() {
        val width = width
        val height = height
        if (width == 0 || height == 0) return
        val contentWidth = width - paddingLeft - paddingRight.toFloat()
        val contentHeight = height - paddingTop - paddingBottom.toFloat()

        if (this.text != null) {
            textPaint.getTextBounds(this.text, 0, this.text!!.length, textBounds)
            val scale = Math.min(contentWidth / textBounds.width(), contentHeight / textBounds.height())
            textPaint.textSize = Math.min((height / 2).toFloat(), textPaint.textSize * scale)
            textPaint.getTextBounds(this.text, 0, this.text!!.length, textBounds)
            textX = contentWidth / 2 + paddingLeft
            textY = contentHeight / 2 + paddingTop.toFloat() + (textBounds.height() / 2).toFloat()
        } else {
            textBounds.setEmpty()
        }
    }
}
