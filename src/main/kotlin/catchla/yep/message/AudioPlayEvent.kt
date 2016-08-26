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
        const val DOWNLOAD = 1
        const val START = 2
        const val PAUSE = 3
        const val END = 4
        const val PROGRESS = 5

        fun download(url: String, @FloatRange(from = 0.0, to = 1.0) progress: Float): AudioPlayEvent {
            return AudioPlayEvent(DOWNLOAD, url, progress)
        }

        fun start(url: String, @FloatRange(from = 0.0, to = 1.0) progress: Float): AudioPlayEvent {
            return AudioPlayEvent(START, url, progress)
        }

        fun pause(url: String, @FloatRange(from = 0.0, to = 1.0) progress: Float): AudioPlayEvent {
            return AudioPlayEvent(PAUSE, url, progress)
        }

        fun end(url: String, @FloatRange(from = 0.0, to = 1.0) progress: Float = 0f): AudioPlayEvent {
            return AudioPlayEvent(END, url, progress)
        }

        fun progress(url: String, @FloatRange(from = 0.0, to = 1.0) progress: Float): AudioPlayEvent {
            return AudioPlayEvent(PROGRESS, url, progress)
        }
    }

    @IntDef(value = *longArrayOf(DOWNLOAD.toLong(), START.toLong(), PAUSE.toLong(), END.toLong(),
            PROGRESS.toLong()))
    annotation class EventType
}
