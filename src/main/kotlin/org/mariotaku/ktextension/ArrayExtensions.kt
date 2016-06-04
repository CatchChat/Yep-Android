package org.mariotaku.ktextension

/**
 * Created by mariotaku on 16/6/4.
 */
fun <T : Any?> Array<T>.toStringArray(): Array<String?> {
    val result = arrayOfNulls<String>(size)
    for (i in 0..size - 1) {
        result[i] = this[i]?.toString()
    }
    return result
}
