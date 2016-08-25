package catchla.yep.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.bluelinelabs.logansquare.annotation.OnJsonParseComplete;
import com.hannesdorfmann.parcelableplease.annotation.ParcelablePlease;
import com.hannesdorfmann.parcelableplease.annotation.ParcelableThisPlease;

import org.mariotaku.library.objectcursor.annotation.AfterCursorObjectCreated;
import org.mariotaku.library.objectcursor.annotation.CursorField;
import org.mariotaku.library.objectcursor.annotation.CursorObject;

import java.util.Date;

import catchla.yep.model.util.LoganSquareCursorFieldConverter;
import catchla.yep.model.util.TimestampToDateConverter;
import catchla.yep.provider.YepDataStore.Friendships;

/**
 * Created by mariotaku on 15/5/28.
 */
@JsonObject
@CursorObject(tableInfo = true, valuesCreator = true)
@ParcelablePlease
public class Friendship implements Parcelable {

    @CursorField(Friendships.ACCOUNT_ID)
    String accountId;

    @JsonField(name = "id")
    @CursorField(Friendships.FRIENDSHIP_ID)
    String id;
    @JsonField(name = "name")
    @CursorField(Friendships.NAME)
    String name;
    @JsonField(name = "remarked_name")
    @CursorField(Friendships.REMARKED_NAME)
    String remarkedName;
    @JsonField(name = "contact_name")
    @CursorField(Friendships.CONTACT_NAME)
    String contactName;

    @JsonField(name = "favored")
    boolean favored;

    @CursorField(Friendships.USER_ID)
    String userId;

    @JsonField(name = "friend")
    @CursorField(value = "friend", converter = LoganSquareCursorFieldConverter.class)
    User friend;

    @ParcelableThisPlease
    @CursorField(value = Friendships.USER_UPDATED_AT, converter = TimestampToDateConverter.class, type = CursorField.INTEGER)
    Date userUpdatedAt;


    public static final Creator<Friendship> CREATOR = new Creator<Friendship>() {
        @Override
        public Friendship createFromParcel(Parcel in) {
            final Friendship friendship = new Friendship();
            FriendshipParcelablePlease.readFromParcel(friendship, in);
            return friendship;
        }

        @Override
        public Friendship[] newArray(int size) {
            return new Friendship[size];
        }
    };

    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public boolean isFavored() {
        return favored;
    }

    public User getFriend() {
        return friend;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(final String accountId) {
        this.accountId = accountId;
    }

    public String getContactName() {
        return contactName;
    }

    public String getRemarkedName() {
        return remarkedName;
    }

    public String getName() {
        return name;
    }

    public Date getUserUpdatedAt() {
        return userUpdatedAt;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        FriendshipParcelablePlease.writeToParcel(this, dest, flags);
    }

    @OnJsonParseComplete
    void onJsonParseComplete() {
        userId = friend.getId();
        userUpdatedAt = friend.getUpdatedAt();
        if (friend.contactName == null) {
            friend.setContactName(contactName);
        }
    }

    @AfterCursorObjectCreated
    void afterCursorObjectCreated() {
        userId = friend.getId();
        friend.setRemarkedName(remarkedName);
        if (friend.contactName == null) {
            friend.setContactName(contactName);
        }
    }

}
