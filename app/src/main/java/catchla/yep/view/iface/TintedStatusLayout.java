/*
 * Copyright (c) 2015. Catch Inc,
 */

package catchla.yep.view.iface;

import android.graphics.Rect;

/**
 * Created by mariotaku on 15/4/27.
 */
public interface TintedStatusLayout extends IExtendedView {
    void setColor(int color);

    void setColor(int color, int alpha);

    void setDrawColor(boolean color);

    void setDrawShadow(boolean shadow);

    void setFactor(float f);

    void setShadowColor(int color);

    void setSetPaddingEnabled(boolean enabled);

    void getSystemWindowsInsets(Rect insets);
}
