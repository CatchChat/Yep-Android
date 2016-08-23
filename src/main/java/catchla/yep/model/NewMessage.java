package catchla.yep.model;

import android.content.ContentValues;
import android.support.annotation.NonNull;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import org.mariotaku.library.objectcursor.annotation.CursorField;
import org.mariotaku.library.objectcursor.annotation.CursorObject;

import java.util.Date;

import catchla.yep.model.iface.JsonBody;
import catchla.yep.model.util.LoganSquareCursorFieldConverter;
import catchla.yep.provider.YepDataStore.Messages;

/**
 * Created by mariotaku on 15/6/12.
 */
@JsonObject
@CursorObject(valuesCreator = true, cursorIndices = false)
public class NewMessage implements JsonBody {

    @CursorField(Messages.ACCOUNT_ID)
    String accountId;
    @CursorField(Messages.CONVERSATION_ID)
    String conversationId;
    @CursorField(Messages.CREATED_AT)
    long createdAt;
    @CursorField(value = Messages.CIRCLE, converter = LoganSquareCursorFieldConverter.class)
    Circle circle;
    @CursorField(value = Messages.SENDER, converter = LoganSquareCursorFieldConverter.class)
    User sender;

    User user;
    @CursorField(value = Messages.LOCAL_METADATA, converter = LoganSquareCursorFieldConverter.class)
    Message.LocalMetadata[] localMetadata;

    @CursorField(Messages.RECIPIENT_ID)
    String recipientId;
    @CursorField(Messages.RECIPIENT_TYPE)
    String recipientType;
    @JsonField(name = "parent_id")
    @CursorField(Messages.PARENT_ID)
    String parentId;
    @JsonField(name = "latitude")
    @CursorField(Messages.LATITUDE)
    double latitude;
    @JsonField(name = "longitude")
    @CursorField(Messages.LONGITUDE)
    double longitude;
    @JsonField(name = "text_content")
    @CursorField(Messages.TEXT_CONTENT)
    String textContent;
    @JsonField(name = "media_type")
    @CursorField(Messages.MEDIA_TYPE)
    String mediaType;
    @JsonField(name = "attachment_id")
    String attachmentId;
    @JsonField(name = "random_id")
    @CursorField(Messages.RANDOM_ID)
    String randomId;

    public NewMessage recipientId(String recipientId) {
        this.recipientId = recipientId;
        return this;
    }

    public NewMessage randomId(String randomId) {
        this.randomId = randomId;
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

    public NewMessage user(User user) {
        this.user = user;
        return this;
    }

    public User user() {
        return user;
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

    public void attachmentId(final String attachmentId) {
        if (attachmentId == null) return;
        //noinspection unchecked
        this.attachmentId = attachmentId;
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
        NewMessageValuesCreator.writeTo(this, values);
        values.put(Messages.STATE, Messages.MessageState.SENDING);
        return values;
    }

    public Message.LocalMetadata[] localMetadata() {
        return localMetadata;
    }

    public String getMetadataValue(@NonNull final String key, final String def) {
        if (localMetadata == null) return def;
        return Message.LocalMetadata.get(localMetadata, key, def);
    }

    public String randomId() {
        return randomId;
    }

    public Conversation toConversation() {
        Conversation conversation = new Conversation();
        conversation.setAccountId(accountId());
        conversation.setCircle(circle());
        conversation.setId(conversationId());
        conversation.setUpdatedAt(new Date(createdAt()));
        conversation.setMediaType(mediaType());
        conversation.setRecipientType(recipientType());
        conversation.setSender(sender());
        conversation.setTextContent(textContent());
        conversation.setUser(user());
        return conversation;
    }
}

