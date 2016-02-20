package catchla.yep.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.hannesdorfmann.parcelableplease.annotation.ParcelablePlease;
import com.hannesdorfmann.parcelableplease.annotation.ParcelableThisPlease;

/**
 * Created by mariotaku on 15/10/30.
 */
@ParcelablePlease
public class ParcelableMedia implements Parcelable {

    @ParcelableThisPlease
    int type;
    @ParcelableThisPlease
    String url;

    public ParcelableMedia() {

    }

    protected ParcelableMedia(Parcel in) {
        ParcelableMediaParcelablePlease.readFromParcel(this, in);
    }

    public static final Creator<ParcelableMedia> CREATOR = new Creator<ParcelableMedia>() {
        @Override
        public ParcelableMedia createFromParcel(Parcel in) {
            return new ParcelableMedia(in);
        }

        @Override
        public ParcelableMedia[] newArray(int size) {
            return new ParcelableMedia[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        ParcelableMediaParcelablePlease.writeToParcel(this, dest, flags);
    }

    public int getType() {
        return type;
    }

    public String getUrl() {
        return url;
    }
}
