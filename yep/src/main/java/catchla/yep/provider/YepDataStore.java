package catchla.yep.provider;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

import org.mariotaku.sqliteqb.library.DataType;

import catchla.yep.BuildConfig;

/**
 * Created by mariotaku on 15/7/2.
 */
public interface YepDataStore {

    Uri BASE_CONTENT_URI = new Uri.Builder().scheme(ContentResolver.SCHEME_CONTENT)
            .authority(BuildConfig.APPLICATION_ID).build();

    interface Messages extends BaseColumns {
        String ACCOUNT_ID = "account_id";
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
        String OUTGOING = "outgoing";
        String LATITUDE = "latitude";
        String LONGITUDE = "longitude";
        String MEDIA_TYPE = "media_type";
        String ATTACHMENTS = "attachments";
        String LOCAL_METADATA = "local_metadata";

        String CONTENT_PATH = "messages";
        Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, CONTENT_PATH);

        String TABLE_NAME = "messages";

        String[] COLUMNS = {_ID, ACCOUNT_ID, MESSAGE_ID, RECIPIENT_ID, TEXT_CONTENT, CREATED_AT,
                SENDER, RECIPIENT_TYPE, CIRCLE, PARENT_ID, CONVERSATION_ID, STATE, OUTGOING,
                LATITUDE, LONGITUDE, MEDIA_TYPE, ATTACHMENTS, LOCAL_METADATA};
        String[] TYPES = {DataType.INTEGER_PRIMARY_KEY, DataType.TEXT, DataType.TEXT, DataType.TEXT,
                DataType.TEXT, DataType.INTEGER, DataType.TEXT, DataType.TEXT, DataType.TEXT, DataType.TEXT,
                DataType.TEXT, DataType.TEXT, DataType.INTEGER, DataType.REAL, DataType.REAL, DataType.TEXT,
                DataType.TEXT, DataType.TEXT};

        interface MessageState {
            String READ = "read";
            String UNREAD = "unread";
            String SENDING = "sending";
            String FAILED = "failed";
        }
    }

    interface Conversations extends BaseColumns {

        String CONTENT_PATH = "conversations";
        String TABLE_NAME = "conversations";
        Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, CONTENT_PATH);


        String ACCOUNT_ID = "account_id";
        String CONVERSATION_ID = "conversation_id";
        String TEXT_CONTENT = "text_content";
        String USER = "user";
        String CIRCLE = "circle";
        String UPDATED_AT = "updated_at";
        String RECIPIENT_TYPE = "recipient_type";
        String MEDIA_TYPE = "media_type";
        String SENDER = "sender";

        String[] COLUMNS = {_ID, ACCOUNT_ID, CONVERSATION_ID, TEXT_CONTENT, USER, CIRCLE,
                RECIPIENT_TYPE, UPDATED_AT, MEDIA_TYPE, SENDER};
        String[] TYPES = {DataType.INTEGER_PRIMARY_KEY, DataType.TEXT, DataType.TEXT, DataType.TEXT,
                DataType.TEXT, DataType.TEXT, DataType.TEXT, DataType.INTEGER,
                DataType.TEXT, DataType.TEXT};
    }

    interface Friendships extends Users {

        String CONTENT_PATH = "friendships";
        Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, CONTENT_PATH);
        String TABLE_NAME = "friendships";

    }

    interface Circles extends BaseColumns {

        String ACCOUNT_ID = "account_id";
        String CIRCLE_ID = "circle_id";
        String NAME = "name";
        String TOPIC_ID = "topic_id";
        String TOPIC = "topic";
        String CREATED_AT = "created_at";
        String UPDATED_AT = "updated_at";
        String ACTIVE = "active";
        String KIND = "kind";

        String[] COLUMNS = {_ID, CIRCLE_ID, NAME, TOPIC_ID, TOPIC, CREATED_AT, UPDATED_AT, ACTIVE,
                KIND};

        String[] TYPES = {DataType.INTEGER_PRIMARY_KEY, DataType.TEXT, DataType.TEXT, DataType.TEXT,
                DataType.TEXT, DataType.INTEGER, DataType.INTEGER, DataType.INTEGER, DataType.TEXT};

        String TABLE_NAME = "circles";
        String CONTENT_PATH = "circles";
        Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, CONTENT_PATH);
    }

    interface Users extends BaseColumns {
        String ACCOUNT_ID = "account_id";
        String USER_ID = "user_id";
        String FRIEND_ID = "friend_id";
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

        String[] COLUMNS = {_ID, ACCOUNT_ID, USER_ID, FRIEND_ID, USERNAME, NICKNAME, INTRODUCTION, AVATAR_URL, MOBILE,
                PHONE_CODE, CONTACT_NAME, LEARNING_SKILLS, MASTER_SKILLS, PROVIDERS};
        String[] TYPES = {DataType.INTEGER_PRIMARY_KEY, DataType.TEXT, DataType.TEXT, DataType.TEXT, DataType.TEXT,
                DataType.TEXT, DataType.TEXT, DataType.TEXT, DataType.TEXT, DataType.TEXT,
                DataType.TEXT, DataType.TEXT, DataType.TEXT, DataType.TEXT};

    }

    interface ContactFriends extends Users {

    }

}
