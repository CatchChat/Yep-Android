package catchla.yep.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

/**
 * Created by mariotaku on 15/10/10.
 */
@JsonObject
public class UserSettings {
    @JsonField(name = "blocked")
    boolean blocked;
    @JsonField(name = "do_not_disturb")
    boolean doNotDisturb;

    public boolean isBlocked() {
        return blocked;
    }

    public boolean isDoNotDisturb() {
        return doNotDisturb;
    }
}
