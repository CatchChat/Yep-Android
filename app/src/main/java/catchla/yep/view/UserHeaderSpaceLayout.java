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
public class UserHeaderSpaceLayout extends FrameLayout {

    private int mMinusTop;

    public UserHeaderSpaceLayout(Context context) {
        super(context);
    }

    public UserHeaderSpaceLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public UserHeaderSpaceLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setMinusTop(int minusTop) {
        mMinusTop = minusTop;
    }

    @Override
    protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
        final int width = MeasureSpec.getSize(widthMeasureSpec), height = width - mMinusTop;
        final int hSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.getMode(heightMeasureSpec));
        super.onMeasure(widthMeasureSpec, hSpec);
        setMeasuredDimension(width, height);
    }

}
