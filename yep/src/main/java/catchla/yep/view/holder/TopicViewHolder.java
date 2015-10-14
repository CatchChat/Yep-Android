package catchla.yep.view.holder;

import android.location.Location;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import catchla.yep.R;
import catchla.yep.adapter.TopicsAdapter;
import catchla.yep.adapter.iface.ItemClickListener;
import catchla.yep.model.LatLng;
import catchla.yep.model.Topic;
import catchla.yep.model.User;
import catchla.yep.util.ImageLoaderWrapper;
import catchla.yep.util.Utils;
import catchla.yep.view.ShortTimeView;

/**
 * Created by mariotaku on 15/10/12.
 */
public class TopicViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private final ImageView profileImageView;
    private final TopicsAdapter adapter;
    private final TextView nameView;
    private final TextView textView;
    private final ShortTimeView timeView;
    private final TextView distanceView;
    private final TextView messagesCountView;
    private final Location currentLocation, tempLocation;
    private final ItemClickListener listener;

    public TopicViewHolder(final View itemView, final TopicsAdapter adapter, final ItemClickListener listener) {
        super(itemView);
        this.listener = listener;
        itemView.setOnClickListener(this);
        this.adapter = adapter;
        profileImageView = (ImageView) itemView.findViewById(R.id.profile_image);
        nameView = (TextView) itemView.findViewById(R.id.name);
        textView = (TextView) itemView.findViewById(R.id.text);
        timeView = (ShortTimeView) itemView.findViewById(R.id.time);
        distanceView = (TextView) itemView.findViewById(R.id.distance);
        messagesCountView = (TextView) itemView.findViewById(R.id.messages_count);
        currentLocation = Utils.getCachedLocation(adapter.getContext());
        tempLocation = new Location("");
    }

    public void displayTopic(final Topic topic) {
        final User user = topic.getUser();
        final ImageLoaderWrapper imageLoader = adapter.getImageLoader();
        imageLoader.displayProfileImage(user.getAvatarUrl(), profileImageView);
        nameView.setText(Utils.getDisplayName(user));
        textView.setText(topic.getBody());
        timeView.setTime(topic.getCreatedAt().getTime());
        final LatLng userLocation = topic.getUser().getLocation();
        if (currentLocation != null && userLocation != null) {
            distanceView.setVisibility(View.VISIBLE);
            tempLocation.setLatitude(userLocation.getLatitude());
            tempLocation.setLongitude(userLocation.getLongitude());
            distanceView.setText(Utils.getDistanceString(currentLocation.distanceTo(tempLocation)));
        } else {
            distanceView.setVisibility(View.GONE);
        }
        messagesCountView.setText(String.valueOf(topic.getMessageCount()));
    }

    @Override
    public void onClick(final View v) {
        listener.onItemClick(getLayoutPosition(), this);
    }
}
