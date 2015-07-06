package catchla.yep.loader;

import android.accounts.Account;
import android.content.Context;

import catchla.yep.model.Conversation;

/**
 * Created by mariotaku on 14-8-5.
 */
public class ConversationsLoader extends ObjectCursorLoader<Conversation> {

    public ConversationsLoader(Context context, Account account) {
        super(context, Conversation.Indices.class);
    }

}
