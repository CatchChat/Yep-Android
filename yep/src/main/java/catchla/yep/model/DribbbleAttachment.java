package catchla.yep.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.hannesdorfmann.parcelableplease.annotation.ParcelablePlease;
import com.hannesdorfmann.parcelableplease.annotation.ParcelableThisPlease;

import java.util.Date;

import catchla.yep.model.util.YepTimestampDateConverter;

/**
 * Created by mariotaku on 15/11/26.
 */
@ParcelablePlease
@JsonObject
public class DribbbleAttachment extends Attachment implements Parcelable {
    public static final Creator<DribbbleAttachment> CREATOR = new Creator<DribbbleAttachment>() {
        @Override
        public DribbbleAttachment createFromParcel(Parcel in) {
            return new DribbbleAttachment(in);
        }

        @Override
        public DribbbleAttachment[] newArray(int size) {
            return new DribbbleAttachment[size];
        }
    };

    @ParcelableThisPlease
    @JsonField(name = "created_at", typeConverter = YepTimestampDateConverter.class)
    Date createdAt;
    @ParcelableThisPlease
    @JsonField(name = "description")
    String description;
    @ParcelableThisPlease
    @JsonField(name = "media_url")
    String mediaUrl;
    @ParcelableThisPlease
    @JsonField(name = "shot_id")
    long shotId;
    @ParcelableThisPlease
    @JsonField(name = "title")
    String title;
    @ParcelableThisPlease
    @JsonField(name = "url")
    String url;


    public DribbbleAttachment(final Parcel in) {
        DribbbleAttachmentParcelablePlease.readFromParcel(this, in);
    }

    public DribbbleAttachment() {
        super();
    }

    @Override
    public String toString() {
        return "DribbbleAttachment{" +
                "createdAt=" + createdAt +
                ", description='" + description + '\'' +
                ", mediaUrl='" + mediaUrl + '\'' +
                ", shotId=" + shotId +
                ", title='" + title + '\'' +
                ", url='" + url + '\'' +
                "} " + super.toString();
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public String getDescription() {
        return description;
    }

    public String getMediaUrl() {
        return mediaUrl;
    }

    public long getShotId() {
        return shotId;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        DribbbleAttachmentParcelablePlease.writeToParcel(this, dest, flags);
    }
}
