package catchla.yep.model;


import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

/**
 * Created by mariotaku on 15/2/4.
 */
@JsonObject
public class UpdateRegistrationResult {

    @JsonField(name = "access_token")
    private String accessToken;

    @JsonField(name = "user")
    private User user;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(final String accessToken) {
        this.accessToken = accessToken;
    }

    public User getUser() {
        return user;
    }

    public void setUser(final User user) {
        this.user = user;
    }
}
