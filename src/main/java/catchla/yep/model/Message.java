package catchla.yep.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.hannesdorfmann.parcelableplease.annotation.ParcelablePlease;

import org.mariotaku.library.objectcursor.annotation.CursorField;
import org.mariotaku.library.objectcursor.annotation.CursorObject;

import java.util.Date;
import java.util.List;

import catchla.yep.model.util.LoganSquareCursorFieldConverter;
import catchla.yep.model.util.NaNDoubleConverter;
import catchla.yep.model.util.NaNIfNullDoubleConverter;
import catchla.yep.model.util.TimestampToDateConverter;
import catchla.yep.model.util.VariableTypeAttachmentsConverter;
import catchla.yep.model.util.YepTimestampDateConverter;
import catchla.yep.provider.YepDataStore.Messages;

/**
 * Created by mariotaku on 15/5/12.
 */
@ParcelablePlease
@JsonObject
@CursorObject(valuesCreator = true)
public class Message implements Parcelable {

    public static final Creator<Message> CREATOR = new Creator<Message>() {
        public Message createFromParcel(Parcel source) {
            Message target = new Message();
            MessageParcelablePlease.readFromParcel(target, source);
            return target;
        }

        public Message[] newArray(int size) {
            return new Message[size];
        }
    };
    @JsonField(name = "latitude", typeConverter = NaNDoubleConverter.class)
    @CursorField(value = Messages.LATITUDE, converter = NaNIfNullDoubleConverter.class)
    double latitude = Double.NaN;
    @JsonField(name = "longitude", typeConverter = NaNDoubleConverter.class)
    @CursorField(value = Messages.LONGITUDE, converter = NaNIfNullDoubleConverter.class)
    double longitude = Double.NaN;
    @JsonField(name = "id")
    @CursorField(Messages.MESSAGE_ID)
    String id;
    @JsonField(name = "recipient_id")
    @CursorField(Messages.RECIPIENT_ID)
    String recipientId;
    @JsonField(name = "parent_id")
    @CursorField(Messages.PARENT_ID)
    String parentId;
    @JsonField(name = "text_content")
    @CursorField(Messages.TEXT_CONTENT)
    String textContent;
    @JsonField(name = "created_at", typeConverter = YepTimestampDateConverter.class)
    @CursorField(value = Messages.CREATED_AT, converter = TimestampToDateConverter.class)
    Date createdAt;
    @JsonField(name = "sender")
    @CursorField(value = Messages.SENDER, converter = LoganSquareCursorFieldConverter.class)
    User sender;
    @JsonField(name = "recipient_type")
    @CursorField(value = Messages.RECIPIENT_TYPE)
    String recipientType;
    @JsonField(name = "media_type")
    @CursorField(value = Messages.MEDIA_TYPE)
    String mediaType;
    @JsonField(name = "circle")
    @CursorField(value = Messages.CIRCLE, converter = LoganSquareCursorFieldConverter.class)
    Circle circle;
    @JsonField(name = "conversation_id")
    @CursorField(Messages.CONVERSATION_ID)
    String conversationId;
    @JsonField(name = "outgoing")
    @CursorField(Messages.OUTGOING)
    boolean outgoing;
    @JsonField(name = "state")
    @CursorField(Messages.STATE)
    String state;
    @JsonField(name = "attachments", typeConverter = VariableTypeAttachmentsConverter.class)
    @CursorField(value = Messages.ATTACHMENTS, converter = VariableTypeAttachmentsConverter.class)
    List<Attachment> attachments;
    @JsonField(name = "local_metadata")
    @CursorField(value = Messages.ATTACHMENTS, converter = LoganSquareCursorFieldConverter.class)
    List<LocalMetadata> localMetadata;
    @CursorField(value = Messages.ACCOUNT_ID)
    String accountId;

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(final String accountId) {
        this.accountId = accountId;
    }

    public List<LocalMetadata> getLocalMetadata() {
        return localMetadata;
    }

    public void setLocalMetadata(final List<LocalMetadata> localMetadata) {
        this.localMetadata = localMetadata;
    }

    public List<Attachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(final List<Attachment> attachments) {
        this.attachments = attachments;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(final double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(final double latitude) {
        this.latitude = latitude;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(final String mediaType) {
        this.mediaType = mediaType;
    }

    public String getRecipientType() {
        return recipientType;
    }

    public void setRecipientType(final String recipientType) {
        this.recipientType = recipientType;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(String recipientId) {
        this.recipientId = recipientId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTextContent() {
        return textContent;
    }

    public void setTextContent(String textContent) {
        this.textContent = textContent;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public Circle getCircle() {
        return circle;
    }

    public void setCircle(final Circle circle) {
        this.circle = circle;
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(final String conversationId) {
        this.conversationId = conversationId;
    }

    public boolean isOutgoing() {
        return outgoing;
    }

    public void setOutgoing(final boolean outgoing) {
        this.outgoing = outgoing;
    }

    public String getState() {
        return state;
    }

    public void setState(final String state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return "Message{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                ", id='" + id + '\'' +
                ", recipientId='" + recipientId + '\'' +
                ", parentId='" + parentId + '\'' +
                ", textContent='" + textContent + '\'' +
                ", createdAt=" + createdAt +
                ", sender=" + sender +
                ", recipientType='" + recipientType + '\'' +
                ", mediaType='" + mediaType + '\'' +
                ", circle=" + circle +
                ", conversationId='" + conversationId + '\'' +
                ", outgoing=" + outgoing +
                ", state='" + state + '\'' +
                ", attachments=" + attachments +
                ", localMetadata=" + localMetadata +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        MessageParcelablePlease.writeToParcel(this, dest, flags);
    }

    public interface RecipientType {
        String USER = "User";
        String CIRCLE = "Circle";
    }

    public interface MediaType {
        String TEXT = "text";
        String LOCATION = "location";
        String IMAGE = "image";
        String AUDIO = "audio";
    }

    @ParcelablePlease
    @JsonObject
    public static class LocalMetadata implements Parcelable {
        public static final Creator<LocalMetadata> CREATOR = new Creator<LocalMetadata>() {
            public LocalMetadata createFromParcel(Parcel source) {
                LocalMetadata target = new LocalMetadata();
                Message$LocalMetadataParcelablePlease.readFromParcel(target, source);
                return target;
            }

            public LocalMetadata[] newArray(int size) {
                return new LocalMetadata[size];
            }
        };
        @JsonField(name = "name")
        String name;
        @JsonField(name = "value")
        String value;

        public LocalMetadata() {
        }

        public LocalMetadata(final String name, final String value) {
            this.name = name;
            this.value = value;
        }

        public static String get(final NewMessage message, final String key) {
            return get(message.localMetadata(), key, null);
        }

        public static String get(final LocalMetadata[] metadata, final String key, final String def) {
            if (metadata == null) return def;
            for (LocalMetadata item : metadata) {
                if (item.name.equals(key)) return item.value;
            }
            return def;
        }

        public static String get(final List<LocalMetadata> metadata, final String key) {
            if (metadata == null) return null;
            for (LocalMetadata item : metadata) {
                if (item.name.equals(key)) return item.value;
            }
            return null;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            Message$LocalMetadataParcelablePlease.writeToParcel(this, dest, flags);
        }
    }
}
