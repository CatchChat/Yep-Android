package catchla.yep.view.holder

import android.location.Location
import android.view.View
import android.widget.TextView
import catchla.yep.Constants.AMAP_WEB_API_KEY
import catchla.yep.R
import catchla.yep.fragment.ChatListFragment
import catchla.yep.model.Message
import catchla.yep.util.StaticMapUrlGenerator
import catchla.yep.view.StaticMapView
import catchla.yep.view.holder.MessageViewHolder

class LocationChatViewHolder(
        itemView: View,
        outgoing: Boolean,
        adapter: ChatListFragment.ChatAdapter
) : MessageViewHolder(itemView, outgoing, adapter) {

    private val mapView by lazy { itemView.findViewById(R.id.mapView) as StaticMapView }
    private val descriptionView by lazy { itemView.findViewById(R.id.mapDescription) as TextView }

    init {
        mapView.setProvider(StaticMapUrlGenerator.AMapProvider(AMAP_WEB_API_KEY))
        mapView.setScaleToDensity(true)
    }

    override fun displayMessage(message: Message) {
        super.displayMessage(message)

        textContentView.visibility = View.GONE

        descriptionView.text = message.textContent

        val location = Location("")
        location.latitude = message.latitude
        location.longitude = message.longitude
        mapView.display(location, 12)
    }
}