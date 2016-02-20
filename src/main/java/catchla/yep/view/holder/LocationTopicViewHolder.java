package catchla.yep.view.holder;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
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
        itemView.findViewById(R.id.attachment_view).setOnClickListener(this);
        mapView = (StaticMapView) itemView.findViewById(R.id.map_view);
        placeView = (TextView) itemView.findViewById(R.id.place);
        mapView.setProvider(new StaticMapUrlGenerator.AMapProvider(Constants.AMAP_WEB_API_KEY));
        mapView.setScaleToDensity(true);
    }

    @Override
    public void displayTopic(final Topic topic) {
        super.displayTopic(topic);
        LocationAttachment attachment = getLocationAttachment(topic);
        placeView.setText(attachment.getPlace());
        final Location location = new Location("");
        location.setLatitude(attachment.getLatitude());
        location.setLongitude(attachment.getLongitude());
        mapView.display(location, 12);
    }

    private LocationAttachment getLocationAttachment(final Topic topic) {
        return (LocationAttachment) topic.getAttachments().get(0);
    }

    @Override
    protected void onAttachmentClick() {
        final LocationAttachment attachment = getLocationAttachment(adapter.getTopic(getLayoutPosition()));
        final Uri geoUri = Uri.parse("geo:" + attachment.getLatitude() + "," + attachment.getLongitude());
        final Context context = adapter.getContext();
        try {
            context.startActivity(new Intent(Intent.ACTION_VIEW, geoUri));
        } catch (ActivityNotFoundException e) {
            // Ignore
        }
    }
}
