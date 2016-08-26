package catchla.yep.util

import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import android.support.annotation.UiThread
import android.util.Log
import catchla.yep.Constants.LOGTAG
import catchla.yep.message.AudioPlayEvent
import com.squareup.otto.Bus
import nl.komponents.kovenant.then
import nl.komponents.kovenant.ui.promiseOnUi
import nl.komponents.kovenant.ui.successUi
import okhttp3.OkHttpClient
import okhttp3.Request
import okio.BufferedSink
import okio.Okio
import java.io.File
import java.io.IOException

/**
 * Created by mariotaku on 16/8/26.
 */
class MessageAudioPlayer(val bus: Bus) {

    private val mediaPlayer: MediaPlayer
    private val handler: Handler

    private var stopRequested: Boolean = false

    init {
        handler = Handler(Looper.getMainLooper())
        mediaPlayer = MediaPlayer()
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC)

    }

    @UiThread
    fun play(url: String): Boolean {
        if (mediaPlayer.isPlaying) {
            return false
        }
        stopRequested = false
        val progressRunnable = ProgressRunnable(mediaPlayer, handler, bus, url)
        mediaPlayer.setOnCompletionListener {
            handler.removeCallbacks(progressRunnable)
            bus.post(AudioPlayEvent.end(url))
        }
        promiseOnUi {
            bus.post(AudioPlayEvent.download(url, 0f))
        }.then {
            var sink: BufferedSink? = null
            var tempFile: File? = null
            try {
                mediaPlayer.reset()
                tempFile = File.createTempFile("voice_dl" + System.currentTimeMillis(), "m4a")
                if (tempFile!!.length() > 0) {
                    mediaPlayer.setDataSource(tempFile.absolutePath)
                } else {
                    val client = OkHttpClient()
                    val response = client.newCall(Request.Builder().url(url).build()).execute()
                    sink = Okio.buffer(Okio.sink(tempFile))!!
                    sink.writeAll(response.body().source())
                    sink.flush()
                    mediaPlayer.setDataSource(tempFile.absolutePath)
                }
                mediaPlayer.prepare()
            } catch (e: IOException) {
                tempFile?.delete()
            } finally {
                Utils.closeSilently(sink)
            }
        }.successUi {
            bus.post(AudioPlayEvent.download(url, 1f))
            if (stopRequested) {
                if (mediaPlayer.isPlaying) {
                    mediaPlayer.stop()
                }
                mediaPlayer.reset()
                return@successUi
            }
            mediaPlayer.start()
            handler.post(progressRunnable)
            bus.post(AudioPlayEvent.start(url))
        }.fail {
            Log.w(LOGTAG, it)
        }
        return true
    }


    fun stop() {
        stopRequested = true
        if (mediaPlayer.isPlaying) {
            mediaPlayer.stop()
        }
    }

    internal class ProgressRunnable(
            val player: MediaPlayer,
            val handler: Handler,
            val bus: Bus,
            val url: String
    ) : Runnable {
        override fun run() {
            val progress = player.currentPosition.toFloat() / player.duration
            bus.post(AudioPlayEvent.progress(url, progress))
            handler.postDelayed(this, 50)
        }

    }
}