package catchla.yep.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.hannesdorfmann.parcelableplease.annotation.ParcelablePlease;

/**
 * Created by mariotaku on 16/8/13.
 */
@ParcelablePlease
@JsonObject
public class WebPageAttachment extends Attachment implements Parcelable {
    @JsonField(name = "description")
    String description;
    @JsonField(name = "image_url")
    String imageUrl;
    @JsonField(name = "site_name")
    String siteName;
    @JsonField(name = "title")
    String title;
    @JsonField(name = "url")
    String url;

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(final String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(final String siteName) {
        this.siteName = siteName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(final String url) {
        this.url = url;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        WebPageAttachmentParcelablePlease.writeToParcel(this, dest, flags);
    }

    @Override
    public String toString() {
        return "WebPageAttachment{" +
                "description='" + description + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", siteName='" + siteName + '\'' +
                ", title='" + title + '\'' +
                ", url='" + url + '\'' +
                "} " + super.toString();
    }

    public static final Creator<WebPageAttachment> CREATOR = new Creator<WebPageAttachment>() {
        public WebPageAttachment createFromParcel(Parcel source) {
            WebPageAttachment target = new WebPageAttachment();
            WebPageAttachmentParcelablePlease.readFromParcel(target, source);
            return target;
        }

        public WebPageAttachment[] newArray(int size) {
            return new WebPageAttachment[size];
        }
    };
}
