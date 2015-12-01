package catchla.yep.model;

import android.net.Uri;
import android.support.v4.util.ArrayMap;

/**
 * Created by mariotaku on 15/5/23.
 */
public class ProfileUpdate extends ArrayMap<String, String> {

    private Uri avatarUri;

    public void setNickname(String nickname) {
        put("nickname", nickname);
    }

    public void setAvatarUrl(String avatarUrl) {
        put("avatar_url", avatarUrl);
    }

    public Uri getAvatarUri() {
        return avatarUri;
    }

    public void setAvatarUri(Uri avatarUri) {
        this.avatarUri = avatarUri;
    }

    public void setUsername(String username) {
        put("username", username);
    }

    public void setBadge(String username) {
        put("badge", username);
    }

    public void setIntroduction(final String introduction) {
        put("introduction", introduction);
    }
}
