package catchla.yep.extension

import android.os.Bundle
import android.os.Parcelable

/**
 * Created by mariotaku on 16/8/18.
 */

inline fun Bundle(action: Bundle.() -> Unit): Bundle {
    val bundle = Bundle()
    action(bundle)
    return bundle
}

operator fun <T> Bundle.get(key: String): T? {
    @Suppress("UNCHECKED_CAST")
    return get(key) as? T
}

operator fun Bundle.set(key: String, value: Int) {
    return putInt(key, value)
}

operator fun <T : Parcelable> Bundle.set(key: String, value: T) {
    return putParcelable(key, value)
}