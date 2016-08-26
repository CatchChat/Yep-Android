package catchla.yep.view.holder

import android.view.View
import catchla.yep.R
import catchla.yep.fragment.ChatListFragment
import catchla.yep.model.FileAttachment
import catchla.yep.model.Message
import catchla.yep.util.JsonSerializer
import catchla.yep.view.MediaSizeImageView
import catchla.yep.view.holder.MessageViewHolder

class ImageChatViewHolder(
        itemView: View,
        outgoing: Boolean,
        adapter: ChatListFragment.ChatAdapter
) : MessageViewHolder(itemView, outgoing, adapter) {
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
            val attachment = attachments.first() as FileAttachment
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