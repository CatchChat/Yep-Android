/*
 * Copyright (c) 2015. Catch Inc,
 */

package catchla.yep.graphic;

import android.graphics.drawable.ColorDrawable;
import android.os.Build;

/**
 * Created by mariotaku on 14/12/8.
 */
public class ActionBarColorDrawableBase extends ColorDrawable {

    private final boolean outlineEnabled;

    public ActionBarColorDrawableBase(boolean outlineEnabled) {
        super();
        this.outlineEnabled = outlineEnabled;
    }

    public ActionBarColorDrawableBase(int color, boolean outlineEnabled) {
        super(color);
        this.outlineEnabled = outlineEnabled;
    }

    public boolean isOutlineEnabled() {
        return outlineEnabled;
    }

    public static ActionBarColorDrawableBase create(int color, boolean outlineEnabled) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return new ActionBarColorDrawableBase(color, outlineEnabled);
        }
        return new ActionBarColorDrawable(color, outlineEnabled);
    }

    public static ActionBarColorDrawableBase create(boolean outlineEnabled) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return new ActionBarColorDrawableBase(outlineEnabled);
        }
        return new ActionBarColorDrawable(outlineEnabled);
    }

}
