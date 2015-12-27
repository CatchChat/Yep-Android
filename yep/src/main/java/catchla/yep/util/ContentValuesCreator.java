package catchla.yep.util;

import android.content.ContentValues;

import catchla.yep.model.Circle;
import catchla.yep.model.CircleValuesCreator;
import catchla.yep.model.Friendship;
import catchla.yep.model.Message;
import catchla.yep.model.MessageValuesCreator;
import catchla.yep.model.NewMessage;
import catchla.yep.model.User;
import catchla.yep.model.UserValuesCreator;
import catchla.yep.provider.YepDataStore.Circles;
import catchla.yep.provider.YepDataStore.Friendships;
import catchla.yep.provider.YepDataStore.Messages;
import catchla.yep.provider.YepDataStore.Users;

/**
 * Created by mariotaku on 15/8/6.
 */
public class ContentValuesCreator {

    public static ContentValues fromUser(final User user, final String accountId) {
        final ContentValues values = UserValuesCreator.create(user);
        values.put(Users.ACCOUNT_ID, accountId);
        return values;
    }

    public static ContentValues fromFriendship(final Friendship friendship, final String accountId) {
        final User friend = friendship.getFriend();
        final ContentValues values = fromUser(friend, accountId);
        values.put(Friendships.ACCOUNT_ID, accountId);
        values.put(Friendships.USER_ID, friendship.getUserId());
        return values;
    }

    public static ContentValues friendshipFromUser(final User friend, final String accountId) {
        return fromUser(friend, accountId);
    }

    public static ContentValues fromMessage(final Message message, final String accountId) {
        final ContentValues values = new ContentValues();
        values.put(Messages.ACCOUNT_ID, accountId);
        MessageValuesCreator.writeTo(message, values);
        return values;
    }

    public static ContentValues fromNewMessage(final NewMessage newMessage, final String accountId) {
        final ContentValues values = new ContentValues();
        values.put(Messages.ACCOUNT_ID, accountId);
        values.put(Messages.CONVERSATION_ID, newMessage.conversationId());
        values.put(Messages.CREATED_AT, newMessage.createdAt());
        values.put(Messages.PARENT_ID, newMessage.parentId());
        values.put(Messages.RECIPIENT_ID, newMessage.recipientId());
        values.put(Messages.RECIPIENT_TYPE, newMessage.recipientType());
        values.put(Messages.CIRCLE, JsonSerializer.serialize(newMessage.circle(), Circle.class));
        values.put(Messages.SENDER, JsonSerializer.serialize(newMessage.sender(), User.class));
        values.put(Messages.TEXT_CONTENT, newMessage.textContent());
        values.put(Messages.MEDIA_TYPE, newMessage.mediaType());
        values.put(Messages.LATITUDE, newMessage.latitude());
        values.put(Messages.LONGITUDE, newMessage.longitude());
        return values;
    }

    public static ContentValues fromCircle(final Circle circle, final String accountId) {
        final ContentValues values = CircleValuesCreator.create(circle);
        values.put(Circles.ACCOUNT_ID, accountId);
        return values;
    }
}
