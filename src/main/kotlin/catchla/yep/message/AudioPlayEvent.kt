package catchla.yep.message

import android.support.annotation.FloatRange

/**
 * Created by mariotaku on 15/10/9.
 */
data class AudioPlayEvent(val what: Int, val url: String, val progress: Float = Float.NaN) {
    companion object {
        val START = 1
        val END = 2
        val PROGRESS = 3

        fun start(url: String): AudioPlayEvent {
            return AudioPlayEvent(START, url)
        }

        fun end(url: String): AudioPlayEvent {
            return AudioPlayEvent(END, url)
        }

        fun progress(url: String, @FloatRange(from = 0.0, to = 1.0) progress: Float): AudioPlayEvent {
            return AudioPlayEvent(PROGRESS, url, progress)
        }
    }
}
