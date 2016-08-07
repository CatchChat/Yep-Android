package catchla.yep.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import catchla.yep.R

/**
 * Created by kevin on 14/12/23.
 */
class VoiceWaveView : View {

    var amplitude: Int = 0
        set(value) {
            field = (field + value) / 2
            phaseNext()
            invalidate()
        }
    private val path = Path()
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private var mDimension: Float = 0.toFloat()
    private var mDimension1: Float = 0.toFloat()
    private val numberOfWaves = 5
    private val mFrequency = 1.2f
    private val density = 1.0f
    private var phase = 0.0
    private val mPhaseShift = 0.25f
    private var maxAmplitude = 0f
    private var drawlock = false
    private var viewWidth = 0
    private var viewHeight = 0
    private var viewMid = 0f
    private var recordingStarted: Boolean = false

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context) : super(context) {
        init()
    }

    private fun init() {
        val res = resources
        paint.color = 0x32a7ff
        paint.style = Paint.Style.STROKE

        mDimension = res.getDimension(R.dimen.waver_width)
        mDimension1 = res.getDimension(R.dimen.waver_width_min)

        amplitude = 0
    }


    override fun onSizeChanged(xNew: Int, yNew: Int, xOld: Int, yOld: Int) {
        super.onSizeChanged(xNew, yNew, xOld, yOld)
        viewWidth = xNew
        viewHeight = yNew
        viewMid = viewWidth / 2.0f
        maxAmplitude = viewHeight / 2.0f - 4.0f
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (drawlock) {
            return
        } else {
            drawlock = true
        }
        for (i in 0 until numberOfWaves) {

            val progress = 1.0f - i / numberOfWaves.toFloat()
            val normedAmplitude = (1.5f * progress - 0.5f) * amplitude / 65536f

            path.reset()

            var x = 0.0f
            while (x < viewWidth + density) {

                //Thanks to https://github.com/stefanceriu/SCSiriWaveformView
                // We use a parable to scale the sinus wave, that has its peak in the middle of the view.
                val scaling = -Math.pow((x / viewMid - 1.0f).toDouble(), 2.0) + 1.0f // make center bigger

                val y = scaling * maxAmplitude.toDouble() * normedAmplitude.toDouble() * Math.sin(2.0 * Math.PI * (x / viewWidth).toDouble() * mFrequency.toDouble() + phase) + viewHeight / 2.0

                if (x == 0.0f) {
                    path.moveTo(x, y.toFloat())
                } else {
                    path.lineTo(x, y.toFloat())
                }
                x += density
            }
            val multiplier = Math.min(1.0f, progress / 3.0f * 2.0f + 1.0f / 3.0f)

            if (i == 0) {
                paint.alpha = 255
                paint.strokeWidth = mDimension
            } else {
                paint.alpha = (1.0 * multiplier.toDouble() * 0.4 * 255).toInt()
                paint.strokeWidth = mDimension1
            }
            canvas.drawPath(path, paint)

        }

        drawlock = false

    }

    private fun phaseNext(): Double {
        phase += mPhaseShift.toDouble()
        if (phase > 2 * Math.PI) {
            phase -= 2 * Math.PI
        }
        return phase
    }


    fun stopRecording() {
        recordingStarted = false
    }

    fun startRecording() {
        recordingStarted = true
    }
}