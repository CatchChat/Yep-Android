package catchla.yep.model;

import android.content.ContentValues;
import android.support.annotation.NonNull;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import catchla.yep.model.iface.JsonBody;
import catchla.yep.provider.YepDataStore.Messages;
import catchla.yep.util.JsonSerializer;

/**
 * Created by mariotaku on 15/6/12.
 */
@JsonObject
public class NewMessage implements JsonBody {

    String conversationId;
    long createdAt;
    Circle circle;
    User sender;
    Message.LocalMetadata[] localMetadata;
    String accountId;

    String recipientId;
    String recipientType;
    @JsonField(name = "parent_id")
    String parentId;
    @JsonField(name = "latitude")
    double latitude;
    @JsonField(name = "longitude")
    double longitude;
    @JsonField(name = "text_content")
    String textContent;
    @JsonField(name = "media_type")
    String mediaType;
    @JsonField(name = "attachments")
    NewAttachment attachments;

    public NewMessage recipientId(String recipientId) {
        this.recipientId = recipientId;
        return this;
    }

    public NewMessage recipientType(String recipientType) {
        this.recipientType = recipientType;
        return this;
    }

    public NewMessage mediaType(String mediaType) {
        this.mediaType = mediaType;
        return this;
    }

    public NewMessage textContent(String textContent) {
        this.textContent = textContent;
        return this;
    }

    public NewMessage conversationId(final String conversationId) {
        this.conversationId = conversationId;
        return this;
    }

    public NewMessage createdAt(final long createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public NewMessage parentId(String parentId) {
        this.parentId = parentId;
        return this;
    }

    public NewMessage location(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
        return this;
    }

    public String conversationId() {
        return conversationId;
    }

    public long createdAt() {
        return createdAt;
    }

    public String parentId() {
        return parentId;
    }

    public String recipientId() {
        return recipientId;
    }

    public String recipientType() {
        return recipientType;
    }

    public void circle(final Circle circle) {
        this.circle = circle;
    }

    public Circle circle() {
        return circle;
    }

    public void sender(final User sender) {
        this.sender = sender;
    }

    public User sender() {
        return sender;
    }

    public String textContent() {
        return textContent;
    }

    public <T extends NewAttachment> void attachment(final T attachments) {
        if (attachments == null) return;
        //noinspection unchecked
        this.attachments = attachments;
    }

    public String mediaType() {
        return mediaType;
    }

    public double latitude() {
        return latitude;
    }

    public double longitude() {
        return longitude;
    }

    public NewMessage localMetadata(final Message.LocalMetadata[] localMetadata) {
        this.localMetadata = localMetadata;
        return this;
    }

    public String accountId() {
        return accountId;
    }

    public NewMessage accountId(String accountId) {
        this.accountId = accountId;
        return this;
    }

    public ContentValues toDraftValues() {
        final ContentValues values = new ContentValues();
        values.put(Messages.ACCOUNT_ID, accountId);
        values.put(Messages.RECIPIENT_ID, recipientId);
        values.put(Messages.TEXT_CONTENT, textContent);
        values.put(Messages.CREATED_AT, createdAt);
        values.put(Messages.SENDER, JsonSerializer.serialize(sender, User.class));
        values.put(Messages.RECIPIENT_TYPE, recipientType);
        values.put(Messages.CIRCLE, JsonSerializer.serialize(circle, Circle.class));
        values.put(Messages.PARENT_ID, parentId);
        values.put(Messages.CONVERSATION_ID, conversationId);
        values.put(Messages.STATE, Messages.MessageState.SENDING);
        values.put(Messages.OUTGOING, true);
        values.put(Messages.LATITUDE, latitude);
        values.put(Messages.LONGITUDE, longitude);
        values.put(Messages.MEDIA_TYPE, mediaType);
        values.put(Messages.LOCAL_METADATA, JsonSerializer.serializeArray(localMetadata, Message.LocalMetadata.class));
        return values;
    }

    public Message.LocalMetadata[] localMetadata() {
        return localMetadata;
    }

    public String getMetadataValue(@NonNull final String key, final String def) {
        if (localMetadata == null) return def;
        return Message.LocalMetadata.get(localMetadata, key, def);
    }

}

