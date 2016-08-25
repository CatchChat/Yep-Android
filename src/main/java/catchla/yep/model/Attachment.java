package catchla.yep.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.hannesdorfmann.parcelableplease.annotation.ParcelablePlease;

/**
 * Created by mariotaku on 15/10/14.
 */
@ParcelablePlease
@JsonObject
public class Attachment implements Parcelable {
    public static final Creator<Attachment> CREATOR = new Creator<Attachment>() {
        @Override
        public Attachment createFromParcel(Parcel in) {
            return new Attachment(in);
        }

        @Override
        public Attachment[] newArray(int size) {
            return new Attachment[size];
        }
    };

    protected Attachment(Parcel in) {
        AttachmentParcelablePlease.readFromParcel(this, in);
    }

    public Attachment() {

    }

    @Override
    public String toString() {
        return "Attachment{}";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        AttachmentParcelablePlease.writeToParcel(this, dest, flags);
    }

    public @interface Kind {
        String GITHUB = "github";
        String DRIBBBLE = "dribbble";
        String LOCATION = "location";
        String IMAGE = "image";
    }
}
