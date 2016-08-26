package catchla.yep.fragment

import android.content.Context
import android.os.Bundle
import android.support.v4.app.LoaderManager
import android.support.v4.content.Loader
import android.support.v7.widget.FixedLinearLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import catchla.yep.Constants.EXTRA_TOPIC
import catchla.yep.R
import catchla.yep.adapter.LoadMoreSupportAdapter
import catchla.yep.adapter.iface.ILoadMoreSupportAdapter
import catchla.yep.message.AudioPlayEvent
import catchla.yep.model.FileAttachment
import catchla.yep.model.Message
import catchla.yep.model.Topic
import catchla.yep.provider.YepDataStore.Messages.MessageState
import catchla.yep.util.Utils
import catchla.yep.view.holder.AudioChatViewHolder
import catchla.yep.view.holder.ImageChatViewHolder
import catchla.yep.view.holder.LocationChatViewHolder
import catchla.yep.view.holder.MessageViewHolder
import catchla.yep.view.holder.iface.clickedPlayPause
import catchla.yep.view.iface.IExtendedView
import com.squareup.otto.Subscribe
import kotlinx.android.synthetic.main.fragment_chat_list.*
import kotlinx.android.synthetic.main.layout_content_recyclerview_common.*

/**
 * List component for chat activities
 * Created by mariotaku on 15/11/16.
 */
abstract class ChatListFragment : AbsContentRecyclerViewFragment<ChatListFragment.ChatAdapter,
        LinearLayoutManager>(), LoaderManager.LoaderCallbacks<List<Message>?> {

    var jumpToLast: Boolean = false

    override var refreshing: Boolean
        get() = false
        set(value) {
            super.refreshing = value
        }
    open val eventSubscriber: Any by lazy { EventSubscriber() }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val topic = topic
        if (topic != null) {
            chatTopic.visibility = View.VISIBLE
            val image = topic.attachments.firstOrNull() as? FileAttachment
            if (image != null) {
                topicIcon.scaleType = ImageView.ScaleType.CENTER_CROP
                imageLoader.displayProviderPreviewImage(image.file.url, topicIcon)
            } else {
                topicIcon.scaleType = ImageView.ScaleType.CENTER_INSIDE
                topicIcon.setImageResource(R.drawable.ic_feed_placeholder_text)
            }
            topicTitle.text = Utils.getDisplayName(topic.user)
            topicSummary.text = topic.body
            imageLoader.displayProfileImage(topic.user.avatar?.thumbUrl, topicProfileImage)
            val paddingTopBackup = recyclerView.paddingTop
            chatTopic.onSizeChangedListener = object : IExtendedView.OnSizeChangedListener {

                override fun onSizeChanged(view: View, w: Int, h: Int, oldw: Int, oldh: Int) {
                    recyclerView.setPadding(recyclerView.paddingLeft, paddingTopBackup + h,
                            recyclerView.paddingRight, recyclerView.paddingBottom);
                }
            }
        } else {
            chatTopic.visibility = View.GONE
        }
        refreshEnabled = false
        loadMoreIndicatorPosition = ILoadMoreSupportAdapter.IndicatorPosition.START
        loaderManager.initLoader(0, null, this)
        showProgress()
    }

    override fun onStart() {
        super.onStart()
        bus.register(eventSubscriber)
    }

    override fun onStop() {
        bus.unregister(eventSubscriber)
        super.onStop()
    }

    override fun onScrollToPositionWithOffset(layoutManager: LinearLayoutManager, position: Int, offset: Int) {
        layoutManager.scrollToPositionWithOffset(position, offset)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_chat_list, container, false)
    }


    override fun onCreateAdapter(context: Context): ChatAdapter {
        return ChatAdapter(this)
    }

    override fun setupRecyclerView(context: Context, recyclerView: RecyclerView, layoutManager: LinearLayoutManager) {
        layoutManager.stackFromEnd = false
    }

    override fun onCreateLayoutManager(context: Context): LinearLayoutManager {
        val lm = FixedLinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, true)
        lm.stackFromEnd = true
        return lm
    }

    val topic: Topic?
        get() = arguments.getParcelable<Topic>(EXTRA_TOPIC)

    override fun onLoadFinished(loader: Loader<List<Message>?>, data: List<Message>?) {
        val backupPosition = layoutManager.findFirstVisibleItemPosition()
        val backupMessage: Message?
        val adapter = adapter
        if (backupPosition != RecyclerView.NO_POSITION && !jumpToLast) {
            backupMessage = adapter.getMessage(backupPosition)
        } else {
            backupMessage = null
        }
        jumpToLast = false
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

    override fun onLoaderReset(loader: Loader<List<Message>?>) {
        adapter.data = null
    }

    override val reachingEnd: Boolean
        get() = layoutManager.findLastCompletelyVisibleItemPosition() >= layoutManager.itemCount - 1

    override val reachingStart: Boolean
        get() = layoutManager.findFirstCompletelyVisibleItemPosition() <= 0

    private fun onStateClicked(message: Message?) {
        if (MessageState.FAILED == message?.state) {
            // TODO resend message
        }
    }

    open inner class EventSubscriber {

        @Subscribe
        fun onAudioPlayEvent(event: AudioPlayEvent) {

        }

    }

    class ChatAdapter(private val fragment: ChatListFragment) : LoadMoreSupportAdapter<RecyclerView.ViewHolder>(fragment.context) {
        private val inflater: LayoutInflater
        var data: List<Message>? = null
            set(value) {
                field = value
                notifyDataSetChanged()
            }

        init {
            inflater = LayoutInflater.from(context)
        }


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val isOutgoing = viewType and FLAG_MESSAGE_OUTGOING != 0
            val baseView: View
            if (isOutgoing) {
                baseView = inflater.inflate(R.layout.list_item_message_outgoing, parent, false)
            } else {
                baseView = inflater.inflate(R.layout.list_item_message_incoming, parent, false)
            }
            val subType = viewType and FLAG_MESSAGE_OUTGOING.inv()
            when (subType) {
                VIEW_SUBTYPE_MESSAGE_TEXT -> {
                    return MessageViewHolder(baseView, isOutgoing, this)
                }
                VIEW_SUBTYPE_MESSAGE_LOCATION -> {
                    val attachmentContainer = baseView.findViewById(R.id.attachmentView) as ViewGroup
                    attachmentContainer.visibility = View.VISIBLE
                    View.inflate(attachmentContainer.context, R.layout.layout_message_attachment_location, attachmentContainer)
                    return LocationChatViewHolder(baseView, isOutgoing, this)
                }
                VIEW_SUBTYPE_MESSAGE_IMAGE -> {
                    val attachmentContainer = baseView.findViewById(R.id.attachmentView) as ViewGroup
                    attachmentContainer.visibility = View.VISIBLE
                    View.inflate(attachmentContainer.context, R.layout.layout_message_attachment_image, attachmentContainer)
                    return ImageChatViewHolder(baseView, isOutgoing, this)
                }
                VIEW_SUBTYPE_MESSAGE_AUDIO -> {
                    val attachmentContainer = baseView.findViewById(R.id.attachmentView) as ViewGroup
                    attachmentContainer.visibility = View.VISIBLE
                    View.inflate(attachmentContainer.context, R.layout.layout_message_attachment_audio, attachmentContainer)
                    return AudioChatViewHolder(baseView, isOutgoing, this)
                }
            }
            throw UnsupportedOperationException()
        }

        override fun onViewRecycled(holder: RecyclerView.ViewHolder?) {
            if (holder is MessageViewHolder) {
                holder.onRecycled()
            }
            super.onViewRecycled(holder)
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

        private fun getItemViewSubType(mediaType: String?): Int {
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

        fun playAudio(attachment: FileAttachment) {
            audioPlayer.clickedPlayPause(attachment.file.url)
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

        fun notifyStateClicked(position: Int) {
            fragment.onStateClicked(getMessage(position))
        }

        companion object {

            private val FLAG_MESSAGE_OUTGOING: Int = 0xF0000000.toInt()
            private val VIEW_SUBTYPE_MESSAGE_TEXT: Int = 0x0001
            private val VIEW_SUBTYPE_MESSAGE_LOCATION: Int = 0x0002
            private val VIEW_SUBTYPE_MESSAGE_IMAGE: Int = 0x0003
            private val VIEW_SUBTYPE_MESSAGE_AUDIO: Int = 0x0004
        }


    }

}
