package catchla.yep.loader;

import android.accounts.Account;
import android.content.Context;

import catchla.yep.model.Conversation;
import catchla.yep.provider.YepDataStore.Conversations;

/**
 * Created by mariotaku on 14-8-5.
 */
public class ConversationsLoader extends ObjectCursorLoader<Conversation> {

    public ConversationsLoader(Context context, Account account) {
        super(context, Conversation.Indices.class, Conversations.CONTENT_URI, Conversations.COLUMNS,
                null, null, null);
    }

}
