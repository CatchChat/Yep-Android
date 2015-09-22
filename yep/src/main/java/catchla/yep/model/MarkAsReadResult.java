package catchla.yep.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

/**
 * Created by mariotaku on 15/9/22.
 */
@JsonObject
public class MarkAsReadResult {
    @JsonField(name = "recipient_id")
    String recipientId;
    @JsonField(name = "recipient_type")
    String recipientType;
    @JsonField(name = "message_ids")
    String[] messageIds;

    public String getRecipientId() {
        return recipientId;
    }

    public String getRecipientType() {
        return recipientType;
    }

    public String[] getMessageIds() {
        return messageIds;
    }
}
