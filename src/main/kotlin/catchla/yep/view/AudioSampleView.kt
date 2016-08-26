package catchla.yep.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import catchla.yep.R

/**
 * Created by mariotaku on 15/8/25.
 */
class AudioSampleView : View {
    private val linePaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    var samples: FloatArray? = null
        set(value) {
            field = value
            requestLayout()
        }

    val sampleSize: Int
        get() = samples?.size ?: -1

    var lineColor: Int = Color.TRANSPARENT
        set(value) {
            field = value
            invalidate()
        }

    var lineColorPlayed: Int = Color.TRANSPARENT
        set(value) {
            field = value
            invalidate()
        }

    var progress: Float = -1f
        set(idx) {
            field = idx
            invalidate()
        }

    constructor(context: Context) : super(context) {
        init(context, null, 0)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context, attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context, attrs, defStyleAttr)
    }

    private fun init(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.AudioSampleView)
        val defWidth = context.resources.displayMetrics.density * 2
        linePaint.strokeWidth = a.getDimension(R.styleable.AudioSampleView_asvLineWidth, defWidth)
        linePaint.strokeCap = Paint.Cap.ROUND
        lineColor = a.getColor(R.styleable.AudioSampleView_asvLineColor, Color.WHITE)
        lineColorPlayed = a.getColor(R.styleable.AudioSampleView_asvLineColorPlayed, Color.BLACK)
        a.recycle()

        if (isInEditMode) {
            samples = floatArrayOf(0.0f, 0.0f, 0.0f, 0.2f, 0.3f, 0.1f, 0.5f, 0.5f, 0.6f, 0.4f, 0.2f, 0.6f, 0f, 0f)
        }
    }

    private val samplesWidth: Int
        get() {
            val samples = this.samples ?: return 0
            return Math.round(linePaint.strokeWidth * 2f * samples.size)
        }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val wSpec = if (layoutParams?.width == ViewGroup.LayoutParams.WRAP_CONTENT) {
            View.MeasureSpec.makeMeasureSpec(samplesWidth + paddingLeft + paddingRight,
                    View.MeasureSpec.AT_MOST)
        } else {
            widthMeasureSpec
        }
        setMeasuredDimension(wSpec, View.getDefaultSize(suggestedMinimumHeight, heightMeasureSpec))
    }

    override fun onDraw(canvas: Canvas) {
        val samples = samples ?: return


        val strokeWidth = linePaint.strokeWidth
        val contentWidth = width - paddingLeft - paddingRight
        val contentHeight = height - paddingTop - paddingBottom

        val sampleCount = samples.size

        val resampleCount = Math.round(contentWidth / strokeWidth / 2f)

        val playedIndex: Int = if (progress >= 0) {
            Math.round(progress * resampleCount)
        } else {
            -1
        }

        val centerY = paddingTop + contentHeight / 2f
        for (i in 0..resampleCount - 1) {
            val value = samples[i * sampleCount / resampleCount]
            val x = strokeWidth * 2f * i + strokeWidth / 2
            val lineH = Math.max(1f, contentHeight * value)
            val currentPaintColor = if (i > playedIndex) lineColor else lineColorPlayed
            linePaint.color = currentPaintColor
            linePaint.alpha = Color.alpha(currentPaintColor)
            canvas.drawLine(x, centerY - lineH / 2, x, centerY + lineH / 2, linePaint)
        }
    }
}
