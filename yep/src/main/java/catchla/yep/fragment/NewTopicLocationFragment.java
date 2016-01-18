package catchla.yep.fragment;

import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import catchla.yep.R;
import catchla.yep.model.LocationAttachment;
import catchla.yep.model.NewTopic;
import catchla.yep.model.Topic;
import catchla.yep.model.YepException;
import catchla.yep.util.StaticMapUrlGenerator;
import catchla.yep.util.YepAPI;
import catchla.yep.view.StaticMapView;

/**
 * Created by mariotaku on 16/1/6.
 */
public class NewTopicLocationFragment extends NewTopicMediaFragment {
    private StaticMapView mMapView;

    @Override
    public boolean hasMedia() {
        return getAttachment() != null;
    }

    @Override
    public boolean saveDraft() {
        return false;
    }

    @Override
    public void uploadMedia(final YepAPI yep, final NewTopic newTopic) throws YepException {
        newTopic.kind(Topic.Kind.LOCATION);
        newTopic.attachments(getAttachment());
    }

    public LocationAttachment getAttachment() {
        return getArguments().getParcelable(EXTRA_ATTACHMENT);
    }

    @Override
    public void onBaseViewCreated(final View view, final Bundle savedInstanceState) {
        super.onBaseViewCreated(view, savedInstanceState);
        mMapView = ((StaticMapView) view.findViewById(R.id.map_view));
    }

    @Override
    public void onActivityCreated(@Nullable final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mMapView.setProvider(new StaticMapUrlGenerator.OpenStreetMapProvider(StaticMapUrlGenerator.OpenStreetMapProvider.MapType.MAPNIK));
        mMapView.setScaleToDensity(true);

        LocationAttachment attachment = getAttachment();

        if (attachment != null) {
            final Location location = new Location("");
            location.setLatitude(attachment.getLatitude());
            location.setLongitude(attachment.getLongitude());
            mMapView.display(location, 12);
        }
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_new_topic_attachment_location, container, false);
    }

    @Override
    public void clearDraft() {

    }
}
