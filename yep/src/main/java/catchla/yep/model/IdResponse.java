package catchla.yep.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

/**
 * Created by mariotaku on 15/11/11.
 */
@JsonObject
public class IdResponse {
    @JsonField(name = "id")
    String id;

    public String getId() {
        return id;
    }

}
