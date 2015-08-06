package catchla.yep.model;

import android.database.Cursor;
import android.support.annotation.NonNull;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import catchla.yep.provider.YepDataStore.Friendships;
import catchla.yep.util.JsonSerializer;

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

    public static class Indices extends ObjectCursor.CursorIndices<Friendship> {

        public final int user_id, friend_id, nickname, username, avatar_url, introduction,
                learning_skills, master_skills;

        public Indices(@NonNull final Cursor cursor) {
            super(cursor);
            user_id = cursor.getColumnIndex(Friendships.USER_ID);
            friend_id = cursor.getColumnIndex(Friendships.FRIEND_ID);
            nickname = cursor.getColumnIndex(Friendships.NICKNAME);
            username = cursor.getColumnIndex(Friendships.USERNAME);
            avatar_url = cursor.getColumnIndex(Friendships.AVATAR_URL);
            introduction = cursor.getColumnIndex(Friendships.INTRODUCTION);
            learning_skills = cursor.getColumnIndex(Friendships.LEARNING_SKILLS);
            master_skills = cursor.getColumnIndex(Friendships.MASTER_SKILLS);
        }

        @Override
        public Friendship newObject(final Cursor cursor) {
            final Friendship friendship = new Friendship();
            final User friend = new User();
            friendship.setUserId(cursor.getString(user_id));
            friend.setId(cursor.getString(friend_id));
            friend.setNickname(cursor.getString(nickname));
            friend.setUsername(cursor.getString(username));
            friend.setAvatarUrl(cursor.getString(avatar_url));
            friend.setIntroduction(cursor.getString(introduction));
            friend.setLearningSkills(JsonSerializer.parseList(cursor.getString(learning_skills), Skill.class));
            friend.setMasterSkills(JsonSerializer.parseList(cursor.getString(master_skills), Skill.class));
            friendship.setFriend(friend);
            return friendship;
        }
    }
}
