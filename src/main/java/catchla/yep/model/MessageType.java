package catchla.yep.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.bluelinelabs.logansquare.annotation.OnJsonParseComplete;

import java.io.IOException;

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

    @OnJsonParseComplete
    void checkDataValidity() throws IOException {
        if (messageType == null) throw new IOException("Invalid message type");
    }
}
