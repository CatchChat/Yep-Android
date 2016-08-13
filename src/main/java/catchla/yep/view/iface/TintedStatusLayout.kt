/*
 * Copyright (c) 2015. Catch Inc,
 */

package catchla.yep.view.iface

/**
 * Created by mariotaku on 15/4/27.
 */
interface TintedStatusLayout : IExtendedView {
    fun setStatusBarColorDarken(color: Int)

    var setPaddingEnabled: Boolean
}
