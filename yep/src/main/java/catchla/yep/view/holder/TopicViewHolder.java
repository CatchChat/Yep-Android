package catchla.yep.view.holder;

import android.content.Context;
import android.location.Location;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import catchla.yep.R;
import catchla.yep.adapter.TopicsAdapter;
import catchla.yep.model.Attachment;
import catchla.yep.model.LatLng;
import catchla.yep.model.Skill;
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
    private final ImageView providerIcon;
    private final ImageLoaderWrapper imageLoader;
    private final TextView nameView;
    private final TextView textView;
    private final ShortTimeView timeView;
    private final TextView distanceView;
    private final TextView messagesCountView;
    private final Location currentLocation, tempLocation;
    private final TopicsAdapter.TopicClickAdapter listener;
    private final TextView skillButton;

    public TopicViewHolder(final View itemView, final Context context,
                           final ImageLoaderWrapper imageLoader,
                           final TopicsAdapter.TopicClickAdapter listener) {
        super(itemView);
        this.imageLoader = imageLoader;
        this.listener = listener;
        if (listener != null) {
            itemView.setOnClickListener(this);
        }
        profileImageView = (ImageView) itemView.findViewById(R.id.profile_image);
        providerIcon = (ImageView) itemView.findViewById(R.id.provider_icon);
        nameView = (TextView) itemView.findViewById(R.id.name);
        textView = (TextView) itemView.findViewById(R.id.text);
        timeView = (ShortTimeView) itemView.findViewById(R.id.time);
        distanceView = (TextView) itemView.findViewById(R.id.distance);
        messagesCountView = (TextView) itemView.findViewById(R.id.messages_count);
        skillButton = (TextView) itemView.findViewById(R.id.skill_button);
        currentLocation = Utils.getCachedLocation(context);
        tempLocation = new Location("");

        profileImageView.setOnClickListener(this);
        nameView.setOnClickListener(this);
        skillButton.setOnClickListener(this);
    }

    public void displayTopic(final Topic topic) {
        final User user = topic.getUser();
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
        final Skill skill = topic.getSkill();
        if (skill != null) {
            skillButton.setText(Utils.getDisplayName(skill));
            skillButton.setVisibility(View.VISIBLE);
        } else {
            skillButton.setText(null);
            skillButton.setVisibility(View.GONE);
        }

        final String attachmentKind = topic.getAttachmentKind();
        if (Attachment.Kind.GITHUB.equals(attachmentKind)) {
            providerIcon.setImageResource(R.drawable.ic_provider_github);
            providerIcon.setColorFilter(R.color.color_github);
        } else if (Attachment.Kind.DRIBBBLE.equals(attachmentKind)) {
            providerIcon.setImageResource(R.drawable.ic_provider_dribbble);
            providerIcon.setColorFilter(R.color.color_dribbble);
        } else {
            providerIcon.setImageDrawable(null);
        }
    }

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.item_content: {
                listener.onItemClick(getLayoutPosition(), this);
                break;
            }
            case R.id.skill_button: {
                listener.onSkillClick(getLayoutPosition(), this);
                break;
            }
            case R.id.profile_image:
            case R.id.name: {
                listener.onUserClick(getLayoutPosition(), this);
            }
        }
    }

    public void setReplyButtonVisible(final boolean visible) {
        messagesCountView.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
    }

}
