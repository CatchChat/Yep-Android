package catchla.yep.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

/**
 * Created by mariotaku on 15/11/13.
 */
@JsonObject
public class InstantStateMessage {
    @JsonField(name = "state")
    int state;
    @JsonField(name = "user")
    User user;

    public int getState() {
        return state;
    }

    public void setState(final int state) {
        this.state = state;
    }

    public User getUser() {
        return user;
    }

    public void setUser(final User user) {
        this.user = user;
    }
}
