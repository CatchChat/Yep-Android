package catchla.yep.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.util.Date;

import catchla.yep.model.util.YepTimestampDateConverter;

/**
 * Created by mariotaku on 15/11/12.
 */
@JsonObject
public class MarkAsReadMessage {
    @JsonField(name = "last_read_at", typeConverter = YepTimestampDateConverter.class)
    Date lastReadAt;
    @JsonField(name = "recipient_type")
    String recipientType;
    @JsonField(name = "recipient_id")
    String recipientId;

    public String getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(final String recipientId) {
        this.recipientId = recipientId;
    }

    public String getRecipientType() {
        return recipientType;
    }

    public void setRecipientType(final String recipientType) {
        this.recipientType = recipientType;
    }

    public Date getLastReadAt() {
        return lastReadAt;
    }

    public void setLastReadAt(final Date lastReadAt) {
        this.lastReadAt = lastReadAt;
    }
}
