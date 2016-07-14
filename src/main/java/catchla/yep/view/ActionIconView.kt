package catchla.yep.view

import android.content.Context
import android.content.res.TypedArray
import android.graphics.PorterDuff.Mode
import android.util.AttributeSet
import android.widget.ImageView

/**
 * Created by mariotaku on 14/11/5.
 */
class ActionIconView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : ImageView(context, attrs, defStyleAttr) {

    val defaultColor: Int

    init {
        val a = context.obtainStyledAttributes(attrs, intArrayOf(android.R.attr.color, android.R.attr.colorForeground))
        if (a.hasValue(0)) {
            defaultColor = a.getColor(0, 0)
        } else {
            defaultColor = a.getColor(1, 0)
        }
        setColorFilter(defaultColor, Mode.SRC_ATOP)
        a.recycle()
    }
}
