package catchla.yep.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.hannesdorfmann.parcelableplease.annotation.ParcelablePlease;

/**
 * Created by mariotaku on 15/11/26.
 */
@ParcelablePlease
@JsonObject
public class AppleMediaAttachment extends Attachment implements Parcelable {

    public static final Creator<AppleMediaAttachment> CREATOR = new Creator<AppleMediaAttachment>() {
        @Override
        public AppleMediaAttachment createFromParcel(Parcel in) {
            return new AppleMediaAttachment(in);
        }

        @Override
        public AppleMediaAttachment[] newArray(int size) {
            return new AppleMediaAttachment[size];
        }
    };
    @JsonField(name = "kind")
    String kind;
    @JsonField(name = "title")
    String title;
    @JsonField(name = "description")
    String description;
    @JsonField(name = "poster")
    String poster;
    @JsonField(name = "media_url")
    String mediaUrl;
    @JsonField(name = "preview_url")
    String previewUrl;
    @JsonField(name = "time_millis")
    String timeMillis;

    public AppleMediaAttachment() {

    }

    protected AppleMediaAttachment(final Parcel in) {
        AppleMediaAttachmentParcelablePlease.readFromParcel(this, in);
    }

    @Override
    public String getKind() {
        return kind;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getPoster() {
        return poster;
    }

    public String getMediaUrl() {
        return mediaUrl;
    }

    public String getPreviewUrl() {
        return previewUrl;
    }

    public String getTimeMillis() {
        return timeMillis;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        AppleMediaAttachmentParcelablePlease.writeToParcel(this, dest, flags);
    }

    @Override
    public String toString() {
        return "AppleMediaAttachment{" +
                "kind='" + kind + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", poster='" + poster + '\'' +
                ", mediaUrl='" + mediaUrl + '\'' +
                ", previewUrl='" + previewUrl + '\'' +
                ", timeMillis='" + timeMillis + '\'' +
                "} " + super.toString();
    }
}
