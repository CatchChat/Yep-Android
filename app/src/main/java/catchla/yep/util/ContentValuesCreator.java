package catchla.yep.util;

import android.content.ContentValues;

import catchla.yep.model.Friendship;
import catchla.yep.model.Provider;
import catchla.yep.model.Skill;
import catchla.yep.model.User;
import catchla.yep.provider.YepDataStore.Friendships;
import catchla.yep.provider.YepDataStore.Users;

/**
 * Created by mariotaku on 15/8/6.
 */
public class ContentValuesCreator {

    public static ContentValues fromUser(final User user) {
        final ContentValues values = new ContentValues();
        values.put(Users.FRIEND_ID, user.getId());
        values.put(Users.CONTACT_NAME, user.getContactName());
        values.put(Users.NICKNAME, user.getNickname());
        values.put(Users.USERNAME, user.getUsername());
        values.put(Users.INTRODUCTION, user.getIntroduction());
        values.put(Users.MOBILE, user.getMobile());
        values.put(Users.PHONE_CODE, user.getPhoneCode());
        values.put(Users.AVATAR_URL, user.getAvatarUrl());
        values.put(Users.LEARNING_SKILLS, JsonSerializer.serialize(user.getLearningSkills(), Skill.class));
        values.put(Users.MASTER_SKILLS, JsonSerializer.serialize(user.getMasterSkills(), Skill.class));
        values.put(Users.PROVIDERS, JsonSerializer.serialize(user.getProviders(), Provider.class));
        return values;
    }

    public static ContentValues fromFriendship(final Friendship friendship) {
        final ContentValues values = new ContentValues();
        final User friend = friendship.getFriend();
        values.put(Friendships.USER_ID, friendship.getUserId());

        values.put(Friendships.FRIEND_ID, friend.getId());
        values.put(Friendships.CONTACT_NAME, friend.getContactName());
        values.put(Friendships.NICKNAME, friend.getNickname());
        values.put(Friendships.USERNAME, friend.getUsername());
        values.put(Friendships.INTRODUCTION, friend.getIntroduction());
        values.put(Friendships.MOBILE, friend.getMobile());
        values.put(Friendships.PHONE_CODE, friend.getPhoneCode());
        values.put(Friendships.AVATAR_URL, friend.getAvatarUrl());
        values.put(Friendships.LEARNING_SKILLS, JsonSerializer.serialize(friend.getLearningSkills(), Skill.class));
        values.put(Friendships.MASTER_SKILLS, JsonSerializer.serialize(friend.getMasterSkills(), Skill.class));
        values.put(Friendships.PROVIDERS, JsonSerializer.serialize(friend.getProviders(), Provider.class));
        return values;
    }
}
