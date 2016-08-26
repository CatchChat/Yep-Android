package catchla.yep.view.holder

import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import catchla.yep.fragment.ChatListFragment
import catchla.yep.message.AudioPlayEvent
import catchla.yep.model.FileAttachment
import catchla.yep.model.Message
import catchla.yep.util.JsonSerializer
import catchla.yep.view.ActionIconView
import catchla.yep.view.AudioSampleView
import catchla.yep.view.holder.iface.IAudioViewHolder
import com.squareup.otto.Subscribe
import kotlinx.android.synthetic.main.layout_message_attachment_audio.view.*
import java.util.*

class AudioChatViewHolder(
        itemView: View,
        outgoing: Boolean,
        adapter: ChatListFragment.ChatAdapter
) : MessageViewHolder(itemView, outgoing, adapter), View.OnClickListener, IAudioViewHolder {

    override val playPauseView: ActionIconView by lazy { itemView.playPause }
    override val playPauseProgressView: ProgressBar by lazy { itemView.playPauseProgress }
    override val audioLengthView: TextView by lazy { itemView.audioLength }
    override val sampleView: AudioSampleView by lazy { itemView.audioSample }

    private var playListener: Any? = null

    init {
        playPauseView.setOnClickListener(this)
    }

    override fun displayMessage(message: Message) {
        super.displayMessage(message)
        val url = (message.attachments?.firstOrNull() as? FileAttachment)?.file?.url
        val metadata = getAudioMetadata(message)
        if (metadata != null) {
            audioLengthView.text = String.format(Locale.US, "%.1f\"", metadata.duration)
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

    private fun getAudioMetadata(message: Message): FileAttachment.AudioMetadata? {
        val attachments = message.attachments
        val localMetadata = message.localMetadata
        if (localMetadata != null && !localMetadata.isEmpty()) {
            return JsonSerializer.parse(Message.LocalMetadata.get(localMetadata, "metadata"), FileAttachment.AudioMetadata::class.java)
        } else if (attachments != null && !attachments.isEmpty()) {
            val attachment = attachments[0] as FileAttachment
            return JsonSerializer.parse(attachment.metadata, FileAttachment.AudioMetadata::class.java)
        } else {
            return null
        }
    }

    override fun onClick(v: View) {
        val message = adapter.getMessage(layoutPosition) ?: return
        if (message.mediaType != "audio") return
        val attachment = message.attachments?.firstOrNull() as? FileAttachment ?: return
        adapter.playAudio(attachment)
    }

    override fun onRecycled() {
        if (playListener != null) {
            adapter.bus.unregister(playListener)
            playListener = null
        }
    }


}