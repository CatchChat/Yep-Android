package catchla.yep.model;

import android.database.Cursor;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.util.Date;

import catchla.yep.model.util.YepTimestampDateConverter;

/**
 * Created by mariotaku on 15/5/29.
 */
@JsonObject
public class Conversation {

    /**
     * Corresponding to {@link Message#getSender()}
     */
    @JsonField(name = "sender")
    private User sender;

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

    public User getSender() {
        return sender;
    }

    public void setSender(final User sender) {
        this.sender = sender;
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

        public Indices(final Cursor cursor) {
            super(cursor);
        }

        @Override
        public Conversation newObject(final Cursor cursor) {
            final Conversation conversation = new Conversation();
            return conversation;
        }
    }
}
