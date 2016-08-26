package catchla.yep.fragment

import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import catchla.yep.Constants
import catchla.yep.R
import catchla.yep.model.*
import catchla.yep.util.StaticMapUrlGenerator
import catchla.yep.util.YepAPI
import kotlinx.android.synthetic.main.layout_topic_attachment_location_content.*

/**
 * Created by mariotaku on 16/1/6.
 */
class NewTopicLocationFragment : NewTopicMediaFragment() {

    override fun hasMedia(): Boolean {
        return attachment != null
    }

    override fun saveDraft(topicDraft: TopicDraft): Boolean {
        return false
    }

    @Throws(YepException::class)
    override fun uploadMedia(yep: YepAPI, newTopic: NewTopic) {
        newTopic.kind(Topic.Kind.LOCATION)
        newTopic.attachments(attachment)
    }

    val attachment: LocationAttachment?
        get() = arguments.getParcelable<LocationAttachment>(Constants.EXTRA_ATTACHMENT)


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mapView.setProvider(StaticMapUrlGenerator.AMapProvider(Constants.AMAP_WEB_API_KEY))
        mapView.setScaleToDensity(true)

        attachment?.let {
            val location = Location("")
            location.latitude = it.latitude
            location.longitude = it.longitude
            place.text = it.place
            mapView.display(location, 12)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_new_topic_attachment_location, container, false)
    }

}
