package catchla.yep.view

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

import catchla.yep.R

/**
 * Created by mariotaku on 15/8/25.
 */
class AudioSampleView : View {
    var samples: FloatArray? = null
        set(samples) {
            field = samples
            requestLayout()
        }
    private var linePaint: Paint? = null
    private var lineColor: Int = 0
    private var lineColorPlayed: Int = 0
    var playedIndex: Int = 0
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
        linePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        val defWidth = context.resources.displayMetrics.density * 2
        linePaint!!.strokeWidth = a.getDimension(R.styleable.AudioSampleView_asvLineWidth, defWidth)
        linePaint!!.strokeCap = Paint.Cap.ROUND
        lineColor = a.getColor(R.styleable.AudioSampleView_asvLineColor, Color.WHITE)
        lineColorPlayed = a.getColor(R.styleable.AudioSampleView_asvLineColorPlayed, Color.BLACK)
        a.recycle()
    }

    private val samplesWidth: Int
        get() {
            if (this.samples == null) return 0
            return Math.round(linePaint!!.strokeWidth * 2f * this.samples!!.size.toFloat())
        }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val wSpec = View.MeasureSpec.makeMeasureSpec(samplesWidth + paddingLeft + paddingRight,
                View.MeasureSpec.EXACTLY)
        setMeasuredDimension(wSpec, View.getDefaultSize(suggestedMinimumHeight, heightMeasureSpec))
    }

    override fun onDraw(canvas: Canvas) {
        if (this.samples == null) return
        val height = height
        val contentHeight = height - paddingTop - paddingBottom
        val center = paddingTop + contentHeight / 2
        val strokeWidth = linePaint!!.strokeWidth
        var i = 0
        val j = this.samples!!.size
        while (i < j) {
            val x = strokeWidth * 2f * i.toFloat() + strokeWidth / 2
            val lineH = contentHeight * this.samples!![i]
            linePaint!!.color = if (i < this.playedIndex) lineColor else lineColorPlayed
            canvas.drawLine(x, center - lineH / 2, x, center + lineH / 2, linePaint!!)
            i++
        }
    }
}
