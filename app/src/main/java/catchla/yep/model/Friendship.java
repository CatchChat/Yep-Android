package catchla.yep.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

/**
 * Created by mariotaku on 15/5/28.
 */
@JsonObject
public class Friendship {

    @JsonField(name = "id")
    private String id;

    @JsonField(name = "user_id")
    private String userId;

    @JsonField(name = "favored")
    private boolean favored;

    @JsonField(name = "friend")
    private User friend;

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(final String userId) {
        this.userId = userId;
    }

    public boolean isFavored() {
        return favored;
    }

    public void setFavored(final boolean favored) {
        this.favored = favored;
    }

    public User getFriend() {
        return friend;
    }

    public void setFriend(final User friend) {
        this.friend = friend;
    }
}
