package catchla.yep.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.hannesdorfmann.parcelableplease.annotation.ParcelablePlease;
import com.hannesdorfmann.parcelableplease.annotation.ParcelableThisPlease;

/**
 * Created by mariotaku on 15/11/26.
 */
@ParcelablePlease
@JsonObject
public class LocationAttachment extends Attachment implements Parcelable {

    public static final Creator<LocationAttachment> CREATOR = new Creator<LocationAttachment>() {
        @Override
        public LocationAttachment createFromParcel(Parcel in) {
            return new LocationAttachment(in);
        }

        @Override
        public LocationAttachment[] newArray(int size) {
            return new LocationAttachment[size];
        }
    };
    @ParcelableThisPlease
    @JsonField(name = "place")
    String place;
    @ParcelableThisPlease
    @JsonField(name = "latitude")
    double latitude;
    @ParcelableThisPlease
    @JsonField(name = "longitude")
    double longitude;

    public LocationAttachment() {

    }

    protected LocationAttachment(final Parcel in) {
        LocationAttachmentParcelablePlease.readFromParcel(this, in);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        LocationAttachmentParcelablePlease.writeToParcel(this, dest, flags);
    }

    public String getPlace() {
        return place;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setPlace(final String place) {
        this.place = place;
    }

    public void setLatitude(final double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(final double longitude) {
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return "LocationAttachment{" +
                "place='" + place + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                "} " + super.toString();
    }
}
