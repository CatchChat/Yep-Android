/*
 * Copyright (c) 2015. Catch Inc,
 */

package catchla.yep.util;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.internal.view.ContextThemeWrapper;
import android.support.v7.internal.widget.ActionBarOverlayLayout;
import android.support.v7.internal.widget.ContentFrameLayout;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;

import java.lang.reflect.Field;

import catchla.yep.R;
import catchla.yep.graphic.ActionBarColorDrawable;
import catchla.yep.util.view.ViewSupport;
import catchla.yep.view.TabPagerIndicator;

/**
 * Created by mariotaku on 15/4/30.
 */
public class ThemeUtils {

    public static int getColorAccent(Context context) {
        return getColorFromAttribute(context, android.support.v7.appcompat.R.attr.colorAccent, 0);
    }

    public static int getTextColorPrimary(Context context) {
        return getColorFromAttribute(context, android.R.attr.textColorPrimary, 0);
    }


    public static int getColorFromAttribute(Context context, int attr, int def) {
        final TypedValue outValue = new TypedValue();
        if (!context.getTheme().resolveAttribute(attr, outValue, true))
            return def;
        if (outValue.resourceId != 0)
            return context.getResources().getColor(outValue.resourceId);
        return outValue.data;
    }


    @NonNull
    public static Drawable getActionBarBackground(final int primaryColor, final boolean outlineEnabled) {
        return ActionBarColorDrawable.create(primaryColor, outlineEnabled);
    }

    public static Context getActionBarPopupThemedContext(Context base) {
        final TypedValue outValue = new TypedValue();
        final Resources.Theme baseTheme = base.getTheme();
        baseTheme.resolveAttribute(android.support.v7.appcompat.R.attr.actionBarPopupTheme, outValue, true);

        if (outValue.resourceId != 0) {
            final Resources.Theme actionBarTheme = base.getResources().newTheme();
            actionBarTheme.setTo(baseTheme);
            actionBarTheme.applyStyle(outValue.resourceId, true);

            final ContextThemeWrapper actionBarContext = new ContextThemeWrapper(base, outValue.resourceId);
            actionBarContext.getTheme().setTo(actionBarTheme);
            return actionBarContext;
        } else {
            return base;
        }
    }

    public static void initPagerIndicatorAsActionBarTab(FragmentActivity activity, TabPagerIndicator indicator, @Nullable View pagerOverlay) {
        final float supportActionBarElevation = getSupportActionBarElevation(activity);
        final int themeRes = R.style.Theme_Yep_Content;
        final int themeColor = getColorFromAttribute(activity, R.attr.colorPrimary, 0);
        if (activity instanceof AppCompatActivity) {
            final ActionBar actionBar = ((AppCompatActivity) activity).getSupportActionBar();
            if (actionBar != null) {
                actionBar.setElevation(0);
                actionBar.setBackgroundDrawable(getActionBarBackground(themeColor, false));
            }
        }
        ViewCompat.setElevation(indicator, supportActionBarElevation);
        ViewSupport.setBackground(indicator, getActionBarBackground(themeColor, true));
        if (pagerOverlay != null) {
            ViewSupport.setBackground(pagerOverlay, getWindowContentOverlay(activity, themeRes));
        }
    }

    public static Drawable getWindowContentOverlay(final Context context, int themeRes) {
        @SuppressWarnings("ConstantConditions")
        final TypedArray a = context.obtainStyledAttributes(null, new int[]{android.R.attr.windowContentOverlay}, 0, themeRes);
        try {
            return a.getDrawable(0);
        } finally {
            a.recycle();
        }
    }

    public static float getSupportActionBarElevation(final Context context) {
        @SuppressWarnings("ConstantConditions")
        final TypedArray a = context.obtainStyledAttributes(null, new int[]{R.attr.elevation}, R.attr.actionBarStyle, 0);
        try {
            return a.getDimension(0, 0);
        } finally {
            a.recycle();
        }
    }

    public static void setCompatToolbarOverlay(Activity activity, Drawable overlay) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) return;
        final Window window = activity.getWindow();
        final View view = window.findViewById(android.support.v7.appcompat.R.id.decor_content_parent);
        if (!(view instanceof ActionBarOverlayLayout)) return;
        try {
            final Field field = ActionBarOverlayLayout.class.getDeclaredField("mWindowContentOverlay");
            field.setAccessible(true);
            field.set(view, overlay);
        } catch (Exception ignore) {
        }
    }

    public static void setCompatContentViewOverlay(Activity activity, Drawable overlay) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) return;
        final Window window = activity.getWindow();
        View contentLayout = window.findViewById(android.support.v7.appcompat.R.id.action_bar_activity_content);
        if (contentLayout == null) {
            contentLayout = window.findViewById(android.R.id.content);
        }
        if (contentLayout instanceof FrameLayout) {
            ViewSupport.setForeground(contentLayout, overlay);
        }
    }


    public static Drawable getCompatToolbarOverlay(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) return null;
        final Window window = activity.getWindow();
        final View view = window.findViewById(android.support.v7.appcompat.R.id.decor_content_parent);
        if (!(view instanceof ActionBarOverlayLayout)) {
            final View contentLayout = window.findViewById(android.support.v7.appcompat.R.id.action_bar_activity_content);
            if (contentLayout instanceof ContentFrameLayout) {
                return ((ContentFrameLayout) contentLayout).getForeground();
            }
            return null;
        }
        try {
            final Field field = ActionBarOverlayLayout.class.getDeclaredField("mWindowContentOverlay");
            field.setAccessible(true);
            return (Drawable) field.get(view);
        } catch (Exception ignore) {
        }
        return null;
    }
}
