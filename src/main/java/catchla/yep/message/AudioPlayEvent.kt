package catchla.yep.message

import catchla.yep.model.Attachment

/**
 * Created by mariotaku on 15/10/9.
 */
data class AudioPlayEvent(val what: Int, val attachment: Attachment, val progress: Float) {
    companion object {
        val START = 1
        val END = 2
        val PROGRESS = 3

        fun start(attachment: Attachment): AudioPlayEvent {
            return AudioPlayEvent(START, attachment, 0f)
        }

        fun end(attachment: Attachment): AudioPlayEvent {
            return AudioPlayEvent(END, attachment, 1f)
        }

        fun progress(attachment: Attachment, progress: Float): AudioPlayEvent {
            return AudioPlayEvent(PROGRESS, attachment, progress)
        }
    }
}
