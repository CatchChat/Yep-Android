package catchla.yep.fragment

import android.content.Context
import android.location.Location
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.LoaderManager
import android.support.v4.content.Loader
import android.support.v7.widget.FixedLinearLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import catchla.yep.Constants
import catchla.yep.R
import catchla.yep.adapter.LoadMoreSupportAdapter
import catchla.yep.message.AudioPlayEvent
import catchla.yep.model.FileAttachment
import catchla.yep.model.Message
import catchla.yep.model.Topic
import catchla.yep.provider.YepDataStore.Messages.MessageState
import catchla.yep.util.JsonSerializer
import catchla.yep.util.StaticMapUrlGenerator
import catchla.yep.util.Utils
import catchla.yep.view.AudioSampleView
import catchla.yep.view.MediaSizeImageView
import catchla.yep.view.StaticMapView
import okhttp3.OkHttpClient
import okhttp3.Request
import okio.BufferedSink
import okio.Okio
import org.mariotaku.abstask.library.AbstractTask
import org.mariotaku.abstask.library.TaskStarter
import java.io.File
import java.util.*

/**
 * List component for chat activities
 * Created by mariotaku on 15/11/16.
 */
abstract class ChatListFragment : AbsContentRecyclerViewFragment<ChatListFragment.ChatAdapter, LinearLayoutManager>(), LoaderManager.LoaderCallbacks<List<Message>> {

    private var mMediaPlayer: MediaPlayer? = null
    private var mJumpToLast: Boolean = false

    private lateinit var topicTitle: TextView
    private lateinit var topicSummary: TextView
    private lateinit var chatTopic: View

    override fun onScrollToPositionWithOffset(layoutManager: LinearLayoutManager, position: Int, offset: Int) {
        layoutManager.scrollToPositionWithOffset(position, offset)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_chat_list, container, false)
    }

    override fun onBaseViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onBaseViewCreated(view, savedInstanceState)
        chatTopic = view.findViewById(R.id.chat_topic)
        topicTitle = view.findViewById(R.id.topic_title) as TextView
        topicSummary = view.findViewById(R.id.topic_summary) as TextView
    }

    override fun isRefreshing(): Boolean {
        return false
    }

    override fun onCreateAdapter(context: Context): ChatAdapter {
        return ChatAdapter(this)
    }

    override fun setupRecyclerView(context: Context, recyclerView: RecyclerView, layoutManager: LinearLayoutManager) {
        layoutManager.stackFromEnd = false
    }

    override fun onCreateLayoutManager(context: Context): LinearLayoutManager {
        return FixedLinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, true)
    }

    override fun onStop() {
        if (mMediaPlayer != null) {
            if (mMediaPlayer!!.isPlaying) {
                mMediaPlayer!!.stop()
            }
            mMediaPlayer!!.release()
            mMediaPlayer = null
        }
        super.onStop()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val topic = topic
        if (topic != null) {
            chatTopic.visibility = View.VISIBLE
            topicTitle.text = Utils.getDisplayName(topic.user)
            topicSummary.text = topic.body
        } else {
            chatTopic.visibility = View.GONE
        }
        loaderManager.initLoader(0, null, this)
        showProgress()
    }

    private val topic: Topic?
        get() = arguments.getParcelable<Topic>(Constants.EXTRA_TOPIC)

    override fun onLoadFinished(loader: Loader<List<Message>>, data: List<Message>) {
        val backupPosition = layoutManager.findFirstVisibleItemPosition()
        val backupMessage: Message?
        val adapter = adapter
        if (backupPosition != RecyclerView.NO_POSITION && !mJumpToLast) {
            backupMessage = adapter.getMessage(backupPosition)
        } else {
            backupMessage = null
        }
        mJumpToLast = false
        adapter.data = data
        showContent()
        if (backupMessage != null && backupMessage.id != null) {
            val position = adapter.findPosition(backupMessage.id)
            if (position != RecyclerView.NO_POSITION) {
                scrollToPositionWithOffset(position, 0)
            }
        } else {
            scrollToStart()
        }
    }

    fun setJumpToLast(jumpToLast: Boolean) {
        mJumpToLast = jumpToLast
    }

    override fun onLoaderReset(loader: Loader<List<Message>>) {
        adapter.data = null
    }

    private fun playAudio(attachment: FileAttachment) {
        if ("audio" != attachment.kind) return
        if (mMediaPlayer == null) {
            mMediaPlayer = MediaPlayer()
            mMediaPlayer!!.setAudioStreamType(AudioManager.STREAM_MUSIC)
            mMediaPlayer!!.setOnCompletionListener { mBus.post(AudioPlayEvent.end(attachment)) }
        }
        if (mMediaPlayer!!.isPlaying) {
            mMediaPlayer!!.stop()
            mBus.post(AudioPlayEvent.end(attachment))
        }
        TaskStarter.execute(object : AbstractTask<Any, Any, Any>() {
            public override fun doLongOperation(o: Any): Any? {
                var sink: BufferedSink? = null
                var tempFile: File? = null
                try {
                    tempFile = File.createTempFile("voice_dl" + System.currentTimeMillis(), "m4a")
                    if (tempFile!!.length() > 0) {
                        mMediaPlayer!!.setDataSource(tempFile.absolutePath)
                    } else {
                        val client = OkHttpClient()
                        val response = client.newCall(Request.Builder().url(attachment.file.url).build()).execute()
                        sink = Okio.buffer(Okio.sink(tempFile))
                        sink!!.writeAll(response.body().source())
                        sink.flush()
                        mMediaPlayer!!.setDataSource(context, Uri.parse(attachment.file.url))
                    }
                    mMediaPlayer!!.prepare()
                } catch (e: Exception) {
                    Log.w(Constants.LOGTAG, e)
                    if (tempFile != null) {
                        tempFile.delete()
                    }
                } catch (t: Throwable) {
                    Log.wtf(Constants.LOGTAG, t)
                } finally {
                    Utils.closeSilently(sink)
                }
                return null
            }

            public override fun afterExecute(o: Any?) {
                if (mMediaPlayer == null) return
                mMediaPlayer!!.start()
                mBus.post(AudioPlayEvent.start(attachment))
            }
        })
    }

    override fun isReachingEnd(): Boolean {
        return layoutManager.findLastCompletelyVisibleItemPosition() >= layoutManager.itemCount - 1
    }

    override fun isReachingStart(): Boolean {
        return layoutManager.findFirstCompletelyVisibleItemPosition() <= 0
    }

    private fun onStateClicked(message: Message?) {
        if (MessageState.FAILED == message?.state) {
            // TODO resend message
        }
    }

    class ChatAdapter internal constructor(private val mActivity: ChatListFragment) : LoadMoreSupportAdapter<RecyclerView.ViewHolder>(mActivity.context) {
        private val mInflater: LayoutInflater
        var data: List<Message>? = null
            set(value) {
                field = value
                notifyDataSetChanged()
            }

        init {
            mInflater = LayoutInflater.from(mActivity.context)
        }


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val isOutgoing = viewType and FLAG_MESSAGE_OUTGOING != 0
            val baseView: View
            if (isOutgoing) {
                baseView = mInflater.inflate(R.layout.list_item_message_outgoing, parent, false)
            } else {
                baseView = mInflater.inflate(R.layout.list_item_message_incoming, parent, false)
            }
            val subType = viewType and FLAG_MESSAGE_OUTGOING.inv()
            when (subType) {
                VIEW_SUBTYPE_MESSAGE_TEXT -> {
                    return MessageViewHolder(baseView, isOutgoing, this)
                }
                VIEW_SUBTYPE_MESSAGE_LOCATION -> {
                    val attachmentContainer = baseView.findViewById(R.id.attachment_view) as ViewGroup
                    attachmentContainer.visibility = View.VISIBLE
                    View.inflate(attachmentContainer.context, R.layout.layout_message_attachment_location, attachmentContainer)
                    return LocationChatViewHolder(baseView, isOutgoing, this)
                }
                VIEW_SUBTYPE_MESSAGE_IMAGE -> {
                    val attachmentContainer = baseView.findViewById(R.id.attachment_view) as ViewGroup
                    attachmentContainer.visibility = View.VISIBLE
                    View.inflate(attachmentContainer.context, R.layout.layout_message_attachment_image, attachmentContainer)
                    return ImageChatViewHolder(baseView, isOutgoing, this)
                }
                VIEW_SUBTYPE_MESSAGE_AUDIO -> {
                    val attachmentContainer = baseView.findViewById(R.id.attachment_view) as ViewGroup
                    attachmentContainer.visibility = View.VISIBLE
                    View.inflate(attachmentContainer.context, R.layout.layout_message_attachment_audio, attachmentContainer)
                    return AudioChatViewHolder(baseView, isOutgoing, this)
                }
            }
            throw UnsupportedOperationException()
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            (holder as MessageViewHolder).displayMessage(data!![position])
        }

        override fun getItemViewType(position: Int): Int {
            val message = data!![position]
            val subType = getItemViewSubType(message.mediaType)
            if (message.isOutgoing) return subType or FLAG_MESSAGE_OUTGOING
            return subType
        }

        private fun getItemViewSubType(mediaType: String): Int {
            if (Message.MediaType.LOCATION == mediaType)
                return VIEW_SUBTYPE_MESSAGE_LOCATION
            else if (Message.MediaType.IMAGE == mediaType)
                return VIEW_SUBTYPE_MESSAGE_IMAGE
            else if (Message.MediaType.AUDIO == mediaType) return VIEW_SUBTYPE_MESSAGE_AUDIO
            return VIEW_SUBTYPE_MESSAGE_TEXT
        }

        override fun getItemCount(): Int {
            return messagesCount
        }

        fun getMessage(position: Int): Message? {
            if (data == null) return null
            return data!![position]
        }

        private fun playAudio(attachment: FileAttachment) {
            mActivity.playAudio(attachment)
        }

        fun findPosition(id: String): Int {
            if (data == null) return RecyclerView.NO_POSITION
            var i = 0
            val j = messagesCount
            while (i < j) {
                if (id == getMessage(i)!!.id) return i
                i++
            }
            return RecyclerView.NO_POSITION
        }

        private val messagesCount: Int
            get() {
                if (data == null) return 0
                return data!!.size
            }

        private fun notifyStateClicked(position: Int) {
            mActivity.onStateClicked(getMessage(position))
        }

        private open class MessageViewHolder(
                itemView: View,
                outgoing: Boolean,
                protected val adapter: ChatAdapter
        ) : RecyclerView.ViewHolder(itemView) {

            private val profileImageView: ImageView?
            private val text1: TextView
            private val stateView: ImageView?

            init {
                text1 = itemView.findViewById(android.R.id.text1) as TextView
                stateView = itemView.findViewById(R.id.state) as ImageView
                profileImageView = itemView.findViewById(R.id.profile_image) as ImageView
                stateView.setOnClickListener { adapter.notifyStateClicked(layoutPosition) }
            }

            open fun displayMessage(message: Message) {
                text1.text = message.textContent
                text1.visibility = if (text1.length() > 0) View.VISIBLE else View.GONE
                if (profileImageView != null) {
                    adapter.imageLoader.displayProfileImage(message.sender.avatarUrl,
                            profileImageView)
                }
                if (stateView != null) {
                    val state = message.state
                    when (Utils.emptyIfNull(state)) {
                        MessageState.READ -> {
                            stateView.setImageDrawable(null)
                        }
                        MessageState.FAILED -> {
                            stateView.setImageResource(R.drawable.ic_message_state_retry)
                        }
                        MessageState.UNREAD -> {
                            stateView.setImageResource(R.drawable.ic_message_state_unread)
                        }
                        else -> {
                            stateView.setImageDrawable(null)
                        }
                    }
                }
            }
        }

        private class LocationChatViewHolder(itemView: View, outgoing: Boolean, adapter: ChatAdapter) : MessageViewHolder(itemView, outgoing, adapter) {
            private val mapView: StaticMapView

            init {
                mapView = itemView.findViewById(R.id.map_view) as StaticMapView
                mapView.setProvider(StaticMapUrlGenerator.AMapProvider(Constants.AMAP_WEB_API_KEY))
                mapView.setScaleToDensity(true)
            }

            override fun displayMessage(message: Message) {
                super.displayMessage(message)
                val location = Location("")
                location.latitude = message.latitude
                location.longitude = message.longitude
                mapView.display(location, 12)
            }
        }

        private class ImageChatViewHolder(itemView: View,
                                          outgoing: Boolean, adapter: ChatAdapter) : MessageViewHolder(itemView, outgoing, adapter) {
            private val imageView: MediaSizeImageView

            init {
                imageView = itemView.findViewById(R.id.image_view) as MediaSizeImageView
            }

            override fun displayMessage(message: Message) {
                super.displayMessage(message)
                val url: String
                val metadata: FileAttachment.ImageMetadata?
                val localMetadata = message.localMetadata
                val attachments = message.attachments
                if (localMetadata != null && !localMetadata.isEmpty()) {
                    url = Message.LocalMetadata.get(localMetadata, "image")
                    metadata = JsonSerializer.parse(Message.LocalMetadata.get(localMetadata, "metadata"), FileAttachment.ImageMetadata::class.java)
                } else if (attachments != null && !attachments.isEmpty()) {
                    val attachment = attachments[0] as FileAttachment
                    url = attachment.file.url
                    metadata = JsonSerializer.parse(attachment.metadata, FileAttachment.ImageMetadata::class.java)
                } else {
                    return
                }
                if (metadata != null) {
                    imageView.setMediaSize(metadata.width, metadata.height)
                    val imageLoader = adapter.imageLoader
                    imageLoader.displayImage(url, imageView)
                }
            }
        }

        private class AudioChatViewHolder(
                itemView: View,
                outgoing: Boolean,
                adapter: ChatAdapter
        ) : MessageViewHolder(itemView, outgoing, adapter), View.OnClickListener {

            private val playPauseView: TextView
            private val audioLengthView: TextView
            private val sampleView: AudioSampleView

            init {
                playPauseView = itemView.findViewById(R.id.play_pause) as TextView
                audioLengthView = itemView.findViewById(R.id.audio_length) as TextView
                sampleView = itemView.findViewById(R.id.audio_sample) as AudioSampleView

                playPauseView.setOnClickListener(this)
            }

            override fun displayMessage(message: Message) {
                super.displayMessage(message)
                val metadata = getAudioMetadata(message)
                if (metadata != null) {
                    audioLengthView.text = String.format(Locale.ROOT, "%.1f", metadata.duration)
                    sampleView.setSamples(metadata.samples)
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
                val message = adapter.getMessage(layoutPosition)
                val attachments = message!!.attachments
                if (attachments == null || attachments.isEmpty()) return
                adapter.playAudio(attachments[0] as FileAttachment)
            }
        }

        companion object {

            private val FLAG_MESSAGE_OUTGOING = 0xF0000000.toInt()
            private val VIEW_SUBTYPE_MESSAGE_TEXT = 0x0001
            private val VIEW_SUBTYPE_MESSAGE_LOCATION = 0x0002
            private val VIEW_SUBTYPE_MESSAGE_IMAGE = 0x0003
            private val VIEW_SUBTYPE_MESSAGE_AUDIO = 0x0004
        }


    }
}
