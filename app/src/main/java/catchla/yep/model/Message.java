package catchla.yep.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.util.Date;

import io.realm.RealmObject;

/**
 * Created by mariotaku on 15/5/12.
 */
@JsonObject
public class Message extends RealmObject {

    @JsonField(name = "id")
    private String id;
    @JsonField(name = "recipient_id")
    private String recipientId;
    @JsonField(name = "text_content")
    private String textContent;
    @JsonField(name = "parent_id")
    private String parentId;
    @JsonField(name = "created_at")
    private Date createdAt;
    @JsonField(name = "sender")
    private User sender;

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public void setTextContent(String textContent) {
        this.textContent = textContent;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
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

    public String getParentId() {
        return parentId;
    }
}
