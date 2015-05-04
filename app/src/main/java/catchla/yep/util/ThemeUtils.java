/*
 * Copyright (c) 2015. Catch Inc,
 */

package catchla.yep.util;

import android.content.Context;
import android.util.TypedValue;

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
}
