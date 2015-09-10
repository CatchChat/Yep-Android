package catchla.yep.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import catchla.yep.R;

/**
 * Created by kevin on 14/12/23.
 */
public class VoiceWaveView extends View {

    public float amplitude = 1.0f;
    private Path mPath;
    private float mDimension;
    private float mDimension1;
    private int numberOfWaves = 5;
    private float frequency = 1.2f;
    private float density = 1.f;
    private float mPhase = 0.f;
    private float mPhaseShift = 0.25f;
    private float mMaxAmplitude = 0;
    private boolean drawlock = false;
    private Paint paintsArray;
    private int ViewWidth = 0;
    private int ViewHeight = 0;
    private float ViewMid = 0;

    public VoiceWaveView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public VoiceWaveView(Context context) {
        super(context);
        init();
    }

    public void setMaxAmplitude(final int maxAmplitude) {
//        mPhase += mPhaseShift;
        mMaxAmplitude = (mMaxAmplitude + Math.max(maxAmplitude / 32768f, 0.01f)) / 2;
        invalidate();
    }

    private void init() {
        Resources res = getResources();
        paintsArray = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintsArray.setColor(Color.WHITE);
        paintsArray.setStyle(Paint.Style.STROKE);

        mDimension = res.getDimension(R.dimen.waver_width);
        mDimension1 = res.getDimension(R.dimen.waver_width_min);

        mPath = new Path();
    }


    @Override
    protected void onSizeChanged(int xNew, int yNew, int xOld, int yOld) {
        super.onSizeChanged(xNew, yNew, xOld, yOld);
        ViewWidth = xNew;
        ViewHeight = yNew;
        ViewMid = ViewWidth / 2.0f;
        mMaxAmplitude = ViewHeight / 2.f - 4.0f;
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
            float normedAmplitude = (1.5f * progress - 0.5f) * amplitude;

            mPath.reset();

            for (float x = 0.f; x < ViewWidth + density; x += density) {

                //Thanks to https://github.com/stefanceriu/SCSiriWaveformView
                // We use a parable to scale the sinus wave, that has its peak in the middle of the view.
                double scaling = -Math.pow(x / ViewMid - 1.f, 2.f) + 1.f; // make center bigger

                double y = scaling * mMaxAmplitude * normedAmplitude * Math.sin(2 * 3.141 * (x / ViewWidth) * frequency + mPhase) + ViewHeight / 2.0;

                if (x == 0.f) {
                    mPath.moveTo(x, (float) y);
                } else {
                    mPath.lineTo(x, (float) y);
                }
            }
//            final int width = canvas.getWidth();
//            final int centerY = canvas.getHeight() / 2;
//            mPath.moveTo(0, normedAmplitude);
//            mPath.lineTo(width, normedAmplitude);
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


}