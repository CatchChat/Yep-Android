package org.mariotaku.ktextension

fun List<*>?.nullOrEmpty(): Boolean {
    return this == null || this.isEmpty()
}