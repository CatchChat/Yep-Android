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
import catchla.yep.Constants
import catchla.yep.R
import catchla.yep.activity.ThemedImagePickerActivity
import catchla.yep.adapter.LoadMoreSupportAdapter
import catchla.yep.annotation.AttachableType
import catchla.yep.extension.Bundle
import catchla.yep.model.*
import catchla.yep.util.JsonSerializer
import catchla.yep.util.YepAPI
import nl.komponents.kovenant.task
import java.io.File
import java.util.*

/**
 * Created by mariotaku on 16/1/3.
 */
class NewTopicGalleryFragment : NewTopicMediaFragment(), Constants {
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
            topicMediaAdapter.addAllMedia(savedInstanceState.getStringArray(EXTRA_ADAPTER_MEDIA))
        } else {
            topicMediaAdapter.addAllMedia(preferences.getStringSet(KEY_TOPIC_DRAFTS_MEDIA, null))
        }
    }

    override fun hasMedia(): Boolean {
        return topicMediaAdapter.itemCount > 0
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState!!.putStringArray(EXTRA_ADAPTER_MEDIA, topicMediaAdapter.media)
    }

    override fun saveDraft(): Boolean {
        val media = topicMediaAdapter.mediaStringSet
        var draftChanged = false
        val editor = preferences.edit()
        if (media != preferences.getStringSet(KEY_TOPIC_DRAFTS_MEDIA, null)) {
            editor.putStringSet(KEY_TOPIC_DRAFTS_MEDIA, media)
            draftChanged = true
        }
        editor.apply()
        return draftChanged
    }

    @WorkerThread
    @Throws(YepException::class)
    override fun uploadMedia(yep: YepAPI, newTopic: NewTopic) {
        val media = topicMediaAdapter.media
        val files = ArrayList<FileAttachment>()
        for (mediaItem in media) {
            val path = Uri.parse(mediaItem).path
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

    override fun clearDraft() {
        val editor = preferences.edit()
        editor.remove(KEY_TOPIC_DRAFTS_MEDIA)
        editor.apply()
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
                    topicMediaAdapter.addMedia(data!!.data.toString())
                }
                return
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun requestPickMedia() {
        startActivityForResult(ThemedImagePickerActivity.withThemed(context).build(), REQUEST_PICK_IMAGE)
    }

    private fun requestRemoveMedia(media: String) {
        val df = RemoveMediaConfirmDialogFragment()
        df.arguments = Bundle {
            putString(EXTRA_MEDIA, media)
        }
        df.show(childFragmentManager, "remove_topic_media_confirm")
    }

    private fun removeMedia(media: String) {
        topicMediaAdapter.removeMedia(media)
        task {
            val uri = Uri.parse(media)
            File(uri.path).delete()
        }
    }

    private class TopicMediaAdapter(private val mFragment: NewTopicGalleryFragment) : LoadMoreSupportAdapter<RecyclerView.ViewHolder>(mFragment.context) {
        private val mInflater: LayoutInflater
        private val mMedia: MutableList<String>

        init {
            mMedia = ArrayList<String>()
            mInflater = LayoutInflater.from(mFragment.context)
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
                    itemHolder.displayMedia(getMedia(position))
                }
            }
        }

        private fun getMedia(position: Int): String {
            return mMedia[position - 1]
        }

        override fun getItemCount(): Int {
            return 1 + mMedia.size
        }

        private fun requestPickMedia() {
            mFragment.requestPickMedia()
        }

        fun addMedia(data: String) {
            mMedia.add(data)
            notifyDataSetChanged()
        }

        val media: Array<String>
            get() = mMedia.toTypedArray()

        fun removeMedia(media: String) {
            mMedia.remove(media)
            notifyDataSetChanged()
        }

        private fun requestRemoveMedia(media: String) {
            mFragment.requestRemoveMedia(media)
        }

        fun addAllMedia(media: Array<String>?) {
            if (media != null) {
                Collections.addAll(mMedia, *media)
            }
            notifyDataSetChanged()
        }

        fun addAllMedia(media: Collection<String>?) {
            if (media != null) {
                mMedia.addAll(media)
            }
            notifyDataSetChanged()
        }

        val mediaStringSet: Set<String>
            get() = HashSet(mMedia)

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
                    val media = arguments.getString(EXTRA_MEDIA)
                    activity.removeMedia(media)
                }
            }
        }
    }

    companion object {

        private val REQUEST_PICK_IMAGE = 102
        private val EXTRA_ADAPTER_MEDIA = "adapter_media"
        private val KEY_TOPIC_DRAFTS_MEDIA = "topic_drafts_media"

        private val EXTRA_MEDIA = "media"
    }
}
