package catchla.yep.model;


import android.os.Parcel;
import android.os.Parcelable;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.hannesdorfmann.parcelableplease.annotation.ParcelablePlease;
import com.hannesdorfmann.parcelableplease.annotation.ParcelableThisPlease;

/**
 * Created by mariotaku on 15/2/4.
 */
@ParcelablePlease
@JsonObject
public class AccessToken implements Parcelable {

    public static final Creator<AccessToken> CREATOR = new Creator<AccessToken>() {
        @Override
        public AccessToken createFromParcel(Parcel in) {
            return new AccessToken(in);
        }

        @Override
        public AccessToken[] newArray(int size) {
            return new AccessToken[size];
        }
    };

    @ParcelableThisPlease
    @JsonField(name = "access_token")
    String accessToken;
    @ParcelableThisPlease
    @JsonField(name = "user")
    User user;

    public AccessToken() {

    }

    public AccessToken(Parcel src) {
        AccessTokenParcelablePlease.readFromParcel(this, src);
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(final String accessToken) {
        this.accessToken = accessToken;
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
        AccessTokenParcelablePlease.writeToParcel(this, dest, flags);
    }
}
