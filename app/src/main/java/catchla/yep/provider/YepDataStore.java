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
        String PARENT_ID = "parent_id";
        String CONVERSATION_ID = "conversation_id";


        String CONTENT_PATH = "messages";
        Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, CONTENT_PATH);
        String[] COLUMNS = {};
    }

    interface Conversations extends BaseColumns {

        String CONTENT_PATH = "conversations";
        Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, CONTENT_PATH);
        String[] COLUMNS = {};
    }

    interface Friends extends BaseColumns {

        String CONTENT_PATH = "friends";
        Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, CONTENT_PATH);
        String[] COLUMNS = {};
    }

    interface ContactFriends extends BaseColumns {

    }

}
