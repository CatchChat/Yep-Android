package org.mariotaku.ktextension

/**
 * Created by mariotaku on 16/8/15.
 */
fun Throwable.initCause(cause: Throwable) {
    @Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN")
    (this as java.lang.Throwable).initCause(cause)
}
