/*
 * Copyright (c) 2015. Catch Inc,
 */

package catchla.yep.util;

import android.content.Context;
import android.util.TypedValue;

import catchla.yep.R;

/**
 * Created by mariotaku on 15/4/30.
 */
public class ThemeUtils {

    public static int getColorAccent(Context context) {
        final TypedValue outValue = new TypedValue();
        if (!context.getTheme().resolveAttribute(android.support.v7.appcompat.R.attr.colorAccent, outValue, true))
            return context.getResources().getColor(R.color.branding_color);
        if (outValue.type == TypedValue.TYPE_REFERENCE)
            return context.getResources().getColor(outValue.resourceId);
        return outValue.data;
    }

}
