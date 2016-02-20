package catchla.yep.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

/**
 * Created by mariotaku on 15/11/11.
 */
@JsonObject
public class AvatarResponse {
    @JsonField(name = "avatar")
    IdResponse avatar;

    public IdResponse getAvatar() {
        return avatar;
    }

}
