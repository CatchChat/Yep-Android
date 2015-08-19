/*
 * Copyright (c) 2015. Catch Inc,
 */

package catchla.yep.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.util.AttributeSet;

import catchla.yep.R;
import catchla.yep.util.MathUtils;
import catchla.yep.util.Utils;
import catchla.yep.view.iface.TintedStatusLayout;


/**
 * Created by mariotaku on 14/11/26.
 */
public class TintedStatusFrameLayout extends ExtendedFrameLayout implements TintedStatusLayout {

    private final Paint mBlackPaint, mShadowPaint, mColorPaint;
    private boolean mSetPadding;

    private int mStatusBarHeight;
    private float mFactor;
    private int mColorAlpha, mShadowAlpha;
    private boolean mDrawShadow, mDrawColor;
    private Rect mSystemWindowsInsets;

    public TintedStatusFrameLayout(Context context) {
        this(context, null);
    }

    public TintedStatusFrameLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TintedStatusFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TintedStatusLayout);
        setSetPaddingEnabled(a.getBoolean(R.styleable.TintedStatusLayout_setPadding, false));
        a.recycle();
        mBlackPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBlackPaint.setColor(Color.BLACK);
        mShadowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mColorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mSystemWindowsInsets = new Rect();
        setWillNotDraw(false);
        setFactor(1);
    }


    @Override
    public void setColor(int color) {
        setColor(color, Color.alpha(color));
    }

    @Override
    public void setColor(int color, int alpha) {
        mColorPaint.setColor(color);
        mColorAlpha = alpha;
        updateAlpha();
    }

    @Override
    public void setDrawColor(boolean color) {
        mDrawColor = color;
        invalidate();
    }

    @Override
    public void setDrawShadow(boolean shadow) {
        mDrawShadow = shadow;
        invalidate();
    }

    @Override
    public void setFactor(float f) {
        mFactor = f;
        updateAlpha();
    }

    @Override
    public void setShadowColor(int color) {
        mShadowPaint.setColor(color);
        mShadowAlpha = Color.alpha(color);
        updateAlpha();
    }

    @Override
    public void setSetPaddingEnabled(boolean enabled) {
        mSetPadding = enabled;
    }

    public void setStatusBarHeight(int height) {
        mStatusBarHeight = height;
        invalidate();
    }

    @Override
    public void getSystemWindowsInsets(Rect insets) {
        insets.set(mSystemWindowsInsets);
    }

    @Override
    protected void dispatchDraw(@NonNull Canvas canvas) {
        super.dispatchDraw(canvas);
        if (mDrawShadow) {
            canvas.drawRect(0, 0, canvas.getWidth(), mStatusBarHeight, mShadowPaint);
        } else if (mDrawColor) {
            canvas.drawRect(0, 0, canvas.getWidth(), mStatusBarHeight, mBlackPaint);
        }
        canvas.drawRect(0, 0, canvas.getWidth(), mStatusBarHeight, mDrawColor ? mColorPaint : mBlackPaint);
    }

    @Override
    protected boolean fitSystemWindows(@NonNull Rect insets) {
        setStatusBarHeight(Utils.getInsetsTopWithoutActionBarHeight(getContext(), insets.top));
        if (mSetPadding) {
            setPadding(insets.left, insets.top, insets.right, insets.bottom);
        }
        mSystemWindowsInsets.set(insets);
        return super.fitSystemWindows(insets);
    }

    private void updateAlpha() {
        final float f = mFactor;
        mShadowPaint.setAlpha(Math.round(mShadowAlpha * MathUtils.clamp(1 - f, 0, 1)));
        mColorPaint.setAlpha(Math.round(0xFF * MathUtils.clamp(f, 0, 1)));
        invalidate();
    }
}
