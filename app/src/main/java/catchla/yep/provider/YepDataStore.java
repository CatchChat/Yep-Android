package catchla.yep.provider;

import android.provider.BaseColumns;

/**
 * Created by mariotaku on 15/7/2.
 */
public interface YepDataStore {


    interface Messages extends BaseColumns {
        String MESSAGE_ID = "message_id";
        String PARENT_ID = "parent_id";
        String CONVERSATION_ID = "conversation_id";
    }

    interface Conversations extends BaseColumns {

    }

    interface Friends extends BaseColumns {

    }

    interface ContactFriends extends BaseColumns {

    }

}
