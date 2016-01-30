package catchla.yep.view.holder;

import android.content.Context;
import android.location.Location;
import android.view.View;
import android.widget.TextView;

import catchla.yep.Constants;
import catchla.yep.R;
import catchla.yep.adapter.TopicsAdapter;
import catchla.yep.model.LocationAttachment;
import catchla.yep.model.Topic;
import catchla.yep.util.ImageLoaderWrapper;
import catchla.yep.util.StaticMapUrlGenerator;
import catchla.yep.view.StaticMapView;

/**
 * Created by mariotaku on 15/12/9.
 */
public class LocationTopicViewHolder extends TopicViewHolder {

    private final StaticMapView mapView;
    private final TextView placeView;

    public LocationTopicViewHolder(final TopicsAdapter topicsAdapter, final View itemView, final Context context,
                                   final ImageLoaderWrapper imageLoader,
                                   final TopicsAdapter.TopicClickListener listener) {
        super(topicsAdapter, itemView, context, imageLoader, listener);
        mapView = (StaticMapView) itemView.findViewById(R.id.map_view);
        placeView = (TextView) itemView.findViewById(R.id.place);
        mapView.setProvider(new StaticMapUrlGenerator.AMapProvider(Constants.AMAP_WEB_API_KEY));
        mapView.setScaleToDensity(true);
    }

    @Override
    public void displayTopic(final Topic topic) {
        super.displayTopic(topic);
        LocationAttachment attachment = (LocationAttachment) topic.getAttachments().get(0);
        placeView.setText(attachment.getPlace());
        final Location location = new Location("");
        location.setLatitude(attachment.getLatitude());
        location.setLongitude(attachment.getLongitude());
        mapView.display(location, 12);
    }
}
