/*
 * Copyright (c) 2015. Catch Inc,
 */

package catchla.yep.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * Created by mariotaku on 15/5/5.
 */
public class RatioFrameLayout extends FrameLayout {

    private float mWidthHeightRatio = 1;

    public RatioFrameLayout(Context context) {
        super(context);
    }

    public RatioFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RatioFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setWidthHeightRatio(float ratio) {
        mWidthHeightRatio = ratio;
        requestLayout();
    }

    @Override
    protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
        final int width = MeasureSpec.getSize(widthMeasureSpec);
        final int height = Math.round(width * mWidthHeightRatio);
        final int hSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, hSpec);
        setMeasuredDimension(width, height);
    }

}
