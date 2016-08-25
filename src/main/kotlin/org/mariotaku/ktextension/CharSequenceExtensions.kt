package org.mariotaku.ktextension

/**
 * Created by mariotaku on 16/8/25.
 */
fun CharSequence.indexOf(string: String, startIndex: Int = 0, ignoreCase: Boolean = false, occurrence: (Int) -> Boolean) {
    var currentIndex = startIndex
    do {
        val findIdx = indexOf(string, currentIndex, ignoreCase)
        if (findIdx < 0) return
        if (!occurrence(findIdx)) {
            return
        }
        currentIndex = findIdx + string.length
    } while (currentIndex >= 0)

}