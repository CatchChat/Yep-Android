package catchla.yep.provider;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

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

    interface ContactFriends extends BaseColumns {

    }

}
