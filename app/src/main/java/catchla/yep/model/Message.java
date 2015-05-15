package catchla.yep.model;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

import io.realm.RealmObject;

/**
 * Created by mariotaku on 15/5/12.
 */
public class Message extends RealmObject {

    @SerializedName("id")
    private String id;
    @SerializedName("recipient_id")
    private String recipientId;
    @SerializedName("text_content")
    private String textContent;
    @SerializedName("parent_id")
    private String parentId;
    @SerializedName("created_at")
    private Date createdAt;

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
