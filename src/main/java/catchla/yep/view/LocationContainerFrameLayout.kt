package catchla.yep.view

import android.content.Context
import android.util.AttributeSet

import com.commonsware.cwac.layouts.AspectLockedFrameLayout

/**
 * Created by mariotaku on 16/1/30.
 */
class LocationContainerFrameLayout @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : AspectLockedFrameLayout(context, attrs) {

    init {
        setAspectRatio(2.5)
    }
}
