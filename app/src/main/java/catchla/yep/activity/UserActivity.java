/*
 * Copyright (c) 2015. Catch Inc,
 */

package catchla.yep.activity;

import android.annotation.TargetApi;
import android.graphics.Outline;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import catchla.yep.R;
import catchla.yep.fragment.UserFragment;
import catchla.yep.graphic.ActionBarColorDrawable;
import catchla.yep.util.MathUtils;
import catchla.yep.util.ThemeUtils;
import catchla.yep.view.HeaderDrawerLayout;
import catchla.yep.view.TintedStatusFrameLayout;
import catchla.yep.view.iface.IExtendedView;

public class UserActivity extends AppCompatActivity implements HeaderDrawerLayout.DrawerCallback, IExtendedView.OnFitSystemWindowsListener {

    private TintedStatusFrameLayout mMainContent;
    private ActionBarDrawable mActionBarBackground;

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        mMainContent = (TintedStatusFrameLayout) findViewById(R.id.main_content);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        final int primaryColor = ThemeUtils.getColorFromAttribute(this, R.attr.colorPrimary, 0);

        final Drawable shadow = ResourcesCompat.getDrawable(getResources(), R.drawable.shadow_user_banner_action_bar, null);

        mActionBarBackground = new ActionBarDrawable(shadow);
        actionBar.setBackgroundDrawable(mActionBarBackground);
        setContentView(R.layout.activity_user);

        mMainContent.setDrawShadow(true);
        mMainContent.setDrawColor(true);
        mMainContent.setOnFitSystemWindowsListener(this);

        mMainContent.setShadowColor(0xA0000000);
        mActionBarBackground.setColor(primaryColor);
        mMainContent.setColor(primaryColor);

        getSupportFragmentManager().beginTransaction().replace(R.id.main_content, new UserFragment()).commit();

        topChanged(0);

        setTitle("Kevin Zhow");
    }


    @Override
    public boolean canScroll(float dy) {
        return false;
    }

    @Override
    public void cancelTouch() {

    }

    @Override
    public void fling(float velocity) {

    }

    @Override
    public boolean isScrollContent(float x, float y) {
        return false;
    }

    @Override
    public void scrollBy(float dy) {

    }

    @Override
    public boolean shouldLayoutHeaderBottom() {
        return false;
    }

    @Override
    public void topChanged(int offset) {
        if (mActionBarBackground == null || mMainContent == null) return;
        final Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.main_content);
        if (fragment == null) {
            mActionBarBackground.setFactor(0);
            mMainContent.setFactor(0);
            return;
        }
        final int paddingTop = ((UserFragment) fragment).getHeaderPaddingTop();
        final int spaceHeight = ((UserFragment) fragment).getHeaderSpaceHeight();
        if (spaceHeight == 0) {
            mActionBarBackground.setFactor(0);
            mMainContent.setFactor(0);
            return;
        }
        final float factor = (paddingTop - offset) / (float) spaceHeight;
        mActionBarBackground.setFactor(factor);
        mMainContent.setFactor(factor);
    }

    @Override
    public void onFitSystemWindows(Rect insets) {
        final Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.main_content);
        if (fragment instanceof IExtendedView.OnFitSystemWindowsListener) {
            ((IExtendedView.OnFitSystemWindowsListener) fragment).onFitSystemWindows(insets);
        }
    }

    private static class ActionBarDrawable extends LayerDrawable {

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

}
