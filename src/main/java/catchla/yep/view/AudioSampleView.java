package catchla.yep.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import catchla.yep.R;

/**
 * Created by mariotaku on 15/8/25.
 */
public class AudioSampleView extends View {
    private float[] mSamples;
    private Paint mLinePaint;
    private int mLineColor, mLineColorPlayed;
    private int mPlayedIndex;

    public AudioSampleView(final Context context) {
        super(context);
        init(context, null, 0);
    }

    public AudioSampleView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public AudioSampleView(final Context context, final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(final Context context, final AttributeSet attrs, final int defStyleAttr) {
        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.AudioSampleView);
        mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        final float defWidth = context.getResources().getDisplayMetrics().density * 2;
        mLinePaint.setStrokeWidth(a.getDimension(R.styleable.AudioSampleView_asvLineWidth, defWidth));
        mLinePaint.setStrokeCap(Paint.Cap.ROUND);
        mLineColor = a.getColor(R.styleable.AudioSampleView_asvLineColor, Color.WHITE);
        mLineColorPlayed = a.getColor(R.styleable.AudioSampleView_asvLineColorPlayed, Color.BLACK);
        a.recycle();
    }

    public void setSamples(float[] samples) {
        mSamples = samples;
        requestLayout();
    }

    public void setPlayedIndex(int idx) {
        mPlayedIndex = idx;
        invalidate();
    }

    private int getSamplesWidth() {
        if (mSamples == null) return 0;
        return Math.round(mLinePaint.getStrokeWidth() * 2 * mSamples.length);
    }

    @Override
    protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
        final int wSpec = MeasureSpec.makeMeasureSpec(getSamplesWidth() + getPaddingLeft() + getPaddingRight(),
                MeasureSpec.EXACTLY);
        setMeasuredDimension(wSpec, getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec));
    }

    @Override
    protected void onDraw(final Canvas canvas) {
        if (mSamples == null) return;
        final int height = getHeight();
        final int contentHeight = height - getPaddingTop() - getPaddingBottom();
        final int center = getPaddingTop() + contentHeight / 2;
        final float strokeWidth = mLinePaint.getStrokeWidth();
        for (int i = 0, j = mSamples.length; i < j; i++) {
            float x = strokeWidth * 2f * i + (strokeWidth / 2);
            float lineH = contentHeight * mSamples[i];
            mLinePaint.setColor(i < mPlayedIndex ? mLineColor : mLineColorPlayed);
            canvas.drawLine(x, center - lineH / 2, x, center + lineH / 2, mLinePaint);
        }
    }
}
