package org.mariotaku.ktextension

import android.annotation.TargetApi
import android.graphics.drawable.Drawable
import android.os.Build
import catchla.yep.util.support.graphic.OutlineCompat

fun Drawable.getOutline(outline: OutlineCompat) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) return
    DrawableSupportLollipop.getOutline(this, outline)
}

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
private object DrawableSupportLollipop {

    fun getOutline(drawable: Drawable, outlineCompat: OutlineCompat) {
        drawable.getOutline(OutlineCompat.OutlineL.getWrapped(outlineCompat))
    }
}