package catchla.yep.view.holder;

import android.location.Location;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import catchla.yep.R;
import catchla.yep.adapter.iface.ItemClickListener;
import catchla.yep.model.LatLng;
import catchla.yep.model.Topic;
import catchla.yep.model.User;
import catchla.yep.util.Utils;
import catchla.yep.view.ShortTimeView;

/**
 * Created by mariotaku on 15/10/12.
 */
public class TopicViewHolder extends RecyclerView.ViewHolder {

    private final ImageView profileImageView;
    private final Fragment fragment;
    private final TextView nameView;
    private final TextView textView;
    private final ShortTimeView timeView;
    private final TextView distanceView;
    private final TextView messagesCountView;
    private final Location currentLocation, tempLocation;

    public TopicViewHolder(final Fragment fragment, final ItemClickListener listener, final View itemView) {
        super(itemView);
        this.fragment = fragment;
        profileImageView = (ImageView) itemView.findViewById(R.id.profile_image);
        nameView = (TextView) itemView.findViewById(R.id.name);
        textView = (TextView) itemView.findViewById(R.id.text);
        timeView = (ShortTimeView) itemView.findViewById(R.id.time);
        distanceView = (TextView) itemView.findViewById(R.id.distance);
        messagesCountView = (TextView) itemView.findViewById(R.id.messages_count);
        currentLocation = Utils.getCachedLocation(fragment.getContext());
        tempLocation = new Location("");
    }

    public void displayTopic(final Topic topic) {
        final User user = topic.getUser();
        Glide.with(fragment).load(user.getAvatarUrl()).placeholder(R.drawable.ic_profile_image_default).into(profileImageView);
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
}
