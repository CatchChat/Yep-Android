package catchla.yep.model;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.hannesdorfmann.parcelableplease.annotation.ParcelablePlease;
import com.hannesdorfmann.parcelableplease.annotation.ParcelableThisPlease;

import java.util.Date;
import java.util.Locale;

import catchla.yep.model.util.YepTimestampDateConverter;
import catchla.yep.provider.YepDataStore.Conversations;
import catchla.yep.util.JsonSerializer;

/**
 * Created by mariotaku on 15/5/29.
 */
@ParcelablePlease
@JsonObject
public class Conversation implements Parcelable {

    public static final Creator<Conversation> CREATOR = new Creator<Conversation>() {
        @Override
        public Conversation createFromParcel(Parcel in) {
            return new Conversation(in);
        }

        @Override
        public Conversation[] newArray(int size) {
            return new Conversation[size];
        }
    };
    @ParcelableThisPlease
    @JsonField(name = "media_type")
    String mediaType;
    /**
     * Corresponding to {@link Message#getSender()}
     */
    @ParcelableThisPlease
    @JsonField(name = "user")
    User user;
    @ParcelableThisPlease
    @JsonField(name = "account_id")
    String accountId;
    @ParcelableThisPlease
    @JsonField(name = "sender")
    User sender;
    @ParcelableThisPlease
    @JsonField(name = "circle")
    Circle circle;
    /**
     * Corresponding to {@link Message#getRecipientType()}
     */
    @ParcelableThisPlease
    @JsonField(name = "recipient_type")
    String recipientType;
    /**
     * Corresponding to {@link Message#getTextContent()}
     */
    @ParcelableThisPlease
    @JsonField(name = "text_content")
    String textContent;
    @ParcelableThisPlease
    @JsonField(name = "id")
    String id;
    @ParcelableThisPlease
    @JsonField(name = "created_at", typeConverter = YepTimestampDateConverter.class)
    Date updatedAt;

    protected Conversation(Parcel in) {
        ConversationParcelablePlease.readFromParcel(this, in);
    }

    public Conversation() {

    }

    public static Conversation fromUser(User user, String accountId) {
        final Conversation conversation = new Conversation();
        conversation.setAccountId(accountId);
        conversation.setId(generateId(Message.RecipientType.USER, user.getId()));
        conversation.setRecipientType(Message.RecipientType.USER);
        conversation.setUser(user);
        return conversation;
    }

    public static String generateId(Message message) {
        final String recipientType = message.getRecipientType();
        if (Message.RecipientType.CIRCLE.equalsIgnoreCase(recipientType)) {
            return generateId(recipientType, message.getCircle().getId());
        } else if (Message.RecipientType.USER.equalsIgnoreCase(recipientType)) {
            return generateId(recipientType, message.getSender().getId());
        }
        throw new UnsupportedOperationException();
    }

    static String generateId(final String recipientType, final String id) {
        return recipientType.toLowerCase(Locale.US) + ":" + id;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(final User sender) {
        this.sender = sender;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(final String mediaType) {
        this.mediaType = mediaType;
    }

    public User getUser() {
        return user;
    }

    public void setUser(final User user) {
        this.user = user;
    }

    public String getRecipientType() {
        return recipientType;
    }

    public void setRecipientType(final String recipientType) {
        this.recipientType = recipientType;
    }

    public String getTextContent() {
        return textContent;
    }

    public void setTextContent(final String textContent) {
        this.textContent = textContent;
    }

    public Circle getCircle() {
        return circle;
    }

    public void setCircle(final Circle circle) {
        this.circle = circle;
    }

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getRecipientId() {
        if (Message.RecipientType.CIRCLE.equalsIgnoreCase(recipientType)) {
            return circle.getId();
        } else if (Message.RecipientType.USER.equalsIgnoreCase(recipientType)) {
            return user.getId();
        }
        throw new UnsupportedOperationException();
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(final Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(final String accountId) {
        this.accountId = accountId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        ConversationParcelablePlease.writeToParcel(this, dest, flags);
    }

    public static final class Indices extends ObjectCursor.CursorIndices<Conversation> {

        final int account_id, conversation_id, circle, user, text_content, recipient_type, updated_at,
                media_type;

        public Indices(final Cursor cursor) {
            super(cursor);
            account_id = cursor.getColumnIndex(Conversations.ACCOUNT_ID);
            conversation_id = cursor.getColumnIndex(Conversations.CONVERSATION_ID);
            circle = cursor.getColumnIndex(Conversations.CIRCLE);
            user = cursor.getColumnIndex(Conversations.USER);
            text_content = cursor.getColumnIndex(Conversations.TEXT_CONTENT);
            recipient_type = cursor.getColumnIndex(Conversations.RECIPIENT_TYPE);
            updated_at = cursor.getColumnIndex(Conversations.UPDATED_AT);
            media_type = cursor.getColumnIndex(Conversations.MEDIA_TYPE);
        }

        @Override
        public Conversation newObject(final Cursor cursor) {
            final Conversation conversation = new Conversation();
            conversation.setId(cursor.getString(conversation_id));
            conversation.setAccountId(cursor.getString(account_id));
            conversation.setTextContent(cursor.getString(text_content));
            conversation.setCircle(JsonSerializer.parse(cursor.getString(circle), Circle.class));
            conversation.setUser(JsonSerializer.parse(cursor.getString(user), User.class));
            conversation.setRecipientType(cursor.getString(recipient_type));
            conversation.setUpdatedAt(new Date(cursor.getLong(updated_at)));
            conversation.setMediaType(cursor.getString(media_type));
            return conversation;
        }
    }
}
