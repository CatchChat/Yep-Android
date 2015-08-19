/*
 * Copyright (c) 2015. Catch Inc,
 */

package catchla.yep.graphic;

import android.annotation.TargetApi;
import android.graphics.Outline;
import android.graphics.Rect;
import android.os.Build;

/**
 * Created by mariotaku on 14/12/8.
 */
public class ActionBarColorDrawable extends ActionBarColorDrawableBase {
    public ActionBarColorDrawable(boolean outlineEnabled) {
        super(outlineEnabled);
    }

    public ActionBarColorDrawable(int color, boolean outlineEnabled) {
        super(color, outlineEnabled);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void getOutline(Outline outline) {
        if (!isOutlineEnabled()) return;
        final Rect bounds = getBounds();
        // Very very dirty hack to make outline shadow in action bar not visible beneath status bar
        outline.setRect(bounds.left - bounds.width() / 2, -bounds.height(),
                bounds.right + bounds.width() / 2, bounds.bottom);
        outline.setAlpha(getAlpha() / 255f);
    }
}
