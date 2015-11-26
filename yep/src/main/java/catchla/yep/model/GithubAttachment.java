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
public class GithubAttachment extends Attachment implements Parcelable {
    public static final Creator<GithubAttachment> CREATOR = new Creator<GithubAttachment>() {
        @Override
        public GithubAttachment createFromParcel(Parcel in) {
            return new GithubAttachment(in);
        }

        @Override
        public GithubAttachment[] newArray(int size) {
            return new GithubAttachment[size];
        }
    };

    @ParcelableThisPlease
    @JsonField(name = "created_at", typeConverter = YepTimestampDateConverter.class)
    Date createdAt;
    @ParcelableThisPlease
    @JsonField(name = "description")
    String description;
    @ParcelableThisPlease
    @JsonField(name = "full_name")
    String fullName;
    @ParcelableThisPlease
    @JsonField(name = "repo_id")
    long repoId;
    @ParcelableThisPlease
    @JsonField(name = "name")
    String name;
    @ParcelableThisPlease
    @JsonField(name = "url")
    String url;

    public GithubAttachment(final Parcel in) {
        GithubAttachmentParcelablePlease.readFromParcel(this, in);
    }

    public GithubAttachment() {
        super();
    }

    @Override
    public String toString() {
        return "GithubAttachment{" +
                "createdAt=" + createdAt +
                ", description='" + description + '\'' +
                ", fullName='" + fullName + '\'' +
                ", repoId=" + repoId +
                ", name='" + name + '\'' +
                ", url='" + url + '\'' +
                "} " + super.toString();
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public String getDescription() {
        return description;
    }

    public String getFullName() {
        return fullName;
    }

    public long getRepoId() {
        return repoId;
    }

    public String getName() {
        return name;
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
        GithubAttachmentParcelablePlease.writeToParcel(this, dest, flags);
    }
}
