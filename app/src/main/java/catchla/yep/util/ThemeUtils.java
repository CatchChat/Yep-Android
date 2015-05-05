/*
 * Copyright (c) 2015. Catch Inc,
 */

package catchla.yep.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.internal.view.ContextThemeWrapper;
import android.util.TypedValue;

import catchla.yep.graphic.ActionBarColorDrawable;

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
        if (outValue.type == TypedValue.TYPE_REFERENCE)
            return context.getResources().getColor(attr);
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
}
