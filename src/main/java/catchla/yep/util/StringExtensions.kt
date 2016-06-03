package catchla.yep.util

fun String.toLong(def: Long): Long {
    try {
        return toLong();
    } catch (e: NumberFormatException) {
        return def
    }
}

fun String.toInt(def: Int): Int {
    try {
        return toInt();
    } catch (e: NumberFormatException) {
        return def
    }
}
