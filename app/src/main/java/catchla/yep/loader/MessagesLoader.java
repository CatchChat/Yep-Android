package catchla.yep.loader;

import android.accounts.Account;
import android.content.Context;

import catchla.yep.model.Conversation;
import catchla.yep.model.Message;

/**
 * Created by mariotaku on 15/5/29.
 */
public class MessagesLoader extends ObjectCursorLoader<Message> {
    private final Conversation mConversation;

    public MessagesLoader(final Context context, final Account account, final Conversation conversation) {
        super(context, Message.CursorIndices.class);
        mConversation = conversation;
    }


}
