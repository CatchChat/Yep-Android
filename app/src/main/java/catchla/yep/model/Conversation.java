package catchla.yep.model;

import android.database.Cursor;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.util.Date;

import catchla.yep.model.util.YepTimestampDateConverter;
import catchla.yep.provider.YepDataStore.Conversations;
import catchla.yep.util.JsonSerializer;

/**
 * Created by mariotaku on 15/5/29.
 */
@JsonObject
public class Conversation {

    /**
     * Corresponding to {@link Message#getSender()}
     */
    @JsonField(name = "user")
    private User user;

    @JsonField(name = "circle")
    private Circle circle;

    /**
     * Corresponding to {@link Message#getRecipientType()}
     */
    @JsonField(name = "recipient_type")
    private String recipientType;

    /**
     * Corresponding to {@link Message#getTextContent()}
     */
    @JsonField(name = "text_content")
    private String textContent;

    @JsonField(name = "id")
    private String id;

    @JsonField(name = "created_at", typeConverter = YepTimestampDateConverter.class)
    private Date createdAt;

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

    public void setTextContent(final String textContent) {
        this.textContent = textContent;
    }

    public void setCreatedAt(final Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public static Conversation fromUser(User user) {
        final Conversation conversation = new Conversation();
        conversation.setId(user.getId());
        conversation.setRecipientType(Message.RecipientType.USER);
        return conversation;
    }

    public static final class Indices extends ObjectCursor.CursorIndices<Conversation> {

        private final int conversation_id, circle, user, text_content;

        public Indices(final Cursor cursor) {
            super(cursor);
            conversation_id = cursor.getColumnIndex(Conversations.CONVERSATION_ID);
            circle = cursor.getColumnIndex(Conversations.CIRCLE);
            user = cursor.getColumnIndex(Conversations.USER);
            text_content = cursor.getColumnIndex(Conversations.TEXT_CONTENT);
        }

        @Override
        public Conversation newObject(final Cursor cursor) {
            final Conversation conversation = new Conversation();
            conversation.setId(cursor.getString(conversation_id));
            conversation.setTextContent(cursor.getString(text_content));
            conversation.setCircle(JsonSerializer.parse(cursor.getString(circle), Circle.class));
            conversation.setUser(JsonSerializer.parse(cursor.getString(user), User.class));
            return conversation;
        }
    }
}
