package catchla.yep.util

import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import android.support.annotation.UiThread
import android.util.Log
import catchla.yep.Constants.LOGTAG
import catchla.yep.message.AudioPlayEvent
import com.nostra13.universalimageloader.cache.disc.DiskCache
import com.squareup.otto.Bus
import nl.komponents.kovenant.then
import nl.komponents.kovenant.ui.promiseOnUi
import nl.komponents.kovenant.ui.successUi
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.ResponseBody
import java.io.File
import java.io.IOException
import java.util.*

/**
 * Created by mariotaku on 16/8/26.
 */
class MessageAudioPlayer(private val bus: Bus, private val diskCache: DiskCache) {

    private val mediaPlayer: MediaPlayer
    private val handler: Handler

    private var stopRequested: Boolean = false

    private val states: MutableMap<String, AudioPlayEvent>

    init {
        handler = Handler(Looper.getMainLooper())
        mediaPlayer = MediaPlayer()
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC)
        states = HashMap()
    }

    @UiThread
    fun play(url: String): Boolean {
        if (mediaPlayer.isPlaying) {
            return false
        }
        stopRequested = false
        val progressRunnable = ProgressRunnable(this, handler, bus, url)
        mediaPlayer.setOnCompletionListener {
            handler.removeCallbacks(progressRunnable)
            states.remove(url)
            bus.post(AudioPlayEvent.end(url))
        }
        promiseOnUi {
        }.then {
            var tempFile: File? = null
            var body: ResponseBody? = null
            try {
                mediaPlayer.reset()
                tempFile = diskCache[url]
                if (tempFile != null && tempFile.length() > 0) {
                    mediaPlayer.setDataSource(tempFile.absolutePath)
                } else {
                    handler.post {
                        val event = AudioPlayEvent.download(url, 0f)
                        states[url] = event
                        bus.post(event)
                    }
                    val client = OkHttpClient()
                    val response = client.newCall(Request.Builder().url(url).build()).execute()
                    body = response.body()
                    diskCache.save(url, body.byteStream()) { current, total ->
                        handler.post {
                            val event = AudioPlayEvent.download(url, current / total.toFloat())
                            states[url] = event
                            bus.post(event)
                        }
                        return@save true
                    }
                    tempFile = diskCache[url]
                    handler.post {
                        val event = AudioPlayEvent.download(url, 1f)
                        states[url] = event
                        bus.post(event)
                    }
                    mediaPlayer.setDataSource(tempFile.absolutePath)
                }
                mediaPlayer.prepare()
            } catch (e: IOException) {
                tempFile?.delete()
            } finally {
                Utils.closeSilently(body)
            }
        }.successUi {
            if (stopRequested) {
                states.remove(url)
                bus.post(AudioPlayEvent.end(url))
                if (mediaPlayer.isPlaying) {
                    mediaPlayer.stop()
                }
                mediaPlayer.reset()
                return@successUi
            }
            val start = AudioPlayEvent.start(url, 0f)
            states[url] = start
            handler.post(progressRunnable)
            mediaPlayer.start()
            bus.post(start)
        }.fail {
            states.remove(url)
            Log.w(LOGTAG, it)
        }
        return true
    }


    fun stop() {
        stopRequested = true
        if (mediaPlayer.isPlaying) {
            val started = states.filter { isPlaying(it.value) }
            started.forEach {
                states.remove(it.key)
                bus.post(AudioPlayEvent.end(it.key))
            }
            mediaPlayer.stop()
        }
    }

    fun pause(url: String) {
        val state = getState(url) ?: return
        if (mediaPlayer.isPlaying && isPlaying(state)) {
            val pause = AudioPlayEvent.pause(url, state.progress)
            states[url] = pause
            bus.post(pause)
            mediaPlayer.pause()
        }
    }

    fun resume(url: String) {
        val pause = states[url] ?: return
        if (!mediaPlayer.isPlaying && pause.what == AudioPlayEvent.PAUSE) {
            val start = AudioPlayEvent.start(url, pause.progress)
            states[url] = start
            bus.post(start)
            mediaPlayer.start()
            handler.post(ProgressRunnable(this, handler, bus, url))
        }
    }

    fun isPlaying(): Boolean {
        return mediaPlayer.isPlaying
    }

    fun isPlaying(url: String): Boolean {
        val state = getState(url) ?: return false
        return isPlaying(state)
    }

    fun isPaused(url: String): Boolean {
        return !mediaPlayer.isPlaying && states[url]?.what == AudioPlayEvent.PAUSE
    }

    fun getState(url: String): AudioPlayEvent? {
        return states[url]
    }

    private fun isPlaying(event: AudioPlayEvent): Boolean {
        when (event.what) {
            AudioPlayEvent.START, AudioPlayEvent.PROGRESS -> return true
        }
        return false
    }

    internal class ProgressRunnable(
            val player: MessageAudioPlayer,
            val handler: Handler,
            val bus: Bus,
            val url: String
    ) : Runnable {

        override fun run() {
            if (!player.isPlaying()) return
            val mediaPlayer = player.mediaPlayer
            val progress = mediaPlayer.currentPosition.toFloat() / mediaPlayer.duration
            val event = AudioPlayEvent.progress(url, progress)
            player.states[url] = event
            bus.post(event)
            handler.postDelayed(this, 50)
        }

    }
}