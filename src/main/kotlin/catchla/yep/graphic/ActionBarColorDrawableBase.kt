/*
 * Copyright (c) 2015. Catch Inc,
 */

package catchla.yep.graphic

import android.graphics.drawable.ColorDrawable
import android.os.Build

/**
 * Created by mariotaku on 14/12/8.
 */
open class ActionBarColorDrawableBase : ColorDrawable {

    val isOutlineEnabled: Boolean

    constructor(outlineEnabled: Boolean) : super() {
        this.isOutlineEnabled = outlineEnabled
    }

    constructor(color: Int, outlineEnabled: Boolean) : super(color) {
        this.isOutlineEnabled = outlineEnabled
    }

    companion object {

        fun create(color: Int, outlineEnabled: Boolean): ActionBarColorDrawableBase {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                return ActionBarColorDrawableBase(color, outlineEnabled)
            }
            return ActionBarColorDrawable(color, outlineEnabled)
        }

        fun create(outlineEnabled: Boolean): ActionBarColorDrawableBase {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                return ActionBarColorDrawableBase(outlineEnabled)
            }
            return ActionBarColorDrawable(outlineEnabled)
        }
    }

}
