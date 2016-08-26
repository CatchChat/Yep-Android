package catchla.yep.view.holder

import android.content.Context
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import catchla.yep.adapter.TopicsAdapter
import catchla.yep.message.AudioPlayEvent
import catchla.yep.model.FileAttachment
import catchla.yep.model.Topic
import catchla.yep.util.ImageLoaderWrapper
import catchla.yep.util.JsonSerializer
import catchla.yep.view.ActionIconView
import catchla.yep.view.AudioSampleView
import catchla.yep.view.holder.iface.IAudioViewHolder
import com.squareup.otto.Subscribe
import kotlinx.android.synthetic.main.layout_topic_attachment_audio.view.*
import java.util.*

/**
 * Created by mariotaku on 15/12/9.
 */
class AudioTopicViewHolder(
        topicsAdapter: TopicsAdapter,
        itemView: View,
        context: Context,
        imageLoader: ImageLoaderWrapper,
        listener: TopicsAdapter.TopicClickListener?
) : TopicViewHolder(topicsAdapter, itemView, context, imageLoader, listener), IAudioViewHolder {

    override val playPauseView: ActionIconView by lazy { itemView.playPause }
    override val playPauseProgressView: ProgressBar by lazy { itemView.playPauseProgress }
    override val sampleView: AudioSampleView by lazy { itemView.audioSample }
    override val audioLengthView: TextView by lazy { itemView.audioLength }

    private var playListener: Any? = null

    init {
        playPauseView.setOnClickListener {
            val attachment = adapter.getTopic(layoutPosition).attachments.firstOrNull() as? FileAttachment ?: return@setOnClickListener
            listener?.onAudioClick(attachment, it)
        }
    }

    override fun displayTopic(topic: Topic, highlight: String?) {
        super.displayTopic(topic, highlight)
        val attachment = topic.attachments[0] as? FileAttachment
        val url = attachment?.file?.url
        val metadata = JsonSerializer.parse(attachment?.metadata, FileAttachment.AudioMetadata::class.java)
        if (metadata != null) {
            val secs = Math.round(metadata.duration % 60)
            val mins = metadata.duration.toInt() / secs
            audioLengthView.text = String.format(Locale.US, "%2d:%2d", mins, secs)
            sampleView.samples = metadata.samples
        }

        playListener = object {
            @Subscribe
            fun onAudioPlayEvent(event: AudioPlayEvent) {
                if (event.url != url) return
                updateAudioPlayEvent(event)
            }
        }
        adapter.bus.register(playListener)
        if (url != null) {
            val state = adapter.audioPlayer.getState(url)
            updateAudioPlayEvent(state)
        }
    }


    override fun onRecycled() {
        if (playListener != null) {
            adapter.bus.unregister(playListener)
            playListener = null
        }
    }

}
