package catchla.yep.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

/**
 * Created by mariotaku on 16/3/27.
 */
@JsonObject
public class MessageType {
    @JsonField(name = "message_type")
    String messageType;

    public String getMessageType() {
        return messageType;
    }
}
