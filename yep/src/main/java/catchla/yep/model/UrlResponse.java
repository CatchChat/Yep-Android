package catchla.yep.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

/**
 * Created by mariotaku on 15/11/11.
 */
@JsonObject
public class UrlResponse {
    @JsonField(name = "url")
    String url;

    public String getUrl() {
        return url;
    }

}
