package catchla.yep.message

import android.support.annotation.FloatRange
import android.support.annotation.IntDef

/**
 * Created by mariotaku on 15/10/9.
 */
data class AudioPlayEvent(
        @EventType
        val what: Int,
        val url: String,
        val progress: Float = Float.NaN
) {
    companion object {
        const val START = 1
        const val END = 2
        const val PROGRESS = 3
        const val DOWNLOAD = 4

        fun start(url: String): AudioPlayEvent {
            return AudioPlayEvent(START, url)
        }

        fun end(url: String): AudioPlayEvent {
            return AudioPlayEvent(END, url)
        }

        fun progress(url: String, @FloatRange(from = 0.0, to = 1.0) progress: Float): AudioPlayEvent {
            return AudioPlayEvent(PROGRESS, url, progress)
        }

        fun download(url: String, @FloatRange(from = 0.0, to = 1.0) progress: Float): AudioPlayEvent {
            return AudioPlayEvent(DOWNLOAD, url, progress)
        }
    }

    @IntDef(value = *longArrayOf(START.toLong(), END.toLong(), PROGRESS.toLong(), DOWNLOAD.toLong()))
    annotation class EventType
}
