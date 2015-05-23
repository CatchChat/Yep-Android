package catchla.yep.model;

import org.mariotaku.simplerestapi.http.SimpleValueMap;

/**
 * Created by mariotaku on 15/5/23.
 */
public class ProfileUpdate extends SimpleValueMap {

    public void setNickname(String nickname) {
        put("nickname", nickname);
    }

    public void setAvatarUrl(String avatarUrl) {
        put("avatar_url", avatarUrl);
    }

    public void setUsername(String username) {
        put("username", username);
    }

}
