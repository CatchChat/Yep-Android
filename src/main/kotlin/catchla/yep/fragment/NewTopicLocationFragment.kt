package catchla.yep.fragment

import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import catchla.yep.Constants

import catchla.yep.R
import catchla.yep.model.LocationAttachment
import catchla.yep.model.NewTopic
import catchla.yep.model.Topic
import catchla.yep.model.YepException
import catchla.yep.util.StaticMapUrlGenerator
import catchla.yep.util.YepAPI
import catchla.yep.view.StaticMapView

/**
 * Created by mariotaku on 16/1/6.
 */
class NewTopicLocationFragment : NewTopicMediaFragment() {
    private lateinit var mapView: StaticMapView

    override fun hasMedia(): Boolean {
        return attachment != null
    }

    override fun saveDraft(): Boolean {
        return false
    }

    @Throws(YepException::class)
    override fun uploadMedia(yep: YepAPI, newTopic: NewTopic) {
        newTopic.kind(Topic.Kind.LOCATION)
        newTopic.attachments(attachment)
    }

    val attachment: LocationAttachment?
        get() = arguments.getParcelable<LocationAttachment>(Constants.EXTRA_ATTACHMENT)

    override fun onBaseViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onBaseViewCreated(view, savedInstanceState)
        mapView = view.findViewById(R.id.map_view) as StaticMapView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mapView.setProvider(StaticMapUrlGenerator.AMapProvider(Constants.AMAP_WEB_API_KEY))
        mapView.setScaleToDensity(true)

        val attachment = attachment

        if (attachment != null) {
            val location = Location("")
            location.latitude = attachment.latitude
            location.longitude = attachment.longitude
            mapView.display(location, 12)
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_new_topic_attachment_location, container, false)
    }

    override fun clearDraft() {

    }
}
