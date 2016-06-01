package catchla.yep.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import org.mariotaku.okfaye.Extension;

/**
 * Created by mariotaku on 16/6/1.
 */
@JsonObject
public class YepFayeExtension extends Extension {
    @JsonField(name = "version")
    String version;
    @JsonField(name = "access_token")
    String accessToken;

    public void setVersion(final String version) {
        this.version = version;
    }

    public void setAccessToken(final String accessToken) {
        this.accessToken = accessToken;
    }
}
