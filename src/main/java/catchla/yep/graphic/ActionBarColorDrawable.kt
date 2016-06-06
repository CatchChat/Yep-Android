/*
 * Copyright (c) 2015. Catch Inc,
 */

package catchla.yep.graphic

import android.annotation.TargetApi
import android.graphics.Outline
import android.os.Build

/**
 * Created by mariotaku on 14/12/8.
 */
class ActionBarColorDrawable : ActionBarColorDrawableBase {
    constructor(outlineEnabled: Boolean) : super(outlineEnabled) {
    }

    constructor(color: Int, outlineEnabled: Boolean) : super(color, outlineEnabled) {
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    override fun getOutline(outline: Outline) {
        if (!isOutlineEnabled) return
        val bounds = bounds
        // Very very dirty hack to make outline shadow in action bar not visible beneath status bar
        outline.setRect(bounds.left - bounds.width() / 2, -bounds.height(),
                bounds.right + bounds.width() / 2, bounds.bottom)
        outline.alpha = alpha / 255f
    }
}
