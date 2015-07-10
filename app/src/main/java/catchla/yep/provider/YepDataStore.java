package catchla.yep.provider;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;

import catchla.yep.model.ObjectCursor;
import catchla.yep.model.User;

/**
 * Created by mariotaku on 15/7/2.
 */
public interface YepDataStore {

    String AUTHORITY = "catchla.yep";
    Uri BASE_CONTENT_URI = new Uri.Builder().scheme(ContentResolver.SCHEME_CONTENT)
            .authority(AUTHORITY).build();

    interface Messages extends BaseColumns {
        String MESSAGE_ID = "message_id";
        String RECIPIENT_ID = "recipient_id";
        String TEXT_CONTENT = "text_content";
        String CREATED_AT = "created_at";
        String SENDER = "sender";
        String RECIPIENT_TYPE = "recipient_type";
        String CIRCLE = "circle";
        String PARENT_ID = "parent_id";
        String CONVERSATION_ID = "conversation_id";
        String STATE = "state";

        String CONTENT_PATH = "messages";
        Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, CONTENT_PATH);
        String[] COLUMNS = {};
    }

    interface Conversations extends BaseColumns {

        String CONTENT_PATH = "conversations";
        Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, CONTENT_PATH);
        String CONVERSATION_ID = "conversation_id";
        String TEXT = "text";
        String RECIPIENT = "recipient";
        String CIRCLE = "circle";
        String UPDATED_AT = "updated_at";
        String RECIPIENT_TYPE = "recipient_type";

        String[] COLUMNS = {_ID, CONVERSATION_ID, TEXT, CIRCLE, RECIPIENT, RECIPIENT_TYPE, UPDATED_AT};
    }

    interface Friendships extends BaseColumns {

        String CONTENT_PATH = "friendships";
        Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, CONTENT_PATH);
        String[] COLUMNS = {};
    }

    interface Users extends BaseColumns {
        String USER_ID = "user_id";
        String USERNAME = "username";
        String NICKNAME = "nickname";
        String INTRODUCTION = "introduction";
        String AVATAR_URL = "avatar_url";
        String MOBILE = "mobile";
        String PHONE_CODE = "phone_code";
        String CONTACT_NAME = "contact_name";
        String LEARNING_SKILLS = "learning_skill";
        String MASTER_SKILLS = "master_skills";
        String PROVIDERS = "providers";

        String[] COLUMNS = {USER_ID, USERNAME, NICKNAME, INTRODUCTION, AVATAR_URL, MOBILE, PHONE_CODE,
                CONTACT_NAME, LEARNING_SKILLS, MASTER_SKILLS, PROVIDERS};

        class Indices extends ObjectCursor.CursorIndices<User> {

            public Indices(@NonNull final Cursor cursor) {
                super(cursor);
            }

            @Override
            public User newObject(final Cursor cursor) {
                return new User();
            }
        }
    }

    interface ContactFriends extends Users {

    }

}
