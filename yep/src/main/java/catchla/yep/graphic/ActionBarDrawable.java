package catchla.yep.graphic;

import android.annotation.TargetApi;
import android.graphics.Outline;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;

import catchla.yep.util.MathUtils;

/**
 * Created by mariotaku on 15/6/3.
 */
public class ActionBarDrawable extends LayerDrawable {

    private final Drawable mShadowDrawable;
    private final ColorDrawable mColorDrawable;

    private float mFactor;
    private int mColor;
    private int mAlpha;
    private float mOutlineAlphaFactor;

    public ActionBarDrawable(Drawable shadow) {
        super(new Drawable[]{shadow, ActionBarColorDrawable.create(true)});
        mShadowDrawable = getDrawable(0);
        mColorDrawable = (ColorDrawable) getDrawable(1);
        setAlpha(0xFF);
        setOutlineAlphaFactor(1);
    }

    public int getColor() {
        return mColor;
    }

    public void setColor(int color) {
        mColor = color;
        mColorDrawable.setColor(color);
        setFactor(mFactor);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void getOutline(Outline outline) {
        mColorDrawable.getOutline(outline);
        outline.setAlpha(mFactor * mOutlineAlphaFactor * 0.99f);
    }

    @Override
    public void setAlpha(int alpha) {
        mAlpha = alpha;
        setFactor(mFactor);
    }

    @Override
    public int getIntrinsicWidth() {
        return mColorDrawable.getIntrinsicWidth();
    }

    @Override
    public int getIntrinsicHeight() {
        return mColorDrawable.getIntrinsicHeight();
    }

    public void setFactor(float f) {
        mFactor = f;
        mShadowDrawable.setAlpha(Math.round(mAlpha * MathUtils.clamp(1 - f, 0, 1)));
        final boolean hasColor = mColor != 0;
        mColorDrawable.setAlpha(hasColor ? Math.round(mAlpha * MathUtils.clamp(f, 0, 1)) : 0);
    }

    public void setOutlineAlphaFactor(float f) {
        mOutlineAlphaFactor = f;
        invalidateSelf();
    }

}
