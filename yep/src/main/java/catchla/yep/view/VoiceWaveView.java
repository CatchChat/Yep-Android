package catchla.yep.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;

import catchla.yep.R;
import catchla.yep.util.MathUtils;

/**
 * Created by kevin on 14/12/23.
 */
public class VoiceWaveView extends View {

    public int mAmplitude;
    private Path mPath;
    private float mDimension;
    private float mDimension1;
    private int numberOfWaves = 5;
    private float mFrequency = 1.2f;
    private float density = 1.f;
    private double mPhase = 0.f;
    private float mPhaseShift = 0.25f;
    private float mMaxAmplitude = 0;
    private boolean drawlock = false;
    private Paint paintsArray;
    private int mViewWidth = 0;
    private int mViewHeight = 0;
    private float ViewMid = 0;
    private boolean mRecordingStarted;
    private ArrayList<Float> mSamplesList = new ArrayList<>();

    public VoiceWaveView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public VoiceWaveView(Context context) {
        super(context);
        init();
    }

    public int getAmplitude() {
        return mAmplitude;
    }

    public void setAmplitude(final int amplitude) {
        mAmplitude = (mAmplitude + amplitude) / 2;
        mSamplesList.add(amplitude / (float) Short.MAX_VALUE);
        phaseNext();
        invalidate();
    }

    private void init() {
        Resources res = getResources();
        paintsArray = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintsArray.setColor(0x32a7ff);
        paintsArray.setStyle(Paint.Style.STROKE);

        mDimension = res.getDimension(R.dimen.waver_width);
        mDimension1 = res.getDimension(R.dimen.waver_width_min);

        mPath = new Path();

        setAmplitude(0);
    }


    @Override
    protected void onSizeChanged(int xNew, int yNew, int xOld, int yOld) {
        super.onSizeChanged(xNew, yNew, xOld, yOld);
        mViewWidth = xNew;
        mViewHeight = yNew;
        ViewMid = mViewWidth / 2.0f;
        mMaxAmplitude = mViewHeight / 2.f - 4.0f;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (drawlock) {
            return;
        } else {
            drawlock = true;
        }
        for (int i = 0; i < numberOfWaves; i++) {

            float progress = 1.f - (i / (float) numberOfWaves);
            float normedAmplitude = (1.5f * progress - 0.5f) * mAmplitude / 65536f;

            mPath.reset();

            for (float x = 0.f; x < mViewWidth + density; x += density) {

                //Thanks to https://github.com/stefanceriu/SCSiriWaveformView
                // We use a parable to scale the sinus wave, that has its peak in the middle of the view.
                double scaling = -Math.pow(x / ViewMid - 1.f, 2.f) + 1.f; // make center bigger

                double y = scaling * mMaxAmplitude * normedAmplitude * Math.sin(2 * Math.PI * (x / mViewWidth)
                        * mFrequency + mPhase) + mViewHeight / 2.0;

                if (x == 0.f) {
                    mPath.moveTo(x, (float) y);
                } else {
                    mPath.lineTo(x, (float) y);
                }
            }
            float multiplier = Math.min(1.0f, (progress / 3.0f * 2.0f) + (1.0f / 3.0f));

            if (i == 0) {
                paintsArray.setAlpha(255);
                paintsArray.setStrokeWidth(mDimension);
            } else {
                paintsArray.setAlpha((int) ((1.0f * multiplier * 0.4) * 255));
                paintsArray.setStrokeWidth(mDimension1);
            }
            canvas.drawPath(mPath, paintsArray);

        }

        drawlock = false;

    }

    private double phaseNext() {
        mPhase += mPhaseShift;
        if (mPhase > 2 * Math.PI) {
            mPhase = mPhase - 2 * Math.PI;
        }
        return mPhase;
    }


    public float[] stopRecording() {
        mRecordingStarted = false;
        final int size = mSamplesList.size();
        final float[] rawSamplesArray = ArrayUtils.toPrimitive(mSamplesList.toArray(new Float[size]));
        final int idealSampleSize = 20;
        if (size < idealSampleSize) {
            return rawSamplesArray;
        }
        final int gap = size / idealSampleSize;
        final float[] result = new float[idealSampleSize];
        for (int i = 0; i < idealSampleSize; i++) {
            result[i] = MathUtils.avg(rawSamplesArray, i * gap, (i + 1) * gap - 1);
        }
        return result;
    }

    public void startRecording() {
        mRecordingStarted = true;
        mSamplesList.clear();
    }
}