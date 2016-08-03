package catchla.yep.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.hannesdorfmann.parcelableplease.annotation.ParcelablePlease;
import com.hannesdorfmann.parcelableplease.annotation.ParcelableThisPlease;

/**
 * Created by mariotaku on 15/12/27.
 */
@ParcelablePlease
@JsonObject
public class Avatar implements Parcelable {
    @ParcelableThisPlease
    @JsonField(name = "url")
    String url;
    @ParcelableThisPlease
    @JsonField(name = "thumb_url")
    String thumbUrl;

    public String getUrl() {
        return url;
    }

    public String getThumbUrl() {
        return thumbUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        AvatarParcelablePlease.writeToParcel(this, dest, flags);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final Avatar avatar = (Avatar) o;

        if (url != null ? !url.equals(avatar.url) : avatar.url != null) return false;
        return thumbUrl != null ? thumbUrl.equals(avatar.thumbUrl) : avatar.thumbUrl == null;

    }

    @Override
    public int hashCode() {
        int result = url != null ? url.hashCode() : 0;
        result = 31 * result + (thumbUrl != null ? thumbUrl.hashCode() : 0);
        return result;
    }

    public static final Creator<Avatar> CREATOR = new Creator<Avatar>() {
        public Avatar createFromParcel(Parcel source) {
            Avatar target = new Avatar();
            AvatarParcelablePlease.readFromParcel(target, source);
            return target;
        }

        public Avatar[] newArray(int size) {
            return new Avatar[size];
        }
    };
}
