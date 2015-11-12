package catchla.yep.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.util.Date;

import catchla.yep.model.util.YepTimestampDateConverter;

/**
 * Created by mariotaku on 15/9/22.
 */
@JsonObject
public class MarkAsReadResult {
    @JsonField(name = "last_read_at", typeConverter = YepTimestampDateConverter.class)
    Date lastReadAt;

    public Date getLastReadAt() {
        return lastReadAt;
    }

    public void setLastReadAt(final Date lastReadAt) {
        this.lastReadAt = lastReadAt;
    }
}
