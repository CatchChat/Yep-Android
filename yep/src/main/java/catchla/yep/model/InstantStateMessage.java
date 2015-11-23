package catchla.yep.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.hannesdorfmann.parcelableplease.annotation.ParcelablePlease;
import com.hannesdorfmann.parcelableplease.annotation.ParcelableThisPlease;

/**
 * Created by mariotaku on 15/11/13.
 */
@ParcelablePlease
@JsonObject
public class InstantStateMessage implements Parcelable {
    public static final Creator<InstantStateMessage> CREATOR = new Creator<InstantStateMessage>() {
        @Override
        public InstantStateMessage createFromParcel(Parcel in) {
            return new InstantStateMessage(in);
        }

        @Override
        public InstantStateMessage[] newArray(int size) {
            return new InstantStateMessage[size];
        }
    };
    @ParcelableThisPlease
    @JsonField(name = "state")
    String state;
    @ParcelableThisPlease
    @JsonField(name = "user")
    User user;

    public InstantStateMessage() {

    }

    public InstantStateMessage(Parcel src) {
        InstantStateMessageParcelablePlease.readFromParcel(this, src);
    }

    public String getState() {
        return state;
    }

    public void setState(final String state) {
        this.state = state;
    }

    public User getUser() {
        return user;
    }

    public void setUser(final User user) {
        this.user = user;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        InstantStateMessageParcelablePlease.writeToParcel(this, dest, flags);
    }

    public static InstantStateMessage create(final Conversation conversation, final String state) {
        final InstantStateMessage message = new InstantStateMessage();
        message.setUser(conversation.getUser());
        message.setState(state);
        return message;
    }
}
