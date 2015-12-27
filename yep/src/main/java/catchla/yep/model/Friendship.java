package catchla.yep.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.NonNull;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import org.mariotaku.library.objectcursor.ObjectCursor;
import org.mariotaku.library.objectcursor.annotation.CursorField;
import org.mariotaku.library.objectcursor.annotation.CursorObject;
import org.mariotaku.library.objectcursor.converter.CursorFieldConverter;

import java.lang.reflect.ParameterizedType;

import catchla.yep.provider.YepDataStore.Friendships;
import catchla.yep.util.JsonSerializer;

/**
 * Created by mariotaku on 15/5/28.
 */
@JsonObject
@CursorObject
public class Friendship {

    @JsonField(name = "id")
    @CursorField(Friendships.FRIEND_ID)
    String id;

    @JsonField(name = "user_id")
    @CursorField(Friendships.USER_ID)
    String userId;

    @JsonField(name = "favored")
    boolean favored;

    @JsonField(name = "friend")
    @CursorField(value = "", converter = UserConverter.class)
    User friend;

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


    public static class UserConverter implements CursorFieldConverter<User> {
        @Override
        public User parseField(final Cursor cursor, final int columnIndex, final ParameterizedType fieldType) {
            return UserCursorIndices.fromCursor(cursor);
        }

        @Override
        public void writeField(final ContentValues values, final User object, final String columnName, final ParameterizedType fieldType) {
            UserValuesCreator.writeTo(object, values);
        }
    }
}
