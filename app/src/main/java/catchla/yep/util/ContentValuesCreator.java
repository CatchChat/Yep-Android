package catchla.yep.util;

import android.content.ContentValues;

import catchla.yep.model.Friendship;
import catchla.yep.model.Skill;
import catchla.yep.model.User;
import catchla.yep.provider.YepDataStore.Friendships;

/**
 * Created by mariotaku on 15/8/6.
 */
public class ContentValuesCreator {
    public static ContentValues fromFriendships(final Friendship friendship) {
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
        return values;
    }
}
