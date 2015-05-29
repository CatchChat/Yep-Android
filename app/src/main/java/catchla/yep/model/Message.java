package catchla.yep.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.util.Date;

import catchla.yep.model.util.ISO8601DateConverter;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by mariotaku on 15/5/12.
 */
@JsonObject
public class Message extends RealmObject {

    @PrimaryKey
    @JsonField(name = "id")
    private String id;
    @JsonField(name = "recipient_id")
    private String recipientId;
    @JsonField(name = "parent_id")
    private String parentId;
    @JsonField(name = "text_content")
    private String textContent;
    @JsonField(name = "created_at", typeConverter = ISO8601DateConverter.class)
    private Date createdAt;
    @JsonField(name = "sender")
    private User sender;
    @JsonField(name = "recipient_type")
    private String recipientType;
    @JsonField(name = "media_type")
    private String mediaType;
    @JsonField(name = "circle")
    private Circle circle;

    private String conversationId;

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

    public interface RecipientType {
        String USER = "User";
        String CIRCLE = "Circle";
    }
}
