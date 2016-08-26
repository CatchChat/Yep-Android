package catchla.yep.view.holder.iface

import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import catchla.yep.R
import catchla.yep.message.AudioPlayEvent
import catchla.yep.util.MessageAudioPlayer
import catchla.yep.view.AudioSampleView

/**
 * Created by mariotaku on 16/8/26.
 */
interface IAudioViewHolder {

    val playPauseView: ImageView
    val playPauseProgressView: ProgressBar
    val sampleView: AudioSampleView
    val audioLengthView: TextView

    fun IAudioViewHolder.updateAudioPlayEvent(event: AudioPlayEvent?) {
        if (event == null) {
            playPauseView.visibility = View.VISIBLE
            playPauseProgressView.visibility = View.GONE
            playPauseView.setImageResource(R.drawable.ic_btn_audio_play)
            sampleView.progress = 0f
            return
        }
        when (event.what) {
            AudioPlayEvent.DOWNLOAD -> {
                playPauseView.visibility = View.GONE
                playPauseProgressView.visibility = View.VISIBLE
                sampleView.progress = 0f
            }
            AudioPlayEvent.START -> {
                playPauseView.visibility = View.VISIBLE
                playPauseProgressView.visibility = View.GONE
                playPauseView.setImageResource(R.drawable.ic_btn_audio_pause)
                sampleView.progress = event.progress
            }
            AudioPlayEvent.PAUSE -> {
                playPauseView.visibility = View.VISIBLE
                playPauseProgressView.visibility = View.GONE
                playPauseView.setImageResource(R.drawable.ic_btn_audio_play)
                sampleView.progress = event.progress
            }
            AudioPlayEvent.END -> {
                playPauseView.visibility = View.VISIBLE
                playPauseProgressView.visibility = View.GONE
                playPauseView.setImageResource(R.drawable.ic_btn_audio_play)
                sampleView.progress = 0f
            }
            AudioPlayEvent.PROGRESS -> {
                playPauseView.visibility = View.VISIBLE
                playPauseProgressView.visibility = View.GONE
                playPauseView.setImageResource(R.drawable.ic_btn_audio_pause)
                if (!event.progress.isNaN()) {
                    sampleView.progress = event.progress
                }
            }
        }
    }

}

fun MessageAudioPlayer.clickedPlayPause(url: String) {
    if (isPaused(url)) {
        resume(url)
    } else if (isPlaying(url)) {
        pause(url)
    } else {
        if (isPlaying()) {
            stop()
        }
        play(url)
    }
}