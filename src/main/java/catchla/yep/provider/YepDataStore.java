package catchla.yep.provider;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

import catchla.yep.BuildConfig;

/**
 * Created by mariotaku on 15/7/2.
 */
public interface YepDataStore {

    Uri BASE_CONTENT_URI = new Uri.Builder().scheme(ContentResolver.SCHEME_CONTENT)
            .authority(BuildConfig.APPLICATION_ID).build();
    String TYPE_PRIMARY_KEY = "INTEGER PRIMARY KEY AUTOINCREMENT";

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
        String RANDOM_ID = "random_id";
        String LATITUDE = "latitude";
        String LONGITUDE = "longitude";
        String MEDIA_TYPE = "media_type";
        String ATTACHMENTS = "attachments";
        String LOCAL_METADATA = "local_metadata";

        String CONTENT_PATH = "messages";
        Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, CONTENT_PATH);

        String TABLE_NAME = "messages";

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
        String LAST_SEEN_AT = "last_seen_at";
        String RECIPIENT_TYPE = "recipient_type";
        String MEDIA_TYPE = "media_type";
        String SENDER = "sender";

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
        String AVATAR_THUMB_URL = "avatar_thumb_url";
        String MOBILE = "mobile";
        String PHONE_CODE = "phone_code";
        String CONTACT_NAME = "contact_name";
        String LEARNING_SKILLS = "learning_skill";
        String MASTER_SKILLS = "master_skills";
        String PROVIDERS = "providers";

    }

}
