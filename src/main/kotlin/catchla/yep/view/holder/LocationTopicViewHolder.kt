package catchla.yep.view.holder

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.location.Location
import android.net.Uri
import android.view.View
import catchla.yep.Constants
import catchla.yep.adapter.TopicsAdapter
import catchla.yep.model.LocationAttachment
import catchla.yep.model.Topic
import catchla.yep.util.ImageLoaderWrapper
import catchla.yep.util.StaticMapUrlGenerator
import catchla.yep.util.Utils
import catchla.yep.util.view.ViewSupport
import kotlinx.android.synthetic.main.layout_topic_attachment_location.view.*
import kotlinx.android.synthetic.main.layout_topic_attachment_location_content.view.*

/**
 * Created by mariotaku on 15/12/9.
 */
class LocationTopicViewHolder(
        topicsAdapter: TopicsAdapter,
        itemView: View,
        context: Context,
        imageLoader: ImageLoaderWrapper,
        listener: TopicsAdapter.TopicClickListener?
) : TopicViewHolder(topicsAdapter, itemView, context, imageLoader, listener) {

    private val attachmentView by lazy { itemView.attachmentView }
    private val mapView by lazy { itemView.mapView }
    private val placeView by lazy { itemView.place }

    init {
        attachmentView.setOnClickListener(this)
        ViewSupport.setClipToOutline(attachmentView, true)
        mapView.setProvider(StaticMapUrlGenerator.AMapProvider(Constants.AMAP_WEB_API_KEY))
        mapView.setScaleToDensity(true)
    }

    override fun displayTopic(topic: Topic) {
        super.displayTopic(topic)
        val attachment = getLocationAttachment(topic)
        placeView.text = attachment.place
        val location = Location("")
        location.latitude = attachment.latitude
        location.longitude = attachment.longitude
        mapView.display(location, 12)
    }

    private fun getLocationAttachment(topic: Topic): LocationAttachment {
        return topic.attachments[0] as LocationAttachment
    }

    override fun onAttachmentClick() {
        val attachment = getLocationAttachment(adapter.getTopic(layoutPosition))
        val geoUri = Uri.parse("geo:" + attachment.latitude + "," + attachment.longitude)
        val context = adapter.context
        try {
            context.startActivity(Intent(Intent.ACTION_VIEW, geoUri))
        } catch (e: ActivityNotFoundException) {
            val fallbackUrl = "https://maps.google.com?q=${attachment.latitude},${attachment.longitude}"
            Utils.openUri(adapter.context as Activity, Uri.parse(fallbackUrl))
        }

    }
}
