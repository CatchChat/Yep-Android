package catchla.yep.extension

import android.os.Bundle

/**
 * Created by mariotaku on 16/8/18.
 */

inline fun Bundle(action: Bundle.() -> Unit): Bundle {
    val bundle = Bundle()
    action(bundle)
    return bundle
}