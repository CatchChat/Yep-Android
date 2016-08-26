package catchla.yep.fragment

import android.app.Activity
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.annotation.WorkerThread
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import catchla.yep.R
import catchla.yep.activity.ThemedImagePickerActivity
import catchla.yep.adapter.LoadMoreSupportAdapter
import catchla.yep.annotation.AttachableType
import catchla.yep.constant.topicDraftKey
import catchla.yep.extension.Bundle
import catchla.yep.extension.set
import catchla.yep.model.*
import catchla.yep.util.JsonSerializer
import catchla.yep.util.YepAPI
import com.bluelinelabs.logansquare.annotation.JsonField
import com.bluelinelabs.logansquare.annotation.JsonObject
import nl.komponents.kovenant.task
import nz.bradcampbell.paperparcel.PaperParcelable
import java.io.File
import java.util.*

/**
 * Created by mariotaku on 16/1/3.
 */
class NewTopicGalleryFragment : NewTopicMediaFragment() {
    private lateinit var mTopicMediaView: RecyclerView
    private lateinit var topicMediaAdapter: TopicMediaAdapter

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val layoutManager = LinearLayoutManager(context)
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL
        mTopicMediaView.layoutManager = layoutManager
        topicMediaAdapter = TopicMediaAdapter(this)
        mTopicMediaView.adapter = topicMediaAdapter

        if (savedInstanceState != null) {
            topicMediaAdapter.addAllMedia(savedInstanceState.getParcelableArrayList<TopicMedia>(EXTRA_ADAPTER_MEDIA))
        } else {
            val draft = preferences[topicDraftKey]
            topicMediaAdapter.addAllMedia(JsonSerializer.parseList(draft?.attachment, TopicMedia::class.java))
        }
    }

    override fun hasMedia(): Boolean {
        return topicMediaAdapter.media.isNotEmpty()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState!!.putParcelableArrayList(EXTRA_ADAPTER_MEDIA, topicMediaAdapter.media)
    }

    override fun saveDraft(topicDraft: TopicDraft): Boolean {
        val media = topicMediaAdapter.media
        topicDraft.kind = Topic.Kind.IMAGE
        topicDraft.attachment = JsonSerializer.serialize(media, TopicMedia::class.java)
        return true
    }

    @WorkerThread
    @Throws(YepException::class)
    override fun uploadMedia(yep: YepAPI, newTopic: NewTopic) {
        val media = topicMediaAdapter.media
        val files = ArrayList<FileAttachment>()
        for (mediaItem in media) {
            val path = Uri.parse(mediaItem.uri).path
            val metadata = FileAttachment.ImageMetadata.getImageMetadata(path)
            val attachmentId = yep.uploadAttachment(AttachmentUpload.create(File(path),
                    metadata.mimeType, AttachableType.TOPIC, JsonSerializer.serialize<FileAttachment.ImageMetadata>(metadata)))
            files.add(attachmentId)
        }
        if (!files.isEmpty()) {
            newTopic.attachments(files)
            newTopic.kind(Topic.Kind.IMAGE)
        } else {
            newTopic.kind(Topic.Kind.TEXT)
        }
    }

    override fun onBaseViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onBaseViewCreated(view, savedInstanceState)
        mTopicMediaView = view.findViewById(R.id.topic_media) as RecyclerView
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_new_topic_gallery, container, false)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_PICK_IMAGE -> {
                if (resultCode == Activity.RESULT_OK) {
                    val media = TopicMedia()
                    media.uri = data!!.dataString
                    topicMediaAdapter.addMedia(media)
                }
                return
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun requestPickMedia() {
        startActivityForResult(ThemedImagePickerActivity.withThemed(context).build(), REQUEST_PICK_IMAGE)
    }

    private fun requestRemoveMedia(media: TopicMedia) {
        val df = RemoveMediaConfirmDialogFragment()
        df.arguments = Bundle {
            this[EXTRA_MEDIA] = media
        }
        df.show(childFragmentManager, "remove_topic_media_confirm")
    }

    private fun removeMedia(media: TopicMedia) {
        topicMediaAdapter.removeMedia(media)
        task {
            val uri = Uri.parse(media.uri)
            File(uri.path).delete()
        }
    }

    @JsonObject
    data class TopicMedia(
            @JsonField(name = kotlin.arrayOf("uri"))
            var uri: String? = null
    ) : PaperParcelable {
        companion object {
            @JvmField val CREATOR = PaperParcelable.Creator(AccessToken::class.java)
        }
    }

    private class TopicMediaAdapter(private val fragment: NewTopicGalleryFragment) : LoadMoreSupportAdapter<RecyclerView.ViewHolder>(fragment.context) {
        private val mInflater: LayoutInflater
        val media: ArrayList<TopicMedia>

        init {
            this.media = ArrayList<TopicMedia>()
            mInflater = LayoutInflater.from(fragment.context)
        }

        override fun getItemViewType(position: Int): Int {
            if (position == 0) return VIEW_TYPE_ADD
            return VIEW_TYPE_ITEM
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            when (viewType) {
                VIEW_TYPE_ITEM -> {
                    val view = mInflater.inflate(R.layout.adapter_item_topic_media_item, parent, false)
                    return TopicMediaItemHolder(view, this)
                }
                VIEW_TYPE_ADD -> {
                    val view = mInflater.inflate(R.layout.adapter_item_topic_media_add, parent, false)
                    return TopicMediaAddHolder(view, this)
                }
            }
            throw UnsupportedOperationException("Unsupported itemType " + viewType)
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            when (getItemViewType(position)) {
                VIEW_TYPE_ITEM -> {
                    val itemHolder = holder as TopicMediaItemHolder
                    itemHolder.displayMedia(getMedia(position).uri!!)
                }
            }
        }

        private fun getMedia(position: Int): TopicMedia {
            return this.media[position - 1]
        }

        override fun getItemCount(): Int {
            return 1 + this.media.size
        }

        private fun requestPickMedia() {
            fragment.requestPickMedia()
        }

        fun addMedia(data: TopicMedia) {
            this.media.add(data)
            notifyDataSetChanged()
        }

        fun removeMedia(media: TopicMedia) {
            this.media.remove(media)
            notifyDataSetChanged()
        }

        private fun requestRemoveMedia(media: TopicMedia) {
            fragment.requestRemoveMedia(media)
        }

        fun addAllMedia(media: Collection<TopicMedia>?) {
            if (media != null) {
                this.media.addAll(media)
            }
            notifyDataSetChanged()
        }

        private inner class TopicMediaItemHolder(itemView: View, private val adapter: TopicMediaAdapter) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
            private val mediaPreviewView: ImageView
            private val mediaRemoveView: ImageView

            init {
                this.mediaPreviewView = itemView.findViewById(R.id.media_preview) as ImageView
                this.mediaRemoveView = itemView.findViewById(R.id.media_remove) as ImageView

                mediaRemoveView.setOnClickListener(this)
            }

            fun displayMedia(media: String) {
                val imageLoader = adapter.imageLoader
                imageLoader.displayImage(media, mediaPreviewView)
            }

            override fun onClick(v: View) {
                when (v.id) {
                    R.id.media_remove -> {
                        adapter.requestRemoveMedia(adapter.getMedia(layoutPosition))
                    }
                }
            }
        }

        private inner class TopicMediaAddHolder(itemView: View, private val adapter: TopicMediaAdapter) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

            init {
                itemView.setOnClickListener(this)
            }

            override fun onClick(v: View) {
                adapter.requestPickMedia()
            }
        }

        companion object {
            private val VIEW_TYPE_ADD = 1
            private val VIEW_TYPE_ITEM = 2
        }
    }

    class RemoveMediaConfirmDialogFragment : DialogFragment(), DialogInterface.OnClickListener {

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            val context = context
            val builder = AlertDialog.Builder(context)
            builder.setMessage(R.string.remove_topic_media_confirm)
            builder.setPositiveButton(R.string.remove, this)
            builder.setNegativeButton(android.R.string.cancel, null)
            return builder.create()
        }

        override fun onClick(dialog: DialogInterface, which: Int) {
            when (which) {
                DialogInterface.BUTTON_POSITIVE -> {
                    val activity = parentFragment as NewTopicGalleryFragment ?: return
                    val media = arguments.getParcelable<TopicMedia>(EXTRA_MEDIA)
                    activity.removeMedia(media)
                }
            }
        }
    }

    companion object {

        private val REQUEST_PICK_IMAGE = 102
        private val EXTRA_ADAPTER_MEDIA = "adapter_media"

        private val EXTRA_MEDIA = "media"
    }
}
